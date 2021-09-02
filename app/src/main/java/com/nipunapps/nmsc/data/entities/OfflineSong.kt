package com.nipunapps.nmsc.data.entities

import java.io.Serializable

class OfflineSong(
    private var data: String,
    private var title: String,
    private var album : String,
    private var artist : String
) : Serializable {

    fun setData(data: String) {
        this.data = data
    }
    fun getData() : String = this.data

    fun setTitle(title: String) {
        this.title = title
    }
    fun getTitle(): String = title

    fun setAlbum(album: String){
        this.album = album
    }
    fun getAlbum() : String = this.album

    fun setArtist(artist: String){
        this.artist = artist
    }
    fun getArtist() : String = this.artist

    private var duration : Int = 0
    fun setDuration(duration : String){
        this.duration = duration.toInt()
    }

    fun getDuration() : Int = this.duration

}