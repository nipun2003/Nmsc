package com.nipunapps.nmsc.ui.fragments

import android.os.Bundle
import android.support.v4.media.session.PlaybackStateCompat
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.RequestManager
import com.nipunapps.nmsc.R
import com.nipunapps.nmsc.data.entities.Song
import com.nipunapps.nmsc.exoplayer.isPlaying
import com.nipunapps.nmsc.exoplayer.toSong
import com.nipunapps.nmsc.other.Status.*
import com.nipunapps.nmsc.other.setCurPlayerTimeToTextView
import com.nipunapps.nmsc.ui.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_song.*
import javax.inject.Inject

@AndroidEntryPoint
class SongFragment : Fragment(R.layout.fragment_song) {

    @Inject
    lateinit var glide : RequestManager

    private lateinit var mainViewModel: MainViewModel
    private var curPlayingSong : Song? = null
    private var playBackState : PlaybackStateCompat? = null
    private var shouldUpdateSeekbar = true

    private var isPlaying : Boolean = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        subscribeToService()
        bt_play.setOnClickListener {
            curPlayingSong?.let {
                mainViewModel.playOrToggleSong(it,isPlaying)
            }
        }
        bt_prev.setOnClickListener {
            mainViewModel.skipToPrevSong()
        }
        bt_next.setOnClickListener {
            mainViewModel.skipToNextSong()
        }

        seek_song_progressbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if(fromUser){
                    tv_song_current_duration.text = setCurPlayerTimeToTextView(progress.toLong())
                }
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
               shouldUpdateSeekbar = false
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                p0?.let {
                    mainViewModel.seekTo(it.progress.toLong())
                    shouldUpdateSeekbar = true
                }
            }
        })
    }

    private fun updateTitleAndImage(song: Song){
        player_activity_title.text = song.title
        glide.load(song.imageUrl).into(song_image_fragment)
    }

    private fun subscribeToService(){
        mainViewModel.mediaItems.observe(viewLifecycleOwner){
            it?.let {result->
                when(result.status){
                    SUCCESS ->{
                        result.data?.let { songs ->
                            if(curPlayingSong == null && songs.isNotEmpty()){
                                curPlayingSong = songs[0]
                                updateTitleAndImage(songs[0])
                            }
                        }
                    }
                }
            }
        }

        mainViewModel.curPlayingSong.observe(viewLifecycleOwner){
            if(it==null)return@observe
            curPlayingSong = it.toSong()
            updateTitleAndImage(curPlayingSong!!)
        }
        mainViewModel.playbackState.observe(viewLifecycleOwner){
            playBackState = it
            bt_play.setImageResource(
                if(playBackState?.isPlaying == true) R.drawable.ic_pause else R.drawable.ic_play_arrow
            )
            isPlaying = playBackState?.isPlaying == true
            seek_song_progressbar.progress = it?.position?.toInt() ?: 0
        }

        try {
            mainViewModel.curPlayerPosition.observe(viewLifecycleOwner) {
                if (shouldUpdateSeekbar) {
                    seek_song_progressbar.progress = it.toInt()
                    tv_song_current_duration.text = setCurPlayerTimeToTextView(it)

                }
            }
        }catch (e : Exception){
            Toast.makeText(requireContext(),""+e.message,Toast.LENGTH_LONG).show()
        }
        mainViewModel.curSongDuration.observe(viewLifecycleOwner){
            seek_song_progressbar.max = it.toInt()
            tv_song_total_duration.text = setCurPlayerTimeToTextView(it)
        }
    }
}