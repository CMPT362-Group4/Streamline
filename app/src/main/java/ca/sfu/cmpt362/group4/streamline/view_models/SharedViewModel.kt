package ca.sfu.cmpt362.group4.streamline.view_models

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import ca.sfu.cmpt362.group4.streamline.api_service.TmdbApiService
import ca.sfu.cmpt362.group4.streamline.data_models.Movie
import ca.sfu.cmpt362.group4.streamline.data_models.MovieResponse
import ca.sfu.cmpt362.group4.streamline.data_models.SharedContent
import ca.sfu.cmpt362.group4.streamline.repositories.MoviesRepository
import ca.sfu.cmpt362.group4.streamline.repositories.SharedRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SharedViewModel(private val repository: SharedRepository) : ViewModel() {

    val savedContent: LiveData<List<Movie>> = repository.savedContent.asLiveData()

    class SharedViewModelFactory(private val repository: SharedRepository) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SharedViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return SharedViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}