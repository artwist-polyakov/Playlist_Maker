package com.example.playlistmaker.common.data.db.dao

import android.util.Log
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.playlistmaker.common.data.db.entity.PlaylistEntity
import com.example.playlistmaker.common.data.db.entity.PlaylistTrackReference
import com.example.playlistmaker.common.data.db.entity.TrackEntity
import com.example.playlistmaker.common.utils.countDurationInSeconds
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

@Dao
interface PlaylistDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlist: PlaylistEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylists(playlists: List<PlaylistEntity>)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertTrackPlaylistTrackReference(playlistTrackReference: PlaylistTrackReference)

    @Query("DELETE FROM playlist_track_reference WHERE playlistId = :playlistId AND trackId = :trackId")
    suspend fun deleteTrackPlaylistTrackReference(playlistId: String, trackId: Long)

    @Query("SELECT * FROM playlists ORDER BY tracksCount DESC")
    fun getPlaylists(): Flow<List<PlaylistEntity>>

    @Transaction
    @Query(
        """
        SELECT music_table.* FROM music_table
        JOIN playlist_track_reference ON music_table.id = playlist_track_reference.trackId
        WHERE playlist_track_reference.playlistId = :playlistId
        ORDER BY playlist_track_reference.lastUpdate DESC
    """
    )
    fun getTracksFromPlaylist(playlistId: String): Flow<List<TrackEntity>>

    @Query("SELECT * FROM playlists WHERE id = :playlistId LIMIT 1")
    suspend fun getPlaylist(playlistId: String): PlaylistEntity

    @Query("SELECT COUNT(*) > 0 FROM playlist_track_reference WHERE playlistId = :playlistId AND trackId = :trackId")
    suspend fun isTrackInPlaylist(playlistId: String, trackId: Long): Boolean

    @Query("SELECT trackTime FROM music_table WHERE id = :trackId LIMIT 1")
    suspend fun getTrackDurationString(trackId: Long): String

    @Transaction
    suspend fun addTrackToPlaylist(playlistTrackReference: PlaylistTrackReference): Boolean {
        return if (!isTrackInPlaylist(
                playlistTrackReference.playlistId,
                playlistTrackReference.trackId
            )
        ) {
            val durationUpdate = getTrackDurationString(playlistTrackReference.trackId).countDurationInSeconds()
            incrementTracksCount(playlistTrackReference.playlistId)
            incrementPlaylistDuration(
                playlistTrackReference.playlistId,
                durationUpdate)
            insertTrackPlaylistTrackReference(playlistTrackReference)
            true
        } else {
            false
        }
    }

    @Transaction
    suspend fun removeTrackFromPlaylist(playlistTrackReference: PlaylistTrackReference) {
        val durationUpdate = getTrackDurationString(playlistTrackReference.trackId).countDurationInSeconds()
        decrementTracksCount(playlistTrackReference.playlistId)
        decrementPlaylistDuration(
            playlistTrackReference.playlistId,
            durationUpdate)
        deleteTrackPlaylistTrackReference(playlistTrackReference.playlistId, playlistTrackReference.trackId)
    }

    @Query("UPDATE playlists SET tracksCount = tracksCount + 1 WHERE id = :playlistId")
    suspend fun incrementTracksCount(playlistId: String)

    @Query("UPDATE playlists SET tracksCount = tracksCount - 1 WHERE id = :playlistId")
    suspend fun decrementTracksCount(playlistId: String)

    @Query("UPDATE playlists SET playlistDurationSeconds = playlistDurationSeconds + :update WHERE id = :playlistId")
    suspend fun incrementPlaylistDuration(playlistId: String, update: Long)

    @Query("UPDATE playlists SET playlistDurationSeconds = playlistDurationSeconds - :update WHERE id = :playlistId")
    suspend fun decrementPlaylistDuration(playlistId: String, update: Long)

    @Query("UPDATE playlists SET wasDurationCalculated = 1 WHERE id = :playlistId")
    suspend fun setWasDurationCalculated(playlistId: String)

    @Transaction
    suspend fun givePlaylistWithTime(playlistId: String): PlaylistEntity {
        var playlist = getPlaylist(playlistId)
        if (!playlist.wasDurationCalculated) {
            val duration = getTracksFromPlaylist(playlistId).map { tracks ->
                tracks.map { track ->
                    track.trackTime.countDurationInSeconds()
                }.sum()
            }.first()
            incrementPlaylistDuration(playlistId, duration)
            setWasDurationCalculated(playlistId)
            playlist = getPlaylist(playlistId)
        }
        return playlist
    }
}