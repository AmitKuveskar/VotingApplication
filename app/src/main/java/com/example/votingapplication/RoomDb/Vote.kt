package com.example.votingapplication.RoomDb

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "votes")
data class Vote(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val userId: Int,
    val candidate: String
)
