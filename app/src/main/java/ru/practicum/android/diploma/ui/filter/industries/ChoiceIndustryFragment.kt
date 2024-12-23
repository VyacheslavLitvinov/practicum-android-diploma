package ru.practicum.android.diploma.ui.filter.industries

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.data.dto.model.industries.IndustriesFullDto
import ru.practicum.android.diploma.databinding.FragmentChoiceIndustryBinding

class ChoiceIndustryFragment : Fragment(), IndustriesAdapter.Listener {

    private var _binding: FragmentChoiceIndustryBinding? = null
    private val viewModel by viewModel<ChoiceIndustryViewModel>()
    private val binding get() = _binding!!
    private lateinit var adapter: IndustriesAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChoiceIndustryBinding.inflate(inflater, container, false)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvIndustries.isVisible = true
        viewModel.showIndustries()


        viewModel.industriesState.observe(viewLifecycleOwner) { state ->
            renderState(state)
        }

        binding.btnShowIndustries.setOnClickListener {
            showContent()
        }
    }

    private fun renderState(state: IndustriesState) {

        when (state) {
            is IndustriesState.FoundIndustries -> {
                adapter = IndustriesAdapter(this)
                adapter.updateIndustries(state.industries as List<IndustriesFullDto>)
                binding.rvIndustries.adapter = adapter
                binding.rvIndustries.isVisible = true
            }

            is IndustriesState.Error -> {

            }

            is IndustriesState.Loading -> {
            }

            is IndustriesState.NothingFound -> {
            }
        }
    }

    fun showContent() {
        viewModel.showIndustries()
        adapter = IndustriesAdapter(this)
        binding.rvIndustries.layoutManager = LinearLayoutManager(requireContext())
        binding.rvIndustries.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onClick(industry: IndustriesFullDto) {
        TODO("Not yet implemented")
    }
}
