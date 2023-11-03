package com.example.playlistmaker.media.ui.view_model.states

sealed class SinglePlaylistScreenInteraction {

    object SharePlaylist : SinglePlaylistScreenInteraction()
    object DeletePlaylist : SinglePlaylistScreenInteraction()
    object TappedBackButton : SinglePlaylistScreenInteraction()
    object OptionsClicked : SinglePlaylistScreenInteraction()
    object OptionsDismissed : SinglePlaylistScreenInteraction()
}
