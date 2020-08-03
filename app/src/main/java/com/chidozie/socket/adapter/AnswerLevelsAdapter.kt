package com.chidozie.socket.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chidozie.socket.databinding.ItemAnswerLevelBinding

class AnswerLevelsAdapter(
    private val correctAnswers: List<String>
) : RecyclerView.Adapter<AnswerLevelsAdapter.ViewHolder>() {

    private var answers: List<String?> = emptyList()

    fun update(answers: List<String?>) {
        this.answers = answers
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(ItemAnswerLevelBinding.inflate(inflater, parent, false))
    }

    override fun getItemCount(): Int {
        return correctAnswers.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(answers.getOrNull(position)?.trim(), correctAnswers[position].trim())
    }

    inner class ViewHolder(
        view: ItemAnswerLevelBinding
    ) : RecyclerView.ViewHolder(view.root) {

        fun bind(item: String?, correct: String) {
            when (item) {
                null -> itemView.setBackgroundColor(Color.GRAY)
                correct -> itemView.setBackgroundColor(Color.GREEN)
                else -> itemView.setBackgroundColor(Color.RED)
            }

        }

    }

}
