package com.example.votingapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.votingapplication.RoomDb.AppDatabase
import com.example.votingapplication.RoomDb.User
import com.example.votingapplication.RoomDb.UserDao
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var userDao: UserDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val db = AppDatabase.getDatabase(applicationContext)
        userDao = db.userDao()

        lifecycleScope.launch {
            ensureAdminUser()
        }

        val editUsername = findViewById<EditText>(R.id.editusername)
        val editPassword = findViewById<EditText>(R.id.editpassword)
        val loginButton = findViewById<Button>(R.id.button)
        val registerbtn = findViewById<androidx.appcompat.widget.AppCompatButton>(R.id.Register)

        registerbtn.setOnClickListener {
            val intent = Intent(this, RegistrationActivity::class.java)
            startActivity(intent)
        }

        loginButton.setOnClickListener {
            val username = editUsername.text.toString()
            val password = editPassword.text.toString()

            if (username.isNotEmpty() && password.isNotEmpty()) {
                lifecycleScope.launch {
                    val admin = userDao.getAdmin(username, password)
                    val user = userDao.getUser(username, password)

                    if (admin != null && admin.username == "admin" && admin.password == "admin") {
                        Log.d("MainActivity", "Admin login successful")
                        val intent = Intent(this@MainActivity, AdminActvity::class.java)
                        intent.putExtra("USER_ID", admin.id)
                        startActivity(intent)
                        finish()
                    } else if (user != null) {
                        Log.d("MainActivity", "User login successful")
                        if (user.isAdmin) {
                            Toast.makeText(
                                this@MainActivity,
                                "Invalid credentials for admin",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            val intent = Intent(this@MainActivity, VotingActivity::class.java)
                            intent.putExtra("USER_ID", user.id)
                            startActivity(intent)
                            finish()
                        }
                    } else {
                        Log.d("MainActivity", "Invalid credentials")
                        Toast.makeText(this@MainActivity, "Invalid credentials", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            } else {
                Toast.makeText(this@MainActivity, "Please fill all fields", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private suspend fun ensureAdminUser() {
        val adminUser = userDao.getAdmin("admin", "admin")
        if (adminUser == null) {
            val admin = User(
                id = 0,
                username = "admin",
                password = "admin",
                isAdmin = true
            )
            userDao.insert(admin)
            Log.d("MainActivity", "Admin user created")
        }
    }
}
