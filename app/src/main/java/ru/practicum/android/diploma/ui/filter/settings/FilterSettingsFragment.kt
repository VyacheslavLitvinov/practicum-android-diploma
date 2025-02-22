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
import ru.practicum.android.diploma.domain.models.WorkplaceFilter
import java.util.Locale

class FilterSettingsFragment : Fragment() {

    private var _binding: FragmentFilterSettingsBinding? = null
    private var industrySave: Industry? = null
    private var countrySave: String? = null
    private var regionSave: String? = null
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
            setClearFiltersView()
            viewModel.checkVisibilityOfButtons()
        }

        binding.etCountry.setOnClickListener {
            val workplaceSave = WorkplaceFilter(regionSave, countrySave)
            val bundleWorkplace = Bundle().apply { putParcelable(KEY_FOR_BUNDLE_DATA, workplaceSave) }
            findNavController().navigate(R.id.action_filterSettingsFragment_to_choiceWorkplaceFragment, bundleWorkplace)
        }

        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.etIndustries.setOnClickListener {
            val bundleIndustry = Bundle().apply { putString(INDUSTRY_KEY, industrySave?.id) }
            findNavController().navigate(R.id.action_filterSettingsFragment_to_choiceIndustryFragment, bundleIndustry)
        }
    }

    private fun setClearFiltersView() {
        binding.etCountry.setText("")
        binding.etIndustries.setText("")
        binding.etSalary.setText("")
        binding.checkBoxSalary.isChecked = false
        clearIndustry()
        clearWorkplace()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun applyFilter() {
        viewModel.saveFilterFromUi()
        findNavController().popBackStack()
    }

    private fun renderState(state: FilterSettingsState) {
        when (state) {
            is FilterSettingsState.FilterSettings -> {
                setFilteredUi(state.filter)
                binding.btApply.isVisible = true
                binding.btReset.isVisible = true
            }

            is FilterSettingsState.Empty -> {
                setEmptyFilterUi()
            }
        }
    }

    private fun setEmptyFilterUi() {
        binding.checkBoxSalary.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setSelectedOnlyWithSalary(isChecked)
            viewModel.checkVisibilityOfButtons()
        }
        binding.etSalary.doAfterTextChanged { text ->
            if (!text.isNullOrEmpty()) {
                viewModel.setSelectedSalary(text.toString().toInt())
            } else {
                viewModel.setSelectedSalary(null)
            }
            viewModel.checkVisibilityOfButtons()
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

        binding.checkBoxSalary.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setSelectedOnlyWithSalary(isChecked)
        }
        binding.etSalary.doAfterTextChanged { text ->
            if (!text.isNullOrEmpty()) {
                viewModel.setSelectedSalary(text.toString().toInt())
            } else {
                viewModel.setSelectedSalary(null)
            }
        }

        binding.etSalary.setText(if (filter.salary != null && filter.salary != 0) filter.salary.toString() else "")
        if (filter.onlyWithSalary != null) {
            binding.checkBoxSalary.setChecked(filter.onlyWithSalary!!)
        }
    }

    private fun setTextChangedListeners(til: TextInputLayout, et: TextInputEditText) {
        et.doAfterTextChanged { text ->
            if (text?.isNotEmpty() == true) {
                til.setEndIconDrawable(R.drawable.search_clear_icon)
                til.setEndIconOnClickListener {
                    text.clear()
                    if (til.id == R.id.tilIndustries) {
                        viewModel.clearIndustry()
                        clearIndustry()
                    } else {
                        viewModel.clearRegions()
                        clearWorkplace()
                    }
                    if (viewModel.counterFilter.value is FilterSettingsState.Empty) {
                        viewModel.checkVisibilityOfButtons()
                    }
                }
                til.defaultHintTextColor = getCorrectColorOfHint()
            } else {
                til.setEndIconDrawable(R.drawable.ic_arrow_right)
                til.setEndIconOnClickListener {}
                til.defaultHintTextColor = ColorStateList.valueOf(resources.getColor(R.color.hh_grey, null))
            }
        }
    }

    private fun getCorrectColorOfHint(): ColorStateList {
        return ColorStateList.valueOf(
            if (isDarkTheme()) {
                resources.getColor(R.color.white, null)
            } else {
                resources.getColor(R.color.black, null)
            }
        )
    }

    private fun setBackStackListeners() {
        with(findNavController().currentBackStackEntry?.savedStateHandle) {
            this?.getLiveData<Country?>(COUNTRY_BACKSTACK_KEY)?.observe(viewLifecycleOwner) { country ->
                viewModel.setSelectedCountry(country)
                binding.etCountry.setText(country?.name ?: "")
                viewModel.checkVisibilityOfButtons()
                countrySave = country?.name
            }

            this?.getLiveData<Region?>(REGION_BACKSTACK_KEY)?.observe(viewLifecycleOwner) { region ->
                viewModel.setSelectedRegion(region)
                regionSave = region?.name
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
                viewModel.checkVisibilityOfButtons()
            }

            this?.getLiveData<Industry?>(INDUSTRY_BACKSTACK_KEY)?.observe(viewLifecycleOwner) { industry ->
                viewModel.setSelectedIndustry(industry)
                binding.etIndustries.setText(industry?.name)
                viewModel.checkVisibilityOfButtons()
                industrySave = industry
            }
        }
    }

    private fun clearWorkplace() {
        findNavController().currentBackStackEntry?.savedStateHandle?.set(COUNTRY_BACKSTACK_KEY, null)
        findNavController().currentBackStackEntry?.savedStateHandle?.set(REGION_BACKSTACK_KEY, null)
        countrySave = null
        regionSave = null
    }

    private fun clearIndustry() {
        findNavController().currentBackStackEntry?.savedStateHandle?.set(INDUSTRY_BACKSTACK_KEY, null)
        industrySave = null
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
        private const val INDUSTRY_KEY = "industry"
    }
}
