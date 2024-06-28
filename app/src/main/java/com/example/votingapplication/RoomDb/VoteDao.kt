package com.example.votingapplication.RoomDb

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface VoteDao {

    @Insert
    suspend fun insert(vote: Vote)

    @Query("SELECT candidate, COUNT(candidate) as voteCount FROM votes GROUP BY candidate")
    suspend fun getVoteCounts(): List<VoteCount>

    @Query("SELECT COUNT(*) FROM votes")
    suspend fun getTotalVoteCount(): Int

    @Query("SELECT COUNT(*) FROM votes WHERE userId = :userId")
    suspend fun hasVoted(userId: Int): Int
}

