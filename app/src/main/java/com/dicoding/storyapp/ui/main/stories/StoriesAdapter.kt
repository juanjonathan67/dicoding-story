package com.dicoding.storyapp.ui.main.stories

import android.location.Geocoder
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.storyapp.data.remote.response.ListStoryItem
import com.dicoding.storyapp.databinding.ItemStoryBinding
import com.dicoding.storyapp.utils.parseTimeInstantRelative
import java.util.Locale

class StoriesAdapter : PagingDataAdapter<ListStoryItem, StoriesAdapter.StoryViewHolder>(DIFF_CALLBACK) {
    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): StoryViewHolder {
        val binding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        val story = getItem(position)
        if (story != null) {
            holder.bind(story)
        }

        holder.itemView.setOnClickListener {
            getItem(holder.adapterPosition)?.let { it1 -> onItemClickCallback.onItemClicked(it1) }
        }
    }

    class StoryViewHolder(private val binding: ItemStoryBinding) : RecyclerView.ViewHolder(binding.root) {
        private lateinit var geocoder: Geocoder

        fun bind (story: ListStoryItem){
            Glide.with(binding.root.context)
                .load(story.photoUrl)
                .into(binding.ivItemPhoto)

            if ((story.lat != null && story.lat > -90 && story.lat < 90) && (story.lon != null && story.lon > -180 && story.lon < 180)) {
                geocoder = Geocoder(binding.root.context, Locale.getDefault())
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    geocoder.getFromLocation(story.lat.toDouble(), story.lon.toDouble(), 1) { addresses ->
                        ((addresses[0].locality ?: "Unknown") + ", " + (addresses[0].countryName ?: "Unknown")).also {
                            binding.tvLocation.text = it
                        }
                    }
                } else {
                    val addresses = geocoder.getFromLocation(story.lat.toDouble(), story.lon.toDouble(), 1)
                    ((addresses?.get(0)?.locality ?: "Unknown") + ", " + (addresses?.get(0)?.countryName ?: "Unknown")).also { binding.tvLocation.text = it }
                }
            } else {
                binding.tvLocation.visibility = View.GONE
            }

            binding.tvItemName.text = story.name
            if (story.createdAt != null) binding.tvItemCreated.text = parseTimeInstantRelative(story.createdAt)
            binding.tvItemDescription.text = story.description
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem.id == newItem.id
            }
            override fun areContentsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }
        }
    }

    interface OnItemClickCallback {
        fun onItemClicked(story: ListStoryItem)
    }
}