package com.chidozie.socket.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.chidozie.socket.R
import com.chidozie.socket.databinding.ActivityResultBinding
import com.chidozie.socket.util.getExtra
import com.chidozie.socket.util.toJson

class ResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_result)

        val won: Boolean = getExtra(EXTRA_WON)

        val text = when {
            won -> "You won!"
            else -> "You lost!"
        }

        val emoji = when {
            won -> {
                "✷‿✷"
            }
            else -> {
                "ಠ︵ಠ"
            }
        }


        binding.resultTextView.text = text
        binding.emojiTextView.text = emoji
        binding.finishButton.setOnClickListener {
            finish()
        }

    }


    companion object {

        private const val EXTRA_WON = "EXTRA_WON"

        fun newIntent(context: Context, won: Boolean): Intent {
            return Intent(context, ResultActivity::class.java).apply {
                putExtra(
                    EXTRA_WON, won.toJson()
                )
            }
        }
    }

}
