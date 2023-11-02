package com.example.playlistmaker.common.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.playlistmaker.common.data.db.entity.PlaylistEntity
import com.example.playlistmaker.common.data.db.entity.PlaylistTrackReference
import com.example.playlistmaker.common.data.db.entity.TrackEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlist: PlaylistEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylists(playlists: List<PlaylistEntity>)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertTrackPlaylistTrackReference(playlistTrackReference: PlaylistTrackReference)

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

    @Query("SELECT * FROM playlists WHERE id = :playlistId")
    suspend fun getPlaylist(playlistId: String): PlaylistEntity

    @Query("SELECT COUNT(*) > 0 FROM playlist_track_reference WHERE playlistId = :playlistId AND trackId = :trackId")
    suspend fun isTrackInPlaylist(playlistId: String, trackId: Long): Boolean

    @Transaction
    suspend fun addTrackToPlaylist(playlistTrackReference: PlaylistTrackReference): Boolean {
        return if (!isTrackInPlaylist(
                playlistTrackReference.playlistId,
                playlistTrackReference.trackId
            )
        ) {
            incrementTracksCount(playlistTrackReference.playlistId.toString())
            insertTrackPlaylistTrackReference(playlistTrackReference)
            true
        } else {
            false
        }
    }

    @Query("UPDATE playlists SET tracksCount = tracksCount + 1 WHERE id = :playlistId")
    suspend fun incrementTracksCount(playlistId: String)
}