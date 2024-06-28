package com.example.votingapplication.RoomDb

import androidx.room.ColumnInfo

data class VoteCount(

@ColumnInfo(name = "candidate") val candidate: String,
@ColumnInfo(name = "voteCount") val voteCount: Int
)


