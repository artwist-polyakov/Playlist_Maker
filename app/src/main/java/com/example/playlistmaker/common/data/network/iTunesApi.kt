package com.example.playlistmaker.common.data.network

import com.example.playlistmaker.common.data.dto.SongsSearchResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ITunesApi {
    @GET("search?entity=song")
    fun search(@Query("term") text: String): Call<SongsSearchResponse>
}