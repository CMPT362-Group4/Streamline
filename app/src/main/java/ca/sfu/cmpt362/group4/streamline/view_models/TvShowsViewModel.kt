package ca.sfu.cmpt362.group4.streamline.view_models

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import ca.sfu.cmpt362.group4.streamline.data_models.TvShows
import ca.sfu.cmpt362.group4.streamline.repositories.TvShowsRepository
import kotlinx.coroutines.launch

class TvShowsViewModel(private val repository: TvShowsRepository): ViewModel() {
    val tvShows = MutableLiveData<List<TvShows>?>()

    fun fetchTvShows() {
        viewModelScope.launch {
            val tvShowsList = repository.getPopularTv()
            tvShows.postValue(tvShowsList)
        }
    }
}

class TvShowsViewModelFactory(private val repository: TvShowsRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(TvShowsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TvShowsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}