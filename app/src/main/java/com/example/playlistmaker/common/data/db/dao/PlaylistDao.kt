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
    @Query("""
        SELECT music_table.* FROM music_table
        JOIN playlist_track_reference ON music_table.id = playlist_track_reference.trackId
        WHERE playlist_track_reference.playlistId = :playlistId
        ORDER BY playlist_track_reference.lastUpdate DESC
    """)
    fun getTracksFromPlaylist(playlistId: Long): Flow<List<TrackEntity>>
}