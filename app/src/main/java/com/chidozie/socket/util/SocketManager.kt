package com.chidozie.socket.util

import io.socket.client.IO
import io.socket.client.Manager
import io.socket.client.Socket
import io.socket.engineio.client.transports.WebSocket
import java.net.URI
import java.net.URISyntaxException

object SocketManager {

    private const val PORT = 9092
    private const val HOST = "192.168.1.100"
    private const val ADDRESS = "http://$HOST:$PORT"

    private var playersSocket: Socket? = null
    private var gameSocket: Socket? = null
    private val manager: Manager

    init {
        try {
            val opts = IO.Options()
            opts.transports = arrayOf(WebSocket.NAME)
            manager = Manager(
                URI(
                    ADDRESS
                ), opts
            )
        } catch (e: URISyntaxException) {
            throw RuntimeException(e)
        }
    }

    fun getPlayersSocket(): Socket {
        if (playersSocket == null) {
            playersSocket = manager.socket("/players")
            playersSocket?.connect()
        }

        return playersSocket!!
    }

    fun getGameSocket(): Socket {
        if (gameSocket == null) {
            gameSocket = manager.socket("/game")
            gameSocket?.connect()
        }

        return gameSocket!!
    }

}
