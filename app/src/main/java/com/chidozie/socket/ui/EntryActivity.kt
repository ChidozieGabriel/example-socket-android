package com.chidozie.socket.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.chidozie.socket.R
import com.chidozie.socket.databinding.ActivityEntryBinding

class EntryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEntryBinding

    private val tag = "com.chidozie.socket"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,
            R.layout.activity_entry
        )

        binding.logInButton.setOnClickListener {
            val name = binding.nameEditText.text.toString()
            if (name.isNotBlank()) {
                val intent = AvailablePlayersActivity.newIntent(this, name)
                startActivity(intent)
            }
        }
    }

}
