package com.nipunapps.nmsc.exoplayer

import android.support.v4.media.MediaMetadataCompat
import com.nipunapps.nmsc.data.entities.Song

fun MediaMetadataCompat.toSong() : Song?{
    return description?.let {
        Song(
            it.mediaId ?: "",
            it.title.toString(),
            it.mediaUri.toString(),
            it.iconUri.toString()
        )
    }
}