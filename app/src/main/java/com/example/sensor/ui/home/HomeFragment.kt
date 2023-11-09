package com.example.sensor.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.airbnb.lottie.LottieComposition
import com.airbnb.lottie.LottieCompositionFactory
import com.airbnb.lottie.LottieDrawable
import com.airbnb.lottie.OnCompositionLoadedListener
import com.example.sensor.databinding.FragmentHomeBinding
import com.example.sensor.persistence.user.UsersDatabase
import com.example.sensor.ui.main.MainViewModel


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    private val binding get() = _binding!!

    private val mainViewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        initView()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i("Maizi", "Home-----viewModel--->hashcode = ${mainViewModel.hashCode()}")
        mainViewModel.total.observe(viewLifecycleOwner) {
            Log.i("Maizi", "Home-----viewModel--->it = $it")
        }
    }

    private fun initView() {
        val loginUser = UsersDatabase.getInstance(requireActivity().applicationContext).getLoginUser()
        binding.userName.text = "UserName: ${loginUser.name}"
        binding.gender.text = "Gender: ${loginUser.gender}"
        binding.email.text = "Email: ${loginUser.email}"
        binding.lottieAvatar.setAnimation(loginUser.avatar)
        binding.lottieAvatar.playAnimation()

    }

    override fun onResume() {
        super.onResume()
        val loginUser = UsersDatabase.getInstance(requireContext()).getLoginUser()
        loginUser.let {
            binding.totalCarbon.text = "Total carbon emissions: ${it.totalEnergy}kg"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}