package com.example.votingapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.votingapplication.RoomDb.AppDatabase
import com.example.votingapplication.RoomDb.Vote
import com.example.votingapplication.RoomDb.VoteDao
import kotlinx.coroutines.launch

class VotingActivity : AppCompatActivity() {

    private lateinit var voteDao: VoteDao
    private var userId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_voting)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        userId = intent.getIntExtra("USER_ID", 0)
        val db = AppDatabase.getDatabase(applicationContext)
        voteDao = db.voteDao()

        lifecycleScope.launch {
            try {
                val hasVoted = voteDao.hasVoted(userId)
                if (hasVoted > 0) {
                    // User has already voted
                    Toast.makeText(this@VotingActivity, "You have already voted", Toast.LENGTH_SHORT).show()
                    finishAffinity()
                } else {
                    // User has not voted, allow voting
                    setupVoting()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@VotingActivity, "Error checking vote status: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setupVoting() {
        val radioGroup = findViewById<RadioGroup>(R.id.radioGroup)
        val voteButton = findViewById<Button>(R.id.Vote)
        val backButton = findViewById<Button>(R.id.back)

        backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        voteButton.setOnClickListener {
            val selectedRadioButtonId = radioGroup.checkedRadioButtonId
            if (selectedRadioButtonId == -1) {
                Toast.makeText(this@VotingActivity, "Please select a candidate", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val selectedCandidate = findViewById<RadioButton>(selectedRadioButtonId).text.toString()

            lifecycleScope.launch {
                try {
                    // Insert vote into database
                    val vote = Vote(0, userId, selectedCandidate)
                    voteDao.insert(vote)
                    Toast.makeText(this@VotingActivity, "Vote cast successfully", Toast.LENGTH_SHORT).show()
                    // After voting, show message and redirect back to MainActivity
                    val intent = Intent(this@VotingActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(this@VotingActivity, "Error casting vote: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
