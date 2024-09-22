package com.example.weatherforecast.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.InputFilter
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.weatherforecast.R
import com.example.weatherforecast.data.apiHelper.Result
import com.example.weatherforecast.data.remote.model.WeatherResponse
import com.example.weatherforecast.databinding.FragmentWeatherBinding
import com.example.weatherforecast.ui.viewmodel.WeatherForecastVM
import com.example.weatherforecast.utils.AppUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class WeatherFragment : Fragment() {

    private var currentCity: String? = null
    private lateinit var binding: FragmentWeatherBinding

    @Inject
    lateinit var viewModel: WeatherForecastVM

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate<FragmentWeatherBinding?>(
            inflater, R.layout.fragment_weather, container, false
        ).also {
            it.lifecycleOwner = viewLifecycleOwner
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getWeatherUpdate()
        initObservers()
        initListeners()
    }

    private fun initListeners() {
        val alphabetFilter = InputFilter { source, start, end, dest, dstart, dend ->
            for (i in start until end) {
                if (!Character.isLetter(source[i])) {
                    return@InputFilter ""
                }
            }
            null
        }
        binding.edtSearch.filters = arrayOf(alphabetFilter)

        binding.edtSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                viewModel.getWeatherUpdate(binding.edtSearch.text.toString())
                true
            } else false
        }

        binding.btnForecast.setOnClickListener {
            if (currentCity.isNullOrEmpty()) {
                AppUtils.showToast(requireContext(), "Please enter city name")
                return@setOnClickListener
            }

            findNavController().navigate(
                WeatherFragmentDirections.actionWeatherFragmentToForecastFragment(
                    currentCity
                )
            )
        }
    }

    private fun initObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.weatherState.collect { result ->
                    handleFocus()
                    when (result) {
                        is Result.Empty -> binding.progressBar.visibility = View.VISIBLE
                        is Result.Failure -> {
                            binding.progressBar.visibility = View.GONE
                            AppUtils.showToast(requireContext(), result.message)
                        }

                        is Result.Loading -> binding.progressBar.visibility = View.VISIBLE
                        is Result.Success -> {
                            binding.progressBar.visibility = View.GONE
                            initViews(result.data)
                        }
                    }
                }
            }
        }
    }

    private fun handleFocus() {
        if (binding.edtSearch.hasFocus()) {
            binding.edtSearch.clearFocus()
            AppUtils.hideKeyboardFrom(requireContext(), binding.edtSearch)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initViews(result: WeatherResponse?) {
        if (result == null) AppUtils.showToast(requireContext(), "No data Found")
        currentCity = result?.location?.name
        binding.apply {
            tvLabel.text = "$currentCity ${getString(R.string.weather_forecast)}"
            tvTemp.text = "${result?.current?.tempC} °C"
            tvHumidity.text = "${result?.current?.humidity}%"
            tvWind.text = "${result?.current?.windMph} km/h"
            tvDescription.text = result?.current?.condition?.text
        }
    }
}