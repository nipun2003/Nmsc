package com.nipunapps.nmsc.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.nipunapps.nmsc.R
import com.nipunapps.nmsc.adapters.SongAdapter
import com.nipunapps.nmsc.other.Constants.TAG
import com.nipunapps.nmsc.other.Status
import com.nipunapps.nmsc.ui.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_home.*
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {

    lateinit var mainViewModel : MainViewModel

    @Inject
    lateinit var songAdapter: SongAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        setupRecyclerView()
        try {
            subscribeToObservers()
        }
        catch (e:Exception){
            Log.e(TAG,"ERRor handling")
        }
        songAdapter.setItemClickListener {
            mainViewModel.playOrToggleSong(it)
        }
    }

    private fun setupRecyclerView() = rvAllSongs.apply {
        adapter = songAdapter
        layoutManager = LinearLayoutManager(requireContext())

    }

    private fun subscribeToObservers(){
        try {
            mainViewModel.mediaItems.observe(viewLifecycleOwner){ result ->
                when(result.status){
                    Status.SUCCESS ->{
                        allSongsProgressBar.isVisible = false

                        result.data?.let { songs ->
                            songAdapter.songs = songs
                        }
                    }
                    Status.ERROR -> {
                        Log.d(TAG,"some error occured")
                        Unit
                    }
                    Status.LOADING -> allSongsProgressBar.isVisible = true
                }
            }
        }catch (e:Exception){
            Log.e(TAG,""+e.message)
        }

    }
}