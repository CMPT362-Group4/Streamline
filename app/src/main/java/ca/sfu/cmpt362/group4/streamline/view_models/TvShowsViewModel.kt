package ca.sfu.cmpt362.group4.streamline.view_models

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import ca.sfu.cmpt362.group4.streamline.data_models.TvShow
import ca.sfu.cmpt362.group4.streamline.repositories.MoviesRepository
import ca.sfu.cmpt362.group4.streamline.repositories.TvShowsRepository
import ca.sfu.cmpt362.group4.streamline.room.databases.MovieDatabase
import ca.sfu.cmpt362.group4.streamline.room.databases.TvShowsDatabase
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class TvShowsViewModel(private val repository: TvShowsRepository): ViewModel() {
    val tvShows = MutableLiveData<List<TvShow>?>()

    val savedTvShows: LiveData<List<TvShow>> = repository.savedTvShows.asLiveData()

    fun fetchTvShows() {
        viewModelScope.launch(Dispatchers.IO) {
            val tvShowsList = repository.getPopularTv()
            tvShows.postValue(tvShowsList)
        }
    }

    fun insertTvShows(tvShow: TvShow) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertTvShows(tvShow)
            repository.uploadTvShowToFirebase(tvShow)
        }
    }

    fun deleteTvShow(tvShow: TvShow) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteTvShow(tvShow)
            repository.deleteTvShowFromFirebase(tvShow.id)
        }
    }

    fun deleteAllTvShows() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAllTvShows()
            repository.deleteAllTvShowsFromFirebase()
        }
    }

    fun updateTvShowsRating(id: Long, newRating: Float) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateTvShowsRating(id, newRating)
        }
    }

    fun updateTvShowFieldInFirebase(id: Long, fieldName: String, newValue: Any) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateTvShowFieldInFirebase(id, fieldName, newValue)
        }
    }

    fun isTvShowsInList(tvShowId: Long): Boolean {
        Log.d("TvShowsViewModel", "entering check")
        val result = viewModelScope.async(Dispatchers.IO) {
            Log.d("TvShowsViewModel", "entering check")
            repository.getTvShowsById(tvShowId)

        }

        val tvShow = runBlocking { result.await() }
        if (tvShow != null) {
            Log.d("TvShowsViewModel", "TvShow with ID $tvShowId found in the list")
            return true
        } else {
            Log.d("TvShowsViewModel", "TvShow with ID $tvShowId not found in the list")
            return false
        }
    }
}

class TvShowsViewModelFactory(context: Context) : ViewModelProvider.Factory {

    private val uid = FirebaseAuth.getInstance().currentUser!!.uid

    //initialize database
    private val dao = TvShowsDatabase.getInstance(context, uid).tvShowsDao
    private val repository = TvShowsRepository(dao)
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TvShowsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TvShowsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}