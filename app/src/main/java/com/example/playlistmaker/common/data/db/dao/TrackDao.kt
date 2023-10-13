package com.example.playlistmaker.common.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.playlistmaker.common.data.db.entity.TrackEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

@Dao
interface TrackDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(track: TrackEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTracks(tracks: List<TrackEntity>)

    @Query("SELECT isLiked FROM music_table WHERE id = :id limit 1")
    fun isTrackLiked(id: Long): Flow<Boolean?>

    @Query("SELECT COUNT(id)>0 FROM music_table WHERE id = :id limit 1")
    fun isTrackExist(id: Long): Flow<Boolean>

    @Query("SELECT * FROM music_table WHERE isLiked = 1")
    fun getLikedTracks(): Flow<List<TrackEntity>>

    @Query("UPDATE music_table SET isLiked = :liked, lastLikeUpdate = :timestamp WHERE id = :id")
    suspend fun setTrackIsLiked(id: Long, liked: Boolean, timestamp: Long)

    @Transaction
    suspend fun switchLike(track: TrackEntity): Boolean {
        val isTrackExist = isTrackExist(track.id).first()
        if (!isTrackExist) {
            insertTrack(track)
        }
        return updateTrackLike(track)
    }

    @Transaction
    suspend fun updateTrackLike(track: TrackEntity): Boolean {
        isTrackLiked(track.id).first()?.let {
            val currentTimestamp = System.currentTimeMillis()
            setTrackIsLiked(track.id, !it, currentTimestamp)
            return !it
        }
        return false
    }
}