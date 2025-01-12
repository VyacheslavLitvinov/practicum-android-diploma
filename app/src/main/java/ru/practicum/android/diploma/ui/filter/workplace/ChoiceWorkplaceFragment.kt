package ru.practicum.android.diploma.ui.filter.workplace

import android.content.res.ColorStateList
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentChoiceWorkplaceBinding
import ru.practicum.android.diploma.domain.models.Country
import ru.practicum.android.diploma.domain.models.Region
import ru.practicum.android.diploma.domain.models.WorkplaceFilter
import ru.practicum.android.diploma.ui.filter.workplace.region.ChoiceRegionFragment

class ChoiceWorkplaceFragment : Fragment() {

    private var _binding: FragmentChoiceWorkplaceBinding? = null
    private val binding get() = _binding!!
    private var regionModel: Region? = null
    private var countryModel: Country? = null
    private var countryContainer: TextInputLayout? = null
    private var countryTextInput: TextInputEditText? = null
    private var regionTextInput: TextInputEditText? = null
    private var regionContainer: TextInputLayout? = null
    private var submitButton: TextView? = null
    private var backButton: ImageView? = null
    private var workplaceFilter: WorkplaceFilter? = null
    private val viewModel by viewModel<ChoiceWorkplaceViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChoiceWorkplaceBinding.inflate(inflater, container, false)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        workplaceFilter = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable(KEY_FOR_BUNDLE_DATA, WorkplaceFilter::class.java)
        } else {
            arguments?.getParcelable(KEY_FOR_BUNDLE_DATA)
        }

        setViews()
        setOnClickListeners()
        setTextChangedListeners()
        setBackStackListeners()

        if (workplaceFilter?.nameCountry.isNullOrEmpty()) {
            binding.etCountry.setText(countryModel?.name)
        } else {
            binding.etCountry.setText(workplaceFilter?.nameCountry)
        }

        if (workplaceFilter?.nameRegion.isNullOrEmpty()) {
            binding.etRegion.setText(regionModel?.name)
        } else {
            binding.etRegion.setText(workplaceFilter?.nameRegion)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setViews() {
        countryContainer = binding.tilCountry
        countryTextInput = binding.etCountry
        regionTextInput = binding.etRegion
        regionContainer = binding.tilRegion
        submitButton = binding.btEnter
        backButton = binding.ivBack
    }

    private fun setOnClickListeners() {
        countryContainer?.setEndIconOnClickListener {
            countryClickListener()
        }

        countryTextInput?.setOnClickListener {
            countryClickListener()
        }

        regionContainer?.setEndIconOnClickListener {
            regionClickListener()
        }

        regionTextInput?.setOnClickListener {
            regionClickListener()
        }

        submitButton?.setOnClickListener {
            findNavController().previousBackStackEntry?.savedStateHandle?.set(COUNTRY_BACKSTACK_KEY, countryModel)
            findNavController().previousBackStackEntry?.savedStateHandle?.set(REGION_BACKSTACK_KEY, regionModel)
            findNavController().popBackStack()
        }

        backButton?.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun regionClickListener() {
        val countryId = countryModel?.id ?: ""
        findNavController().navigate(
            R.id.action_choiceWorkplaceFragment_to_choiceRegionFragment,
            ChoiceRegionFragment.createArgs(countryId)
        )
    }

    private fun countryClickListener() {
        findNavController().navigate(
            R.id.action_choiceWorkplaceFragment_to_choiceCountryFragment
        )
    }

    private fun setTextChangedListeners() {
        val countryTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
            override fun afterTextChanged(s: Editable?) {
                with(countryContainer!!) {
                    if (s.isNullOrBlank()) {
                        setEndIconDrawable(R.drawable.ic_arrow_right)
                        defaultHintTextColor = ColorStateList.valueOf(resources.getColor(R.color.hh_grey, null))
                        setEndIconOnClickListener {
                            findNavController().navigate(
                                R.id.action_choiceWorkplaceFragment_to_choiceCountryFragment
                            )
                        }
                    } else {
                        endIconMode = TextInputLayout.END_ICON_CUSTOM
                        setEndIconDrawable(R.drawable.search_clear_icon)

                        defaultHintTextColor = ColorStateList.valueOf(
                            resources.getColor(
                                if (isDarkTheme()) {
                                    R.color.white
                                } else {
                                    R.color.black
                                },
                                null
                            )
                        )

                        setEndIconOnClickListener {
                            s.clear()
                            regionTextInput?.text?.clear()
                            countryModel = null
                            regionModel = null

                            findNavController().currentBackStackEntry?.savedStateHandle?.set(
                                COUNTRY_BACKSTACK_KEY,
                                null
                            )

                            findNavController().currentBackStackEntry?.savedStateHandle?.set(
                                REGION_BACKSTACK_KEY,
                                null
                            )
                            submitButton?.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }

        val regionTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
            override fun afterTextChanged(s: Editable?) {
                with(regionContainer!!) {
                    if (s.isNullOrBlank()) {
                        setEndIconDrawable(R.drawable.ic_arrow_right)
                        defaultHintTextColor = ColorStateList.valueOf(resources.getColor(R.color.hh_grey, null))
                        setEndIconOnClickListener {
                            val countryId = countryModel?.id ?: ""
                            findNavController().navigate(
                                R.id.action_choiceWorkplaceFragment_to_choiceRegionFragment,
                                ChoiceRegionFragment.createArgs(countryId)
                            )
                        }
                    } else {
                        endIconMode = TextInputLayout.END_ICON_CUSTOM
                        setEndIconDrawable(R.drawable.search_clear_icon)

                        defaultHintTextColor = ColorStateList.valueOf(
                            resources.getColor(
                                if (isDarkTheme()) {
                                    R.color.white
                                } else {
                                    R.color.black
                                },
                                null
                            )
                        )

                        setEndIconOnClickListener {
                            s.clear()
                            regionTextInput?.text?.clear()
                            regionModel = null

                            findNavController().currentBackStackEntry?.savedStateHandle?.set(
                                REGION_BACKSTACK_KEY,
                                null
                            )

                            submitButton?.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }
        countryTextInput?.addTextChangedListener(countryTextWatcher)
        regionTextInput?.addTextChangedListener(regionTextWatcher)
    }

    private fun setBackStackListeners() {
        with(findNavController().currentBackStackEntry?.savedStateHandle) {
            this?.getLiveData<Region?>(REGION_BACKSTACK_KEY)?.observe(viewLifecycleOwner) { region ->
                regionModel = region

                if (countryModel == null) {
                    countryModel = regionModel?.parentCountry
                    countryTextInput?.setText(regionModel?.parentCountry?.name)
                }

                if (region != null) {
                    regionTextInput?.setText(region.name)
                    submitButton?.visibility = View.VISIBLE
                }
            }

            this?.getLiveData<Country>(COUNTRY_BACKSTACK_KEY)?.observe(viewLifecycleOwner) { country ->
                countryModel = country
                regionModel = get(REGION_BACKSTACK_KEY)

                if (country != null) {
                    countryTextInput?.setText(country.name)
                    submitButton?.visibility = View.VISIBLE

                    if (countryModel?.id != regionModel?.parentCountry?.id) {
                        regionTextInput?.text?.clear()
                        regionModel = null
                        findNavController().currentBackStackEntry?.savedStateHandle?.set(
                            REGION_BACKSTACK_KEY,
                            null
                        )
                    }
                }
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
    }
}
