package com.nipunapps.nmsc.adapters

import androidx.recyclerview.widget.AsyncListDiffer
import com.nipunapps.nmsc.R
import kotlinx.android.synthetic.main.swipe_item.view.*

class SwipeSongAdapter  : BaseSongAdapter(R.layout.swipe_item) {

    override val differ = AsyncListDiffer(this,diffCallback)

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val song = songs[position]
        holder.itemView.apply {
            tvPrimary.text = song.title
            setOnClickListener {
                onItemClickListener?.let { click ->
                    click(song)
                }
            }
        }
    }
} 