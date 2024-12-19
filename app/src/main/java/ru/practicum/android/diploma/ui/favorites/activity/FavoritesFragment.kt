package ru.practicum.android.diploma.ui.favorites.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentFavoritesBinding
import ru.practicum.android.diploma.domain.favorites.models.FavoriteVacanciesScreenState
import ru.practicum.android.diploma.domain.models.Vacancy
import ru.practicum.android.diploma.ui.favorites.viewmodel.FavoritesViewModel

class FavoritesFragment : Fragment() {

    private var _binding: FragmentFavoritesBinding? = null
    private var favoriteVacanciesRecyclerViewAdapter: VacancyAdapter? = null

    private val binding get() = _binding!!
    private val viewModel: FavoritesViewModel by viewModel()

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

        val onItemClickListener: (Vacancy) -> Unit = {
            // Логика, исполняемая по нажатию на элемент списка вакансий
        }
        val onItemLongClickListener: (Vacancy) -> Unit = {
            // Логика, исполняемая по длительному нажатию на элемент списка вакансий
        }
        favoriteVacanciesRecyclerViewAdapter = VacancyAdapter(
            onItemClicked = onItemClickListener,
            onLongItemClicked = onItemLongClickListener
        )
        binding.rvFavoriteVacancies.adapter = favoriteVacanciesRecyclerViewAdapter

        viewModel.getFavoritesScreenStateLiveData().observe(viewLifecycleOwner) { favoritesScreenState ->
            if (favoritesScreenState is FavoriteVacanciesScreenState.Content) {
                favoriteVacanciesRecyclerViewAdapter?.setData(favoritesScreenState.favoriteVacanciesList)
            }
            binding.rvFavoriteVacancies.isVisible = favoritesScreenState is FavoriteVacanciesScreenState.Content
            binding.ivEmptyListPlaceholder.apply {
                isVisible = favoritesScreenState !is FavoriteVacanciesScreenState.Content
                setImageResource(
                    if (favoritesScreenState is FavoriteVacanciesScreenState.Error) R.drawable.placeholder_not_found
                    else R.drawable.placeholder_favorites_fragment
                )
            }
            binding.tvEmptyListText.apply {
                isVisible = favoritesScreenState !is FavoriteVacanciesScreenState.Content
                text = requireActivity().getString(
                    if (favoritesScreenState is FavoriteVacanciesScreenState.Error) R.string.not_found_text
                    else R.string.empty_list
                )
            }
        }

        viewModel.getFavoriteVacancies()
    }

    override fun onResume() {
        super.onResume()
        viewModel.getFavoriteVacancies()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
