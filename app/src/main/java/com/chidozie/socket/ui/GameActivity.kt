package com.chidozie.socket.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.chidozie.socket.R
import com.chidozie.socket.adapter.AnswerLevelsAdapter
import com.chidozie.socket.databinding.ActivityGameBinding
import com.chidozie.socket.model.*
import com.chidozie.socket.util.*
import io.socket.client.Socket
import io.socket.emitter.Emitter

class GameActivity : AppCompatActivity() {

    private lateinit var game: StartGame
    private lateinit var player: Player
    private lateinit var userLevelsAdapter: AnswerLevelsAdapter
    private lateinit var opponentLevelsAdapter: AnswerLevelsAdapter
    private lateinit var binding: ActivityGameBinding
    private lateinit var socket: Socket
    private var index = 0
    private lateinit var db: Db

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_game)

        db = Db()

        userLevelsAdapter = AnswerLevelsAdapter(db.answers)
        opponentLevelsAdapter = AnswerLevelsAdapter(db.answers)

        socket = SocketManager.getGameSocket()

        player = getExtra(EXTRA_PLAYER)
        game = getExtra(EXTRA_GAME)

        binding.userRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = userLevelsAdapter
        }
        binding.opponentRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = opponentLevelsAdapter
        }

        binding.opponentTextView.text = game.otherPlayer.name

        binding.submitButton.setOnClickListener {
            val text = binding.answerEditText.text
            if (text.isNotBlank()) {
                onSubmitAnswer(text.toString())
                fetchNextQuestion()
            }
        }

        socket
            .on(ServerEvent.ABORT_GAME, onAbortGame())
            .on(ServerEvent.UPDATE_OPPONENT_ANSWERS, onUpdateOpponentAnswers())
            .on(ServerEvent.SHOW_RESULT, onShowResult())

        populateQuestion(db.getQuestion(index)!!)
    }

    override fun onDestroy() {
        super.onDestroy()
        val data = AbortEvent(
            id = player.id,
            gameId = game.gameId
        )
        socket.emit(ClientEvent.ABORT_GAME, data.toJson())
    }

    private fun onSubmitAnswer(answer: String) {
        db.addAnswer(index, answer)

        val data = UpdateAnswers(player.id, game.gameId, db.getUserAnswers())
        socket.emit(
            ClientEvent.UPDATE_MY_ANSWERS, data.toJson()
        )
        userLevelsAdapter.update(data.answers)
    }

    private fun fetchNextQuestion() {
        val question = db.getQuestion(index + 1)
        if (question == null) {
            onFinished()
        } else {
            populateQuestion(question)
            index += 1
        }
    }

    private fun populateQuestion(question: String) {
        binding.questionTextView.text = question
        binding.answerEditText.setText("")
    }

    private fun onFinished() {
        val data = FinishEvent(player.id, game.gameId, db.getNoOfCorrectAnswers())
        socket.emit(ClientEvent.FINISH, data.toJson())

        AlertDialog.Builder(this)
            .setMessage("Waiting for ${game.otherPlayer.name}...")
            .create()
            .show()
    }

    private fun onUpdateOpponentAnswers(): Emitter.Listener {
        return Emitter.Listener {
            runOnUiThread {
                val data: UpdateAnswers = it.toObjectNonNull()
                opponentLevelsAdapter.update(data.answers)
            }
        }
    }

    private fun onShowResult(): Emitter.Listener {
        return Emitter.Listener {
            runOnUiThread {
                val won: Boolean = it.toObjectNonNull()
                val intent = ResultActivity.newIntent(this, won)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun onAbortGame(): Emitter.Listener {
        return Emitter.Listener {
            runOnUiThread {
                finish()
                Toast.makeText(this, "Game aborted", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private object ClientEvent {
        const val UPDATE_MY_ANSWERS = "UPDATE_MY_ANSWERS"
        const val FINISH = "FINISH"
        const val ABORT_GAME = "ABORT_GAME"
    }

    private object ServerEvent {
        const val ABORT_GAME = "ABORT_GAME"
        const val UPDATE_OPPONENT_ANSWERS = "UPDATE_OPPONENT_ANSWERS"
        const val SHOW_RESULT = "SHOW_RESULT"
    }

    companion object {

        private const val EXTRA_GAME = "EXTRA_GAME"
        private const val EXTRA_PLAYER = "EXTRA_PLAYER"

        fun newIntent(context: Context, player: Player, game: StartGame): Intent {
            return Intent(context, GameActivity::class.java).apply {
                putExtra(
                    EXTRA_PLAYER, player.toJson()
                )
                putExtra(
                    EXTRA_GAME, game.toJson()
                )
            }
        }
    }

}
