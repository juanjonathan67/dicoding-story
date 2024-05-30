package com.dicoding.storyapp.ui.main.storyDetail

import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.transition.TransitionInflater
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.dicoding.storyapp.R
import com.dicoding.storyapp.data.Result
import com.dicoding.storyapp.databinding.FragmentStoryDetailBinding
import com.dicoding.storyapp.utils.ViewModelFactory
import com.dicoding.storyapp.utils.parseTimeInstant
import java.time.Instant
import java.util.Locale

class StoryDetailFragment : Fragment() {
    private var _binding: FragmentStoryDetailBinding? = null
    private val binding get() = _binding!!
    private val storiesViewModel by viewModels<StoryDetailViewModel> { ViewModelFactory.getStoryInstance(requireContext()) }
    private lateinit var geocoder: Geocoder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = TransitionInflater.from(requireContext())
            .inflateTransition(R.transition.shared_detail)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStoryDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val storyId = arguments?.getString("story_id") ?: ""

        storiesViewModel.getStoryDetail(storyId).observe(viewLifecycleOwner) { result ->
            if (result != null) {
                when (result) {
                    is Result.Error -> {
                        showToast(result.error)
                        binding.pbDetail.visibility = View.GONE
                    }
                    Result.Loading -> {
                        binding.pbDetail.visibility = View.VISIBLE
                    }
                    is Result.Success -> {
                        Glide.with(requireContext())
                            .load(result.data.story.photoUrl)
                            .into(binding.ivDetailPhoto)

                        if ((result.data.story.lat != null && result.data.story.lat > -90 && result.data.story.lat < 90) && (result.data.story.lon != null && result.data.story.lon > -180 && result.data.story.lon < 180)) {
                            geocoder = Geocoder(binding.root.context, Locale.getDefault())
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                geocoder.getFromLocation(result.data.story.lat.toDouble(), result.data.story.lon.toDouble(), 1) { addresses ->
                                    ((addresses[0].locality ?: "Unknown") + ", " + (addresses[0].countryName ?: "Unknown")).also {
                                        binding.tvLocation.text = it
                                    }
                                }
                            } else {
                                val addresses = geocoder.getFromLocation(result.data.story.lat.toDouble(), result.data.story.lon.toDouble(), 1)
                                ((addresses?.get(0)?.locality ?: "Unknown") + ", " + (addresses?.get(0)?.countryName ?: "Unknown")).also { binding.tvLocation.text = it }
                            }
                        } else {
                            binding.tvLocation.visibility = View.GONE
                        }

                        binding.tvDetailName.text = result.data.story.name
                        binding.tvDetailCreated.text = parseTimeInstant(result.data.story.createdAt ?: Instant.now().toString(), resources.configuration.locales.get(0))
                        binding.tvDetailDescription.text = result.data.story.description
                        binding.pbDetail.visibility = View.GONE
                    }
                }
            }
        }
    }


    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}