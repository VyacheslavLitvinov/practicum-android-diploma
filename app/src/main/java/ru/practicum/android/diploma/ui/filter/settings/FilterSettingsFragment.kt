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
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentFilterSettingsBinding
import ru.practicum.android.diploma.domain.models.Filter
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
        viewModel.currentFilter()
        viewModel.counterFilter.observe(viewLifecycleOwner) { state ->
            renderState(state)
        }

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
            filterSave = Filter(onlyWithSalary = checkBox)
        } else {
            val salary = binding.etSalary.text.toString().toInt()
            filterSave = Filter(
                salary = salary,
                onlyWithSalary = checkBox
            )
        }
        viewModel.saveFilterFromUi(filterSave!!)
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
                clearFilter()
                setEmptyFilterUi()
                binding.btReset.isVisible = false
                binding.btApply.isVisible = false
            }
        }
    }

    private fun clearFilter() {
        binding.etCountry.setText("")
        binding.etIndustries.setText("")
        binding.etSalary.setText("")
        binding.checkBoxSalary.setChecked(false)
    }

    private fun setEmptyFilterUi() {
        binding.etCountry.doAfterTextChanged { text ->
            binding.btApply.isVisible = text?.isNotEmpty() == true || binding.checkBoxSalary.isChecked ||
                binding.etIndustries.text?.isNotEmpty() == true ||
                binding.etSalary.text?.isNotEmpty() == true && binding.etSalary.text?.isNotBlank() == true
            binding.btReset.isVisible = text?.isNotEmpty() == true || binding.checkBoxSalary.isChecked ||
                binding.etIndustries.text?.isNotEmpty() == true ||
                binding.etSalary.text?.isNotEmpty() == true && binding.etSalary.text?.isNotBlank() == true
            if (text?.isNotEmpty() == true) {
                binding.tilCountry.setEndIconDrawable(R.drawable.search_clear_icon)
                binding.tilCountry.setEndIconOnClickListener {
                    text.clear()
                }
                binding.tilCountry.defaultHintTextColor = ColorStateList.valueOf(
                    if (isDarkTheme()) {
                        resources.getColor(R.color.white, null)
                    } else {
                        resources.getColor(R.color.black, null)
                    }
                )
            } else {
                binding.tilCountry.setEndIconDrawable(R.drawable.ic_arrow_right)
                binding.tilCountry.setEndIconOnClickListener {}
                binding.tilCountry.defaultHintTextColor = ColorStateList.valueOf(resources.getColor(R.color.hh_grey, null))
            }
        }
        binding.etIndustries.doAfterTextChanged { text ->
            binding.btApply.isVisible = text?.isNotEmpty() == true || binding.checkBoxSalary.isChecked ||
                binding.etCountry.text?.isNotEmpty() == true ||
                binding.etSalary.text?.isNotEmpty() == true && binding.etSalary.text?.isNotBlank() == true
            binding.btReset.isVisible = text?.isNotEmpty() == true || binding.checkBoxSalary.isChecked ||
                binding.etCountry.text?.isNotEmpty() == true ||
                binding.etSalary.text?.isNotEmpty() == true && binding.etSalary.text?.isNotBlank() == true
            if (text?.isNotEmpty() == true) {
                binding.tilIndustries.setEndIconDrawable(R.drawable.search_clear_icon)
                binding.tilIndustries.setEndIconOnClickListener {
                    text.clear()
                }
                binding.tilIndustries.defaultHintTextColor = ColorStateList.valueOf(
                    if (isDarkTheme()) {
                        resources.getColor(R.color.white, null)
                    } else {
                        resources.getColor(R.color.black, null)
                    }
                )
            } else {
                binding.tilIndustries.setEndIconDrawable(R.drawable.ic_arrow_right)
                binding.tilIndustries.setEndIconOnClickListener {}
                binding.tilIndustries.defaultHintTextColor = ColorStateList.valueOf(resources.getColor(R.color.hh_grey, null))
            }
        }
        binding.checkBoxSalary.setOnCheckedChangeListener { _, isChecked ->
            binding.btApply.isVisible = binding.etSalary.text?.isNotEmpty() == true &&
                binding.etSalary.text?.isNotBlank() == true || isChecked ||
                binding.etCountry.text?.toString()?.isNotEmpty() == true ||
                binding.etIndustries.text?.toString()?.isNotEmpty() == true
            binding.btReset.isVisible = binding.etSalary.text?.isNotEmpty() == true &&
                binding.etSalary.text?.isNotBlank() == true || isChecked ||
                binding.etCountry.text?.toString()?.isNotEmpty() == true ||
                binding.etIndustries.text?.toString()?.isNotEmpty() == true
        }
        binding.etSalary.doAfterTextChanged { text ->
            binding.btApply.isVisible = !text.isNullOrEmpty() && text.isNotBlank() ||
                binding.checkBoxSalary.isChecked ||
                binding.etCountry.text?.toString()?.isNotEmpty() == true ||
                binding.etIndustries.text?.toString()?.isNotEmpty() == true
            binding.btReset.isVisible = !text.isNullOrEmpty() && text.isNotBlank() ||
                binding.checkBoxSalary.isChecked ||
                binding.etCountry.text?.toString()?.isNotEmpty() == true ||
                binding.etIndustries.text?.toString()?.isNotEmpty() == true
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
        if (binding.etCountry.text?.isNotEmpty() == true) {
            binding.tilCountry.setEndIconDrawable(R.drawable.search_clear_icon)
            binding.tilCountry.setEndIconOnClickListener {
                binding.etCountry.setText("")
            }
            binding.tilCountry.defaultHintTextColor = ColorStateList.valueOf(
                if (isDarkTheme()) {
                    resources.getColor(R.color.white, null)
                } else {
                    resources.getColor(R.color.black, null)
                }
            )
        } else {
            binding.tilCountry.defaultHintTextColor = ColorStateList.valueOf(resources.getColor(R.color.hh_grey, null))
        }
        if (binding.etIndustries.text?.isNotEmpty() == true) {
            binding.tilIndustries.setEndIconDrawable(R.drawable.search_clear_icon)
            binding.tilIndustries.setEndIconOnClickListener {
                binding.etIndustries.setText("")
            }
            binding.tilIndustries.defaultHintTextColor = ColorStateList.valueOf(
                if (isDarkTheme()) {
                    resources.getColor(R.color.white, null)
                } else {
                    resources.getColor(R.color.black, null)
                }
            )
        }
        binding.etCountry.doAfterTextChanged { text ->
            if (text?.isNotEmpty() == true) {
                binding.tilCountry.setEndIconDrawable(R.drawable.search_clear_icon)
                binding.tilCountry.setEndIconOnClickListener {
                    text.clear()
                }
                binding.tilCountry.defaultHintTextColor = ColorStateList.valueOf(
                    if (isDarkTheme()) {
                        resources.getColor(R.color.white, null)
                    } else {
                        resources.getColor(R.color.black, null)
                    }
                )
            } else {
                binding.tilCountry.setEndIconDrawable(R.drawable.ic_arrow_right)
                binding.tilCountry.setEndIconOnClickListener {}
                binding.tilCountry.defaultHintTextColor = ColorStateList.valueOf(resources.getColor(R.color.hh_grey, null))
            }
        }
        binding.etIndustries.doAfterTextChanged { text ->
            if (text?.isNotEmpty() == true) {
                binding.tilIndustries.setEndIconDrawable(R.drawable.search_clear_icon)
                binding.tilIndustries.setEndIconOnClickListener {
                    text.clear()
                }
                binding.tilIndustries.defaultHintTextColor = ColorStateList.valueOf(
                    if (isDarkTheme()) {
                        resources.getColor(R.color.white, null)
                    } else {
                        resources.getColor(R.color.black, null)
                    }
                )
            } else {
                binding.tilIndustries.setEndIconDrawable(R.drawable.ic_arrow_right)
                binding.tilIndustries.setEndIconOnClickListener {}
                binding.tilIndustries.defaultHintTextColor = ColorStateList.valueOf(resources.getColor(R.color.hh_grey, null))
            }
        }
    }

    private fun isDarkTheme(): Boolean {
        return requireActivity().resources.configuration.uiMode and
            Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
    }

    companion object {
        private const val KEY_FOR_BUNDLE_DATA = "region_was_selected"
    }
}
