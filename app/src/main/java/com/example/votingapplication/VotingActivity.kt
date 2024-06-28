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
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.example.votingapplication.RoomDb.AppDatabase
import com.example.votingapplication.RoomDb.Vote
import com.example.votingapplication.RoomDb.VoteDao
import kotlinx.coroutines.launch


class VotingActivity : AppCompatActivity() {

    private lateinit var voteDao: VoteDao
    private var userId: Int = 0
    lateinit var voteButton: androidx.appcompat.widget.AppCompatButton
    lateinit var backbtn :androidx.appcompat.widget.AppCompatButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_voting)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        voteButton = findViewById(R.id.Vote)
        backbtn = findViewById(R.id.back)
        backbtn.setOnClickListener{
            val intent = Intent (this@VotingActivity, MainActivity::class.java)
           startActivity(intent)
        }

        userId = intent.getIntExtra("USER_ID", 0)
        val db = AppDatabase.getDatabase(applicationContext)
        voteDao = db.voteDao()

        lifecycleScope.launch {
            try {
                val hasVoted = voteDao.hasVoted(userId)
                if (hasVoted > 0) {
                    // User has already voted
                    val vote = voteDao.getVoteByUserId(userId)
                    if (vote != null) {
                        disableAllRadioButtons(vote.candidate)
                        // Freeze the radio button corresponding to the voted candidate
                        freezeVotedCandidate(vote.candidate)
                    }
                    Toast.makeText(this@VotingActivity, "You have already voted", Toast.LENGTH_SHORT).show()
                    voteButton.isVisible = false
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

    private fun freezeVotedCandidate(candidate: String) {
        val radioGroup = findViewById<RadioGroup>(R.id.radioGroup)
        for (i in 0 until radioGroup.childCount) {
            val radioButton = radioGroup.getChildAt(i) as RadioButton
            if (radioButton.text == candidate) {
                radioButton.isChecked = true
                radioButton.isEnabled = false
            }
        }
    }

    private fun disableAllRadioButtons(votedCandidate: String) {
        val radioGroup = findViewById<RadioGroup>(R.id.radioGroup)
        for (i in 0 until radioGroup.childCount) {
            val radioButton = radioGroup.getChildAt(i) as RadioButton
            if (radioButton.text == votedCandidate) {
                radioButton.isChecked = true
            }
            radioButton.isEnabled = false
        }
    }
}

