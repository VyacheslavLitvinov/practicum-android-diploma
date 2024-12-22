package ru.practicum.android.diploma.ui.vacancy.activity

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.text.HtmlCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentVacancyBinding
import ru.practicum.android.diploma.domain.models.Vacancy
import ru.practicum.android.diploma.ui.vacancy.viewmodel.FavoriteVacancyButtonState
import ru.practicum.android.diploma.ui.vacancy.viewmodel.VacancyState
import ru.practicum.android.diploma.ui.vacancy.viewmodel.VacancyViewModel
import ru.practicum.android.diploma.util.DimenConvertor
import java.util.Locale

class VacancyFragment : Fragment() {

    private var _binding: FragmentVacancyBinding? = null

    private val binding get() = _binding!!
    private val viewModel by viewModel<VacancyViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVacancyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getVacancyScreenStateLiveData.observe(viewLifecycleOwner) {
            render(it)
        }

        viewModel.getFavoriteVacancyButtonStateLiveData.observe(viewLifecycleOwner) { favoriteButtonState ->
            binding.ivFavorites.setImageResource(
                if (favoriteButtonState is FavoriteVacancyButtonState.VacancyIsFavorite) R.drawable.favorites_on
                else R.drawable.favorites_off
            )
        }

        viewModel.getShareLinkStateLiveData.observe(viewLifecycleOwner) { linkForShareState ->
            if (linkForShareState.linkForShare != null) {
                val shareIntent = Intent.createChooser(
                    Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, linkForShareState.linkForShare)
                        setType("text/plain")
                    },
                    null
                )
                startActivity(shareIntent)
            }
        }

        binding.ivFavorites.setOnClickListener {
            if (viewModel.getFavoriteVacancyButtonStateLiveData.value is FavoriteVacancyButtonState.VacancyIsFavorite) {
                viewModel.deleteVacancyFromFavorites()
            } else {
                viewModel.insertVacancyInFavorites()
            }
        }

        binding.ivBack.setOnClickListener {
            goToPreviousScreen()
        }

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            goToPreviousScreen()
        }

        binding.ivSharing.setOnClickListener {
            viewModel.shareVacancy()
        }

        val bundle = this.arguments
        if (bundle != null) {
            val vacancyId = bundle.getString(KEY_FOR_BUNDLE_DATA)
            viewModel.getVacancyResources(vacancyId!!)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun goToPreviousScreen() {
        activity?.findViewById<BottomNavigationView>(R.id.bottomNavigationView)?.isVisible = true
        activity?.supportFragmentManager?.popBackStack()
    }

    private fun render(state: VacancyState) {
        when (state) {
            is VacancyState.Loading -> showLoading()
            is VacancyState.ServerError -> showError()
            is VacancyState.BadRequest -> badRequest()
            is VacancyState.NetworkError -> networkError()
            is VacancyState.Empty -> showEmpty()
            is VacancyState.Content -> showContent(state.item)
        }
    }

    private fun showLoading() {
        binding.progressBarVacancy.isVisible = true
        binding.scrollableContent.isVisible = false
        binding.llVacancyNotFound.isVisible = false
    }

    private fun showError() {
        binding.progressBarVacancy.isVisible = false
        binding.scrollableContent.isVisible = false
        binding.llVacancyNotFound.isVisible = true
        binding.atvErrorServer.isVisible = true
        binding.atvErrorInternet.isVisible = false
        binding.atvVacancyNotFound.isVisible = false
    }

    private fun badRequest() {
        binding.progressBarVacancy.isVisible = false
        binding.scrollableContent.isVisible = false
        binding.llVacancyNotFound.isVisible = true
        binding.atvErrorServer.isVisible = true
        binding.atvErrorInternet.isVisible = false
        binding.atvVacancyNotFound.isVisible = false
    }

    private fun networkError() {
        binding.progressBarVacancy.isVisible = false
        binding.scrollableContent.isVisible = false
        binding.llVacancyNotFound.isVisible = true
        binding.atvErrorServer.isVisible = false
        binding.atvErrorInternet.isVisible = true
        binding.atvVacancyNotFound.isVisible = false
    }

    private fun showEmpty() {
        binding.progressBarVacancy.isVisible = false
        binding.scrollableContent.isVisible = false
        binding.llVacancyNotFound.isVisible = true
        binding.atvErrorServer.isVisible = false
        binding.atvErrorInternet.isVisible = false
        binding.atvVacancyNotFound.isVisible = true
    }

    private fun showContent(item: Vacancy) {
        val imgSizeInPx =
            DimenConvertor.dpToPx(requireContext().resources.getDimension(R.dimen.img_size48), requireContext())
        binding.progressBarVacancy.isVisible = false
        binding.scrollableContent.isVisible = true
        binding.llVacancyNotFound.isVisible = false
        binding.tvName.text = item.titleOfVacancy
        binding.tvSalary.text = item.salary
        Glide.with(this)
            .load(item.employerLogoUrl)
            .override(imgSizeInPx, imgSizeInPx)
            .transform(RoundedCorners(R.dimen.corner_radius_10))
            .placeholder(R.drawable.grey_android_icon)
            .into(binding.ivImageEmployer)
        binding.tvEmployer.text = item.employerName
        binding.tvAddressEmployer.text = item.regionName
        binding.tvExperienceText.text = item.experience
        binding.tvScheduleText.text =
            String.format(
                Locale.getDefault(),
                "%s, %s",
                item.employmentType,
                item.scheduleType
            )
        binding.tvResponsibilitiesText.text = Html.fromHtml(item.description, HtmlCompat.FROM_HTML_MODE_LEGACY)

        binding.tvKeySkillsText.apply {
            isVisible = item.keySkills != null
            text = item.keySkills
        }
        binding.tvKeySkillsTitle.isVisible = item.keySkills != null
    }

    companion object {
        private const val KEY_FOR_BUNDLE_DATA = "saved_vacancy"

        fun newInstance(vacancyId: String): VacancyFragment {
            val fragment = VacancyFragment()
            val bundle = Bundle()
            bundle.putString(KEY_FOR_BUNDLE_DATA, vacancyId)
            fragment.arguments = bundle

            return fragment
        }

    }

}
