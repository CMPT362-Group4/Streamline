package ca.sfu.cmpt362.group4.streamline.view_models

import android.content.Context
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
import ca.sfu.cmpt362.group4.streamline.room.databases.MovieDatabase
import com.google.firebase.auth.FirebaseAuth
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
            repository.uploadMovieToFirebase(movie)
        }
    }

    fun deleteMovie(movie: Movie) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteMovie(movie)
            repository.deleteMovieFromFirebase(movie.id)
        }
    }

    fun deleteAllMovies() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAllMovies()
            repository.deleteAllMoviesFromFirebase()
        }
    }

    fun updateMovieRating(id: Long, newRating: Float) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateMovieRating(id, newRating)
        }
    }

    fun updateMovieFieldInFirebase(id: Long, fieldName: String, newValue: Any) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateMovieFieldInFirebase(id, fieldName, newValue)
        }
    }

    fun isMovieInList(movieId: Long): Boolean {
        val result = viewModelScope.async(Dispatchers.IO) {
            repository.getMovieById(movieId)

        }

        val movie = runBlocking { result.await() }
        return if (movie != null) {
            Log.d("MoviesViewModel", "Movie with ID $movieId found in the list")
            true
        } else {
            Log.d("MoviesViewModel", "Movie with ID $movieId not found in the list")
            false
        }
    }

}

class MoviesViewModelFactory(context: Context) : ViewModelProvider.Factory {

    private val uid = FirebaseAuth.getInstance().currentUser!!.uid

    //initialize database
    private val dao = MovieDatabase.getInstance(context, uid).movieDao
    private val repository = MoviesRepository(dao)
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MoviesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MoviesViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}