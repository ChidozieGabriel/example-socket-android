package com.chidozie.socket.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isInvisible
import androidx.recyclerview.widget.RecyclerView
import com.chidozie.socket.databinding.ItemPlayerBinding
import com.chidozie.socket.model.Player

class PlayerAdapter : RecyclerView.Adapter<PlayerAdapter.ViewHolder>() {

    private var items: List<Player> = emptyList()
    private var currentPlayer: Player? = null
    private var listener: Listener? = null

    interface Listener {
        fun onRequestClicked(item: Player)
    }

    fun setListener(listener: Listener) {
        this.listener = listener
    }

    fun setCurrentPlayer(player: Player) {
        currentPlayer = player
        update(items)
    }

    fun getCurrentPlayer(): Player? {
        return currentPlayer
    }

    fun update(items: List<Player>) {
        this.items = items.sortedByDescending {
            it.id == currentPlayer?.id
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(ItemPlayerBinding.inflate(inflater, parent, false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    inner class ViewHolder(
        private val view: ItemPlayerBinding
    ) : RecyclerView.ViewHolder(view.root) {

        fun bind(item: Player) {
            view.nameTextView.text = item.name
            view.requestButton.apply {
                isInvisible = item.id == currentPlayer?.id
                setOnClickListener {
                    listener?.onRequestClicked(item)
                }
            }
        }

    }

}
