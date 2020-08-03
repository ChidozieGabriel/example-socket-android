package com.chidozie.socket.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.chidozie.socket.R
import com.chidozie.socket.adapter.PlayerAdapter
import com.chidozie.socket.databinding.ActivityAvailablePlayersBinding
import com.chidozie.socket.model.Player
import com.chidozie.socket.model.StartGame
import com.chidozie.socket.util.SocketManager
import com.chidozie.socket.util.getExtra
import com.chidozie.socket.util.toJson
import com.chidozie.socket.util.toObjectNonNull
import io.socket.client.Socket
import io.socket.emitter.Emitter

class AvailablePlayersActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAvailablePlayersBinding
    private lateinit var adapter: PlayerAdapter
    private lateinit var socket: Socket

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this,
            R.layout.activity_available_players
        )
        socket = SocketManager.getPlayersSocket()
        adapter = PlayerAdapter()

        adapter.setListener(object : PlayerAdapter.Listener {
            override fun onRequestClicked(item: Player) {
                socket.emit(
                    ClientEvent.CHALLENGE_PLAYER, item.id)
            }
        })

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = this@AvailablePlayersActivity.adapter
        }

        val name: String = getExtra(
            EXTRA_NAME
        )

        socket.emit(ClientEvent.CREATE_PLAYER, name)

        socket.on(
            ServerEvent.AVAILABLE_PLAYERS, onAvailablePlayers())
        socket.on(
            ServerEvent.PLAYER_CREATED, onPlayerCreated())
        socket.on(
            ServerEvent.PLAY_REQUEST, onPlayRequest())
        socket.on(
            ServerEvent.START_GAME, onStartGame())
    }

    private fun onPlayerCreated(): Emitter.Listener {
        return Emitter.Listener {
            runOnUiThread {
                adapter.setCurrentPlayer(it.toObjectNonNull())
            }
        }
    }

    private fun onAvailablePlayers(): Emitter.Listener {
        return Emitter.Listener {
            runOnUiThread {
                val players: List<Player> = it.toObjectNonNull()
                adapter.update(players)
            }
        }
    }

    private fun onPlayRequest(): Emitter.Listener {
        return Emitter.Listener {
            runOnUiThread {
                val player: Player = it.toObjectNonNull()
                AlertDialog.Builder(this)
                    .setMessage("Accept Request from ${player.name}")
                    .setPositiveButton(android.R.string.ok) { _, _ ->
                        socket.emit(
                            ClientEvent.ACCEPT_REQUEST, player.id)
                    }
                    .create()
                    .show()
            }
        }
    }

    private fun onStartGame(): Emitter.Listener {
        return Emitter.Listener {
            runOnUiThread {
                val game: StartGame = it.toObjectNonNull()
                val currentPlayer = adapter.getCurrentPlayer()
                if (currentPlayer != null) {
                    val intent =
                        GameActivity.newIntent(
                            this, currentPlayer, game
                        )
                    startActivity(intent)
                }
            }
        }
    }

    private object ClientEvent {
        const val CREATE_PLAYER = "CREATE_PLAYER"
        const val CHALLENGE_PLAYER = "CHALLENGE_PLAYER"
        const val ACCEPT_REQUEST = "ACCEPT_REQUEST"
    }

    private object ServerEvent {
        const val PLAYER_CREATED = "PLAYER_CREATED"
        const val AVAILABLE_PLAYERS = "AVAILABLE_PLAYERS"
        const val PLAY_REQUEST = "PLAY_REQUEST"
        const val START_GAME = "START_GAME"
    }

    companion object {

        private const val EXTRA_NAME = "EXTRA_NAME"

        fun newIntent(context: Context, name: String): Intent {
            return Intent(context, AvailablePlayersActivity::class.java).apply {
                putExtra(
                    EXTRA_NAME, name.toJson())
            }
        }

    }

}
