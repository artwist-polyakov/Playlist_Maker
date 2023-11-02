package com.example.playlistmaker.media.ui.view_model.states

sealed class SinglePlaylistScreenInteraction {
    object CreatePlaylist : SinglePlaylistScreenInteraction()
    object SharePlaylist : SinglePlaylistScreenInteraction()
    object DeletePlaylist : SinglePlaylistScreenInteraction()
    object HandleBackButton : SinglePlaylistScreenInteraction()
    object OptionsClicked : SinglePlaylistScreenInteraction()
    object OptionsDismissed : SinglePlaylistScreenInteraction()
}
