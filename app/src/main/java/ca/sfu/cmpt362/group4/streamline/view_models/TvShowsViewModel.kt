package ca.sfu.cmpt362.group4.streamline.view_models

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import ca.sfu.cmpt362.group4.streamline.data_models.TvShows
import ca.sfu.cmpt362.group4.streamline.repositories.TvShowsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class TvShowsViewModel(private val repository: TvShowsRepository): ViewModel() {
    val tvShows = MutableLiveData<List<TvShows>?>()

    val savedTvShows: LiveData<List<TvShows>> = repository.savedTvShows.asLiveData()

    fun fetchTvShows() {
        viewModelScope.launch(Dispatchers.IO) {
            val tvShowsList = repository.getPopularTv()
            tvShows.postValue(tvShowsList)
        }
    }

    fun insertTvShows(tvShows: TvShows) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertTvShows(tvShows)
        }
    }

    fun deleteTvShows(tvShows: TvShows) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteTvShows(tvShows)
        }
    }

    fun updateTvShowsRating(databaseId: Long, newRating: Float) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateTvShowsRating(databaseId, newRating)
        }
    }

    fun isTvShowsInList(tvShowsId: Long): Boolean {
        Log.d("TvShowsViewModel", "entering check")
        val result = viewModelScope.async(Dispatchers.IO) {
            Log.d("TvShowsViewModel", "entering check")
            repository.getTvShowsById(tvShowsId)

        }

        val tvShows = runBlocking { result.await() }
        if (tvShows != null) {
            Log.d("TvShowsViewModel", "TvShows with ID $tvShowsId found in the list")
            return true
        } else {
            Log.d("TvShowsViewModel", "TvShows with ID $tvShowsId not found in the list")
            return false
        }
    }

    fun deleteAllTvShows() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAllTvShows()
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