package com.globant.videoplayerproject.ui.videos

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.globant.videoplayerproject.R
import com.globant.videoplayerproject.model.DataStream
import com.globant.videoplayerproject.utils.Utils
import kotlinx.android.synthetic.main.item_stream.view.*

class ListVideoAdapter: RecyclerView.Adapter<ListVideoAdapter.ViewHolder>() {

    lateinit var onClick: (DataStream) -> Unit
    private var dataSet = emptyList<DataStream>()

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int) =
        ViewHolder(
            LayoutInflater.from(viewGroup.context).
            inflate(R.layout.item_video, viewGroup, false)
        )

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val item = dataSet[position]
        viewHolder.bindResponse(item, onClick)
    }

    override fun getItemCount() =  dataSet.size

    fun addStreams(items: List<DataStream>) {
        this.dataSet = items
        notifyDataSetChanged()
    }

    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        private val view = v
        fun bindResponse(video: DataStream, onClick: (DataStream) -> Unit) = with(itemView){
            view.item_title.text = video.title
            view.item_time_and_date.text = Utils().formatDate(video.started_at)

            val imageUrl = Utils().adaptImageUrl(video.thumbnail_url)
            Glide.with(itemView.context)
                .load(imageUrl)
                .centerCrop()
                .placeholder(R.drawable.ic_baseline_image_search_24)
                .error(R.drawable.ic_baseline_broken_image_24)
                .into(item_video_image)

            setOnClickListener{ onClick(video) }
        }
    }
}