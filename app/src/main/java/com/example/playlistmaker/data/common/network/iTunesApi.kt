package com.example.playlistmaker.data.common.network

import com.example.playlistmaker.data.common.dto.SongsSearchResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ITunesApi {
    @GET("search?entity=song")
    fun search(@Query("term") text: String): Call<SongsSearchResponse>
}