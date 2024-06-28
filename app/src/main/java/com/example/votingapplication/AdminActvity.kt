package com.example.votingapplication

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.votingapplication.RoomDb.AppDatabase
import com.example.votingapplication.RoomDb.VoteDao
import kotlinx.coroutines.launch

class AdminActvity : AppCompatActivity() {
    private lateinit var voteDao: VoteDao
    lateinit var logout: androidx.appcompat.widget.AppCompatButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_admin_actvity)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        logout = findViewById(R.id.vote_button)

        logout.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }


        val db = AppDatabase.getDatabase(applicationContext)
        voteDao = db.voteDao()


        updateVoteCounts()
    }

    private fun updateVoteCounts() {
        lifecycleScope.launch {
            // Fetch and display counts
            val voteCounts = voteDao.getVoteCounts()

            for (voteCount in voteCounts) {
                when (voteCount.candidate) {
                    "Shivsena" -> updateCount(R.id.shivsena_count, voteCount.voteCount)
                    "Congress" -> updateCount(R.id.congress_count, voteCount.voteCount)
                    "BJP" -> updateCount(R.id.bjp_count, voteCount.voteCount)
                    "AAP" -> updateCount(R.id.aap_count, voteCount.voteCount)
                    // Handle other candidates as needed
                }
            }
        }
    }

    private fun updateCount(viewId: Int, count: Int) {
        findViewById<TextView>(viewId).text = count.toString()
    }
}