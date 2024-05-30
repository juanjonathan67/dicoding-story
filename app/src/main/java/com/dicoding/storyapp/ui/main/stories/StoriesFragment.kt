package com.dicoding.storyapp.ui.main.stories

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.storyapp.R
import com.dicoding.storyapp.data.remote.response.ListStoryItem
import com.dicoding.storyapp.databinding.FragmentStoriesBinding
import com.dicoding.storyapp.databinding.ItemStoryBinding
import com.dicoding.storyapp.utils.ViewModelFactory

class StoriesFragment : Fragment() {
    private var _binding: FragmentStoriesBinding? = null
    private val binding get() = _binding!!
    private val storiesViewModel by viewModels<StoriesViewModel> { ViewModelFactory.getStoryInstance(requireContext()) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStoriesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.fabAddStory.setOnClickListener {
            findNavController().navigate(StoriesFragmentDirections.actionStoriesFragmentToAddStoryFragment())
        }
    }

    override fun onResume() {
        super.onResume()
        setStories()
    }

    private fun setStories() {
        val storiesAdapter = StoriesAdapter()
        binding.rvStories.adapter = storiesAdapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                storiesAdapter.retry()
            }
        )
        binding.rvStories.layoutManager = LinearLayoutManager(requireContext())

        storiesViewModel.stories?.observe(viewLifecycleOwner) {
            storiesAdapter.submitData(lifecycle, it)
        }

        storiesAdapter.refresh()

        val itemStoryView = ItemStoryBinding.inflate(layoutInflater)

        storiesAdapter.setOnItemClickCallback(object : StoriesAdapter.OnItemClickCallback {
            override fun onItemClicked(story: ListStoryItem) {

                val extras = FragmentNavigatorExtras(
                    itemStoryView.ivItemPhoto to "photo",
                    itemStoryView.tvItemName to "name",
                    itemStoryView.tvLocation to "location",
                    itemStoryView.tvItemCreated to "created",
                    itemStoryView.tvItemDescription to "description"
                )

                val bundle = bundleOf("story_id" to story.id)
                findNavController().navigate(
                    R.id.action_storiesFragment_to_storyDetailFragment,
                    bundle,
                    null,
                    extras
                )
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}