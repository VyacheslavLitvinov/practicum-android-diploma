package ru.practicum.android.diploma.ui.filter.settings

import android.content.res.ColorStateList
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentFilterSettingsBinding
import ru.practicum.android.diploma.domain.models.Country
import ru.practicum.android.diploma.domain.models.Filter
import ru.practicum.android.diploma.domain.models.Industry
import ru.practicum.android.diploma.domain.models.Region
import java.util.Locale

class FilterSettingsFragment : Fragment() {

    private var _binding: FragmentFilterSettingsBinding? = null
    private var filterSave: Filter? = null

    private val binding get() = _binding!!
    private val viewModel by viewModel<FilterSettingsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFilterSettingsBinding.inflate(inflater, container, false)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setTextChangedListeners(binding.tilCountry, binding.etCountry)
        setTextChangedListeners(binding.tilIndustries, binding.etIndustries)
        setBackStackListeners()

        viewModel.counterFilter.observe(viewLifecycleOwner) { state ->
            renderState(state)
        }

        viewModel.getApplyResetButtonsStateLiveData.observe(viewLifecycleOwner) { stateOfButtons ->
            binding.btApply.isVisible = stateOfButtons.visibility
            binding.btReset.isVisible = stateOfButtons.visibility
        }

        viewModel.currentFilter()

        binding.btApply.setOnClickListener {
            applyFilter()
        }

        binding.btReset.setOnClickListener {
            viewModel.clearFilters()
        }

        binding.etCountry.setOnClickListener {
            if (binding.etCountry.text?.isNotEmpty() == true) {
                val bundle = Bundle()
                bundle.putBoolean(KEY_FOR_BUNDLE_DATA, true)
                findNavController().navigate(R.id.action_filterSettingsFragment_to_choiceWorkplaceFragment, bundle)
            } else {
                findNavController().navigate(R.id.action_filterSettingsFragment_to_choiceWorkplaceFragment)
            }
        }

        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.etIndustries.setOnClickListener {
            findNavController().navigate(R.id.action_filterSettingsFragment_to_choiceIndustryFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun applyFilter() {
        val checkBox = binding.checkBoxSalary.isChecked

        if (binding.etSalary.text.isNullOrEmpty()) {
            viewModel.saveFilterFromUi(null, checkBox)
        } else {
            val salary = binding.etSalary.text.toString().toInt()
            viewModel.saveFilterFromUi(salary, checkBox)
        }
        findNavController().popBackStack()
    }

    private fun renderState(state: FilterSettingsState) {
        when (state) {
            is FilterSettingsState.FilterSettings -> {
                setFilteredUi(state.filter)
            }

            is FilterSettingsState.Empty -> {
                setEmptyFilterUi()
            }
        }
    }

    private fun checkVisibilityOfApplyAndResetButtonsForEmptyState() {
        binding.btApply.isVisible = binding.etCountry.text?.isNotEmpty() == true || binding.checkBoxSalary.isChecked ||
            binding.etIndustries.text?.isNotEmpty() == true ||
            binding.etSalary.text?.isNotEmpty() == true && binding.etSalary.text?.isNotBlank() == true
        binding.btReset.isVisible = binding.etCountry.text?.isNotEmpty() == true || binding.checkBoxSalary.isChecked ||
            binding.etIndustries.text?.isNotEmpty() == true ||
            binding.etSalary.text?.isNotEmpty() == true && binding.etSalary.text?.isNotBlank() == true
    }

    private fun setEmptyFilterUi() {
        binding.checkBoxSalary.setOnCheckedChangeListener { _, _ ->
            checkVisibilityOfApplyAndResetButtonsForEmptyState()
        }
        binding.etSalary.doAfterTextChanged {
            checkVisibilityOfApplyAndResetButtonsForEmptyState()
        }
    }

    private fun setFilteredUi(filter: Filter) {
        if (filter.region?.name.isNullOrEmpty()) {
            binding.etCountry.setText(filter.country?.name ?: "")
        } else {
            binding.etCountry.setText(
                String.format(Locale.getDefault(), "%s, %s", filter.country?.name ?: "", filter.region?.name ?: "")
            )
        }
        binding.etIndustries.setText(filter.industry?.name ?: "")
        binding.etSalary.setText(if (filter.salary != null && filter.salary != 0) filter.salary.toString() else "")
        if (filter.onlyWithSalary != null) {
            binding.checkBoxSalary.setChecked(filter.onlyWithSalary!!)
        }
        filterSave = filter
    }

    private fun setTextChangedListeners(til: TextInputLayout, et: TextInputEditText) {
        et.doAfterTextChanged { text ->
            if (text?.isNotEmpty() == true) {
                til.setEndIconDrawable(R.drawable.search_clear_icon)
                til.setEndIconOnClickListener {
                    text.clear()
                    if (til.id == R.id.tilIndustries) {
                        viewModel.clearIndustry()
                    } else {
                        viewModel.clearRegions()
                    }
                }
                til.defaultHintTextColor = ColorStateList.valueOf(
                    if (isDarkTheme()) {
                        resources.getColor(R.color.white, null)
                    } else {
                        resources.getColor(R.color.black, null)
                    }
                )
            } else {
                til.setEndIconDrawable(R.drawable.ic_arrow_right)
                til.setEndIconOnClickListener {}
                til.defaultHintTextColor = ColorStateList.valueOf(resources.getColor(R.color.hh_grey, null))
            }
        }
    }

    private fun setBackStackListeners() {
        with(findNavController().currentBackStackEntry?.savedStateHandle) {
            this?.getLiveData<Country?>(COUNTRY_BACKSTACK_KEY)?.observe(viewLifecycleOwner) { country ->
                viewModel.setSelectedCountry(country)
                binding.etCountry.setText(country?.name ?: "")
            }

            this?.getLiveData<Region?>(REGION_BACKSTACK_KEY)?.observe(viewLifecycleOwner) { region ->
                viewModel.setSelectedRegion(region)
                if (region != null) {
                    binding.etCountry.setText(
                        String.format(
                            Locale.getDefault(),
                            "%s, %s",
                            binding.etCountry.text?.toString(),
                            region.name
                        )
                    )
                }
            }

            this?.getLiveData<Industry?>(INDUSTRY_BACKSTACK_KEY)?.observe(viewLifecycleOwner) { industry ->
                viewModel.setSelectedIndustry(industry)
                binding.etIndustries.setText(industry?.name)
            }
        }
    }

    private fun isDarkTheme(): Boolean {
        return requireActivity().resources.configuration.uiMode and
            Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
    }

    companion object {
        private const val KEY_FOR_BUNDLE_DATA = "region_was_selected"
        private const val COUNTRY_BACKSTACK_KEY = "country_key"
        private const val REGION_BACKSTACK_KEY = "region_key"
        private const val INDUSTRY_BACKSTACK_KEY = "industry_key"
    }
}
