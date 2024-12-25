package ru.practicum.android.diploma.ui.favorites.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentFavoritesBinding
import ru.practicum.android.diploma.domain.models.Vacancy
import ru.practicum.android.diploma.ui.favorites.viewmodel.FavoriteVacanciesScreenState
import ru.practicum.android.diploma.ui.favorites.viewmodel.FavoritesViewModel
import ru.practicum.android.diploma.ui.vacancy.activity.VacancyFragment

class FavoritesFragment : Fragment() {

    private var _binding: FragmentFavoritesBinding? = null
    private var favoriteVacanciesRecyclerViewAdapter: VacancyAdapter? = null

    private val viewModel: FavoritesViewModel by viewModel()
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getFavoriteVacanciesScreenStateLiveData().observe(viewLifecycleOwner) { favVacScreenState ->
            when (favVacScreenState) {
                is FavoriteVacanciesScreenState.Empty -> showEmpty()
                is FavoriteVacanciesScreenState.DbError -> showError()
                is FavoriteVacanciesScreenState.Content -> showContent(favVacScreenState.favoriteVacancies)
            }
        }

        val onItemClickListener: (Vacancy) -> Unit = {
            // Логика, исполняемая по нажатию на элемент списка вакансий
            requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView).isVisible = false

            requireActivity().supportFragmentManager
                .beginTransaction()
                .replace(
                    R.id.container_view,
                    VacancyFragment.newInstance(it.id),
                    null
                )
                .addToBackStack(null)
                .commit()

        }
        val onItemLongClickListener: (Vacancy) -> Unit = {
            // Логика, исполняемая по длительному нажатию на элемент списка вакансий
        }
        favoriteVacanciesRecyclerViewAdapter = VacancyAdapter(
            onItemClicked = onItemClickListener,
            onLongItemClicked = onItemLongClickListener
        )
        binding.rvFavoriteVacancies.layoutManager = LinearLayoutManager(requireContext())
        binding.rvFavoriteVacancies.adapter = favoriteVacanciesRecyclerViewAdapter

        viewModel.getFavoriteVacanciesList()
    }

    override fun onResume() {
        super.onResume()
        viewModel.getFavoriteVacanciesList()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showEmpty() {
        binding.rvFavoriteVacancies.isVisible = false
        binding.ivEmptyListPlaceholder.apply {
            setImageResource(R.drawable.placeholder_favorites_fragment)
            isVisible = true
        }
        binding.tvEmptyListText.apply {
            text = requireActivity().resources.getString(R.string.empty_list)
            isVisible = true
        }
    }

    private fun showError() {
        binding.rvFavoriteVacancies.isVisible = false
        binding.ivEmptyListPlaceholder.apply {
            setImageResource(R.drawable.placeholder_not_found)
            isVisible = true
        }
        binding.tvEmptyListText.apply {
            text = requireActivity().resources.getString(R.string.not_found_text)
            isVisible = true
        }
    }

    private fun showContent(listOfFavoriteVacancies: List<Vacancy>) {
        favoriteVacanciesRecyclerViewAdapter?.setData(listOfFavoriteVacancies)
        binding.rvFavoriteVacancies.isVisible = true
        binding.ivEmptyListPlaceholder.isVisible = false
        binding.tvEmptyListText.isVisible = false
    }

}
