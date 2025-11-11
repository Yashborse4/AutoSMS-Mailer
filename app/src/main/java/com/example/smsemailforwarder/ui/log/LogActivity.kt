package com.example.smsemailforwarder.ui.log

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.ItemTouchHelper
import com.example.smsemailforwarder.R
import com.example.smsemailforwarder.data.AppDatabase
import com.example.smsemailforwarder.data.SmsEmailLog
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.chip.Chip
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

class LogActivity : AppCompatActivity() {

    private val queryFlow = MutableStateFlow("")
    private val statusFlow = MutableStateFlow("ALL")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        val rv = findViewById<RecyclerView>(R.id.rvLogs)
        val adapter = LogAdapter { id ->
            startActivity(Intent(this, LogDetailActivity::class.java).putExtra(LogDetailActivity.EXTRA_ID, id))
        }
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = adapter

        val dao = AppDatabase.get(this).logDao()
        lifecycleScope.launch {
            statusFlow.flatMapLatest { status ->
                val q = queryFlow.debounce(200).distinctUntilChanged()
                q.flatMapLatest { query ->
                    val like = "%${query.trim()}%"
                    when (status) {
                        "SENT" -> dao.searchByStatus("SENT", like)
                        "FAILED" -> dao.searchByStatus("FAILED", like)
                        else -> dao.searchAll(like)
                    }
                }
            }.collectLatest { list -> adapter.submit(list) }
        }

        // Swipe to delete with undo
        val touchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(rv: RecyclerView, vh: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean = false
            override fun onSwiped(vh: RecyclerView.ViewHolder, dir: Int) {
                val position = vh.adapterPosition
                val item = adapter.currentItems().getOrNull(position)
                if (item != null) {
                    lifecycleScope.launch(Dispatchers.IO) {
                        dao.deleteById(item.id)
                    }
                    Snackbar.make(rv, "Deleted", Snackbar.LENGTH_LONG).setAction("Undo") {
                        lifecycleScope.launch(Dispatchers.IO) { dao.insert(item.copy(id = 0)) }
                    }.show()
                }
            }
        })
        touchHelper.attachToRecyclerView(rv)

        val chipAll = findViewById<Chip>(R.id.chipAll)
        val chipSent = findViewById<Chip>(R.id.chipSent)
        val chipFailed = findViewById<Chip>(R.id.chipFailed)
        chipAll.setOnClickListener { statusFlow.value = "ALL" }
        chipSent.setOnClickListener { statusFlow.value = "SENT" }
        chipFailed.setOnClickListener { statusFlow.value = "FAILED" }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_log, menu)
        val item = menu.findItem(R.id.action_search)
        val searchView = item.actionView as SearchView
        searchView.queryHint = getString(R.string.search_hint)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                queryFlow.value = query?.trim() ?: ""
                return true
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                queryFlow.value = newText?.trim() ?: ""
                return true
            }
        })
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) { finish(); return true }
        if (item.itemId == R.id.action_clear) {
            MaterialAlertDialogBuilder(this)
                .setTitle(R.string.confirm_clear_title)
                .setMessage(R.string.confirm_clear_msg)
                .setPositiveButton(R.string.ok) { _, _ ->
                    lifecycleScope.launch(Dispatchers.IO) { AppDatabase.get(this@LogActivity).logDao().deleteAll() }
                }
                .setNegativeButton(R.string.cancel, null)
                .show()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
