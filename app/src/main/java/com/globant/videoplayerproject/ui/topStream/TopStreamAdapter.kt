package com.globant.videoplayerproject.ui.topStream

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.globant.videoplayerproject.R
import com.globant.videoplayerproject.model.DataStream
import com.globant.videoplayerproject.utils.LIVE_VIDEO
import com.globant.videoplayerproject.utils.Utils
import kotlinx.android.synthetic.main.item_stream.view.*

class TopStreamAdapter: RecyclerView.Adapter<TopStreamAdapter.ViewHolder>() {

    lateinit var onClick: (DataStream) -> Unit
    private var dataSet = emptyList<DataStream>()

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int) =
        ViewHolder(
            LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.item_stream, viewGroup, false)
        )

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val item = dataSet[position]
        viewHolder.bindResponse(item, onClick)
    }

    override fun getItemCount() =  dataSet.size

    fun addStreams(items: List<DataStream>?) {
        if(items != null){
            this.dataSet = items
            notifyDataSetChanged()
        }
    }

    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        private val view = v
        fun bindResponse(stream: DataStream, onClick: (DataStream) -> Unit) = with(itemView) {
            view.item_title.text = stream.title
            view.item_time_and_date.text = Utils().formatDate(stream.started_at)
            if(stream.type == LIVE_VIDEO) {
                view.item_type.text = context.resources.getString(R.string.live)
            }

            val imageUrl = Utils().adaptImageUrl(stream.thumbnail_url)
            Glide.with(itemView.context)
                .load(imageUrl)
                .centerCrop()
                .placeholder(R.drawable.ic_baseline_image_search_24)
                .error(R.drawable.ic_baseline_broken_image_24)
                .into(item_video_image)

            setOnClickListener{ onClick(stream) }
        }
    }
}