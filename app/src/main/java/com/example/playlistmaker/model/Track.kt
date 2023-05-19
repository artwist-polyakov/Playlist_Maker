package com.example.playlistmaker.model

class Track (val trackName: String,
             val artistName: String,
             val trackTimeMillis: Int,
             val artworkUrl100: String)

val tracks: ArrayList<Track> = arrayListOf(
    Track(
        "Smells Like Teen Spirit",
        "Nirvanaаоплпдатпдаьабвюрьпьабютрбватплртбьыавтрпьбытапьбытьбвпатьб",
        293000,
        "https://is5-ssl.mzstatic.com/image/thumb/Music115/v4/7b/58/c2/7b58c21a-2b51-2bb2-e59a-9bb9b96ad8c3/00602567924166.rgb.jpg/100x100bb.jpg"
    ),
    Track(
        "Billie Jean",
        "Michael Jackson",
        293000,
        "https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/3d/9d/38/3d9d3811-71f0-3a0e-1ada-3004e56ff852/827969428726.jpg/100x100bb.jpg"
    ),

    Track(
        "Stayin' Alive",
        "Bee Gees",
        293000,
        "https://is4-ssl.mzstatic.com/image/thumb/Music115/v4/1f/80/1f/1f801fc1-8c0f-ea3e-d3e5-387c6619619e/16UMGIM86640.rgb.jpg/100x100bb.jpg"
    ),
    Track(
        "Whole Lotta Love",
        "Led Zeppelin",
        293000,
        "https://is2-ssl.mzstatic.com/image/thumb/Music62/v4/7e/17/e3/7e17e33f-2efa-2a36-e916-7f808576cf6b/mzm.fyigqcbs.jpg/100x100bb.jpg"
    ),
            Track(
            "Sweet Child O'Mine",
    "Guns N' Roses",
                293000,
    "https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/a0/4d/c4/a04dc484-03cc-02aa-fa82-5334fcb4bc16/18UMGIM24878.rgb.jpg/100x100bb.jpg"
    )
)


// Path: app/src/main/java/com/example/playlistmaker/model/Track.kt
