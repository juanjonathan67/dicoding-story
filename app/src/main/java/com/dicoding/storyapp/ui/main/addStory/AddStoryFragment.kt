package com.dicoding.storyapp.ui.main.addStory

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.dicoding.storyapp.R
import com.dicoding.storyapp.data.Result
import com.dicoding.storyapp.databinding.FragmentAddStoryBinding
import com.dicoding.storyapp.ui.main.CameraActivity
import com.dicoding.storyapp.ui.main.CameraActivity.Companion.CAMERAX_RESULT
import com.dicoding.storyapp.utils.UserPreferences
import com.dicoding.storyapp.utils.ViewModelFactory
import com.dicoding.storyapp.utils.datastore
import com.dicoding.storyapp.utils.uriToFile
import com.dicoding.storyapp.utils.urlToFile
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.net.URL

class AddStoryFragment : Fragment() {
    private var _binding: FragmentAddStoryBinding? = null
    private val binding get() = _binding!!
    private val addStoryViewModel by viewModels<AddStoryViewModel> { ViewModelFactory.getStoryInstance(requireContext()) }
    private var currentImageUri: String? = null
    private var isUrl = false
    private var currentLocation: Location? = null
    private lateinit var policy : ThreadPolicy
    private lateinit var prefs : UserPreferences
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val requestCameraPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                showToast("Permission request granted")
            } else {
                showToast("Permission request denied")
            }
        }

    private val requestLocationPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                    // Precise location access granted.
                    getMyLastLocation()
                }
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                    // Only approximate location access granted.
                    getMyLastLocation()
                }
                else -> {
                    showToast("Location permission denied")
                }
            }
        }


    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri.toString()
            Glide.with(requireContext())
                .load(currentImageUri)
                .into(binding.ivImageSelected)
            isUrl = false
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERAX_RESULT) {
            currentImageUri = it.data?.getStringExtra(CameraActivity.EXTRA_CAMERAX_IMAGE) ?: ""
            Glide.with(requireContext())
                .load(currentImageUri)
                .into(binding.ivImageSelected)
            isUrl = false
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddStoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        policy = ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        prefs = UserPreferences.getInstance(requireContext().datastore)

        binding.rgImageMode.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rbLocalImage -> {
                    binding.tfImageUrl.visibility = View.GONE
                    binding.llLocalButtons.visibility = View.VISIBLE
                }
                R.id.rbImageUrl -> {
                    binding.llLocalButtons.visibility = View.GONE
                    binding.tfImageUrl.visibility = View.VISIBLE
                }
            }
        }

        binding.btChooseImage.setOnClickListener {
            launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.btTakePicture.setOnClickListener {
            if (checkPermission(Manifest.permission.CAMERA)) {
                val cameraXIntent = Intent(requireActivity(), CameraActivity::class.java)
                launcherIntentCameraX.launch(cameraXIntent)
            } else {
                requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }

        binding.tfImageUrl.editText?.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                currentImageUri = binding.tfImageUrl.editText?.text.toString()
                Glide.with(requireContext())
                    .load(currentImageUri)
                    .into(binding.ivImageSelected)
                isUrl = true
            }
        }

        binding.cbLocation.setOnCheckedChangeListener { _, isChecked ->
            if (!(checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
                        checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION))) {
                binding.cbLocation.isChecked = false
                requestLocationPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            } else {
                if (isChecked) {
                    getMyLastLocation()
                } else {
                    currentLocation = null
                }
            }
        }

        binding.buttonAdd.setOnClickListener {
            if (currentImageUri != null) {
                val file = if (isUrl) {
                    urlToFile(URL(currentImageUri), requireContext())
                } else {
                    uriToFile(currentImageUri!!.toUri(), requireContext())
                }
                val lat = if (binding.cbLocation.isChecked) currentLocation?.latitude?.toFloat() else null
                val lon = if (binding.cbLocation.isChecked) currentLocation?.longitude?.toFloat() else null

                addStoryViewModel.uploadStory(
                    file,
                    binding.tfStoryDescription.editText?.text.toString(),
                    lat,
                    lon,
                ).observe(viewLifecycleOwner) { result ->
                    if (result != null) {
                        when (result) {
                            is Result.Error -> {
                                binding.pbAddStory.visibility = View.GONE
                                showToast(result.error)
                            }
                            Result.Loading -> {
                                binding.pbAddStory.visibility = View.VISIBLE
                            }
                            is Result.Success -> {
                                binding.pbAddStory.visibility = View.GONE
                                showToast(result.data.message)
                                findNavController().popBackStack()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun getMyLastLocation() {
        if     (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
            checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        ){
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    currentLocation = location
                } else {
                    showToast("Location not found")
                }
            }
        } else {
            requestLocationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
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