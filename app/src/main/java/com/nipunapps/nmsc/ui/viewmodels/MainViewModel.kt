package com.nipunapps.nmsc.ui.viewmodels

import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_MEDIA_ID
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nipunapps.nmsc.data.entities.Song
import com.nipunapps.nmsc.exoplayer.*
import com.nipunapps.nmsc.other.Constants
import com.nipunapps.nmsc.other.Constants.MEDIA_ROOT_ID
import com.nipunapps.nmsc.other.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(
    private val musicServiceConnection: MusicServiceConnection
) : ViewModel() {
    private val _mediaItems = MutableLiveData<Resource<List<Song>>>()
    val mediaItems: LiveData<Resource<List<Song>>> = _mediaItems

    val isConnected = musicServiceConnection.isConnected
    val networkError = musicServiceConnection.networkError
    val curPlayingSong = musicServiceConnection.curPlayingSong
    val playbackState = musicServiceConnection.playbackState

    private val _curSongDuration = MutableLiveData<Long>()
    val curSongDuration : LiveData<Long> = _curSongDuration

    private val _curPlayerPosition = MutableLiveData<Long>()
    val curPlayerPosition : LiveData<Long> = _curPlayerPosition

    init {
        _mediaItems.postValue(Resource.loading(null))
        musicServiceConnection.subscribe(
            MEDIA_ROOT_ID,
            object : MediaBrowserCompat.SubscriptionCallback() {
                override fun onChildrenLoaded(
                    parentId: String,
                    children: MutableList<MediaBrowserCompat.MediaItem>
                ) {
                    super.onChildrenLoaded(parentId, children)
                    val items = children.map {
                        Song(
                            it.mediaId!!,
                            it.description.title.toString(),
                            it.description.mediaUri.toString(),
                            it.description.iconUri.toString()
                        )
                    }

                    _mediaItems.postValue(Resource.success(items))
                }
            })
        updateCurrentPlayerPosition()
    }

    private fun updateCurrentPlayerPosition(){
        viewModelScope.launch {
            while (true){
                val pos = playbackState.value?.currentPlaybackPosition
                if(curPlayerPosition.value != pos){
                    _curPlayerPosition.postValue(pos!!)
                    _curSongDuration.postValue(MusicService.curSongDuration)
                }
                delay(Constants.UPDATE_PAYER_POSITION_INTERVAL)
            }
        }
    }

    fun skipToNextSong(){
        musicServiceConnection.transportController.skipToNext()
    }
    fun skipToPrevSong(){
        musicServiceConnection.transportController.skipToPrevious()
    }

    fun seekTo(pos : Long){
        musicServiceConnection.transportController.seekTo(pos)
    }

    fun playOrToggleSong(mediaItem : Song, toggle: Boolean = false){
        val isPrepared = playbackState.value?.isPrepared?: false
        Log.e("Nipun","loaded")
        if(isPrepared && mediaItem.mediaId == curPlayingSong.value?.getString(METADATA_KEY_MEDIA_ID)){
            Log.e("Nipun","loaded2")
            playbackState.value?.let { playbackState ->
                when{
                    playbackState.isPrepared -> if(toggle) {
                        Log.e("Nipun","loaded prepare")
                        musicServiceConnection.transportController.pause()
                    }
                    else{
                        musicServiceConnection.transportController.play()
                    }
                    playbackState.isPlayEnabled -> {
                        Log.e("Nipun","loaded play")
                        musicServiceConnection.transportController.play()
                    }
                    else ->{
                        Log.e("Nipun","loaded prepare")
                        Unit
                    }
                }
            }
        }
        else{
            musicServiceConnection.transportController.playFromMediaId(mediaItem.mediaId,null)
        }
    }

    override fun onCleared() {
        super.onCleared()
        musicServiceConnection.unSubscribe(MEDIA_ROOT_ID,object : MediaBrowserCompat.SubscriptionCallback(){

        })
    }

}