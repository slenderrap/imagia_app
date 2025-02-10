package com.project.imagia.ui.history

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.project.imagia.databinding.DialogTextBinding
import com.project.imagia.databinding.ItemHistoryBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HistoryAdapter(private var historyList: List<HistoryItem>) :
    RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    class HistoryViewHolder(private val binding: ItemHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: HistoryItem) {
            binding.textAnswer.text =
                item.answer.take(50) + if (item.answer.length > 50) "..." else ""
            binding.textDate.text = formatDate(item.promtDate)


            binding.root.setOnClickListener {
                showAllText(binding.root.context, item.answer, binding.textDate.text.toString())
            }
        }

        private fun formatDate(promtDate: String): String? {
            return try {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                val outputFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault())
                val date = inputFormat.parse(promtDate)
                outputFormat.format(date ?: Date())
            } catch (e: Exception) {
                promtDate
            }
        }

        private fun showAllText(context: Context?, answer: String, date: String) {
            val dialogView = DialogTextBinding.inflate(LayoutInflater.from(context))
            val dialog = AlertDialog.Builder(context)
                .setTitle(date)
                .setView(dialogView.root)
                .setPositiveButton("Tancar", null)
                .create()
            dialogView.fullTextView.text = answer
            dialog.show()
        }
    }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val binding = ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(historyList[position])
    }

    override fun getItemCount() = historyList.size

    fun updateData(newList: List<HistoryItem>) {
        historyList = newList
        notifyDataSetChanged()
    }
}
