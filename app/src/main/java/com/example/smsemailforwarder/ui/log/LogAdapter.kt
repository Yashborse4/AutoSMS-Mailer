package com.example.smsemailforwarder.ui.log

import android.graphics.Color
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.smsemailforwarder.R
import com.example.smsemailforwarder.data.SmsEmailLog

class LogAdapter(private val onClick: (Long) -> Unit) : RecyclerView.Adapter<LogAdapter.VH>() {
    private val items = mutableListOf<SmsEmailLog>()

    fun currentItems(): List<SmsEmailLog> = items

    fun submit(list: List<SmsEmailLog>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_log, parent, false)
        return VH(v, onClick)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    class VH(itemView: View, val onClick: (Long) -> Unit) : RecyclerView.ViewHolder(itemView) {
        private val tvHeader = itemView.findViewById<TextView>(R.id.tvItemHeader)
        private val tvSub = itemView.findViewById<TextView>(R.id.tvItemSub)
        private val tvStatus = itemView.findViewById<TextView>(R.id.tvItemStatus)
        private var currentId: Long = -1

        init {
            itemView.setOnClickListener { if (currentId > 0) onClick(currentId) }
        }

        fun bind(item: SmsEmailLog) {
            currentId = item.id
            val ts = DateFormat.format("yyyy-MM-dd HH:mm:ss", item.timestamp)
            tvHeader.text = "${item.from} • ${ts}"
            tvSub.text = item.body
            tvStatus.text = item.status + (item.error?.let { ": $it" } ?: "")
            tvStatus.setTextColor(
                when (item.status) {
                    "SENT" -> 0xFF2E7D32.toInt() // green
                    "FAILED" -> 0xFFC62828.toInt() // red
                    else -> Color.DKGRAY
                }
            )
        }
    }
}
