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
import ca.sfu.cmpt362.group4.streamline.repositories.MoviesRepository
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

class MoviesViewModel(private val repository: MoviesRepository) : ViewModel() {
    val movies = MutableLiveData<List<Movie>?>()

    val savedMovies: LiveData<List<Movie>> = repository.savedMovies.asLiveData()


    fun fetchMovies() {
        viewModelScope.launch(Dispatchers.IO) {
            val movieList = repository.getPopularMovies()
            movies.postValue(movieList)
        }
    }

    fun insertMovie(movie: Movie) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertMovie(movie)
        }
    }

    fun deleteMovie(movie: Movie) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteMovie(movie)
        }
    }

    fun updateMovieRating(databaseId: Long, newRating: Float) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateMovieRating(databaseId, newRating)
        }
    }

    fun isMovieInList(movieId: Long): Boolean {
        Log.d("MoviesViewModel", "entering check")
        val result = viewModelScope.async(Dispatchers.IO) {
            Log.d("MoviesViewModel", "entering check")
            repository.getMovieById(movieId)

        }

        val movie = runBlocking { result.await() }
        if (movie != null) {
            Log.d("MoviesViewModel", "Movie with ID $movieId found in the list")
            return true
        } else {
            Log.d("MoviesViewModel", "Movie with ID $movieId not found in the list")
            return false
        }
    }

    fun deleteAllMovies() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAllMovies()
        }
    }

}

class MoviesViewModelFactory(private val repository: MoviesRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MoviesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MoviesViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}