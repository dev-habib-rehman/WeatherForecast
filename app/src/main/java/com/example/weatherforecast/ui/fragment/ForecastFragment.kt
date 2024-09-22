package com.example.weatherforecast.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.example.weatherforecast.R
import com.example.weatherforecast.data.apiHelper.Result
import com.example.weatherforecast.databinding.FragmentForecastBinding
import com.example.weatherforecast.ui.adapter.ForecastAdapter
import com.example.weatherforecast.ui.viewmodel.WeatherForecastVM
import com.example.weatherforecast.utils.AppUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ForecastFragment : Fragment() {

    private lateinit var binding: FragmentForecastBinding
    private lateinit var adapter: ForecastAdapter

    @Inject
    lateinit var viewModel: WeatherForecastVM

    private val args: ForecastFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate<FragmentForecastBinding?>(
            inflater, R.layout.fragment_forecast, container, false
        ).also {
            it.lifecycleOwner = viewLifecycleOwner
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getWeatherForecast(args.cityName.toString())
        initViews()
        initObservers()
    }

    @SuppressLint("SetTextI18n")
    private fun initViews() {
        binding.apply {
            binding.tvLabel.text = "${args.cityName} Forecast"
        }
        adapter = ForecastAdapter()
        binding.rvForecast.adapter = adapter
    }

    private fun initObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.weatherForecastState.collect { result ->
                    when (result) {
                        is Result.Failure -> AppUtils.showToast(requireContext(), result.message)
                        is Result.Success -> adapter.submitList(result.data)
                        else -> Unit
                    }
                }
            }
        }
    }
}