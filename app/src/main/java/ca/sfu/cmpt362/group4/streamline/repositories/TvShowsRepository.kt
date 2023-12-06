package ca.sfu.cmpt362.group4.streamline.repositories

import android.util.Log
import ca.sfu.cmpt362.group4.streamline.api_service.TmdbApiService
import ca.sfu.cmpt362.group4.streamline.data_models.TvShow
import ca.sfu.cmpt362.group4.streamline.room.DAOs.TvShowsDao
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.Flow
import retrofit2.Retrofit
import retrofit2.awaitResponse
import retrofit2.converter.gson.GsonConverterFactory

class TvShowsRepository(private val tvShowsDao: TvShowsDao) {

    private val apiKey = "40a2698ae5f721766169b5862398f409"

    val savedTvShows: Flow<List<TvShow>> = tvShowsDao.getAllTvShows()

    private val uid = FirebaseAuth.getInstance().currentUser!!.uid
    private val firebaseTvShows = FirebaseDatabase.getInstance().reference.child("users").child(uid).child("tv_shows")

    private val tmdbApi: TmdbApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org/3/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TmdbApiService::class.java)
    }

    suspend fun getPopularTv(): List<TvShow>? {
        return try {
            val response = tmdbApi.getPopularTvShows(apiKey)
            if (response.isSuccessful) {
                response.body()?.results
            } else {
                Log.e("TvShowsRepository", "Error fetching tv shows: ${response.errorBody()?.string()}")
                null
            }
        } catch (e: Exception) {
            Log.e("TvShowsRepository", "Exception fetching tv shows", e)
            null
        }
    }

    suspend fun searchTvShowsByNameFromTmdb(query: String): List<TvShow>? {
        return try {
            val response = tmdbApi.searchTvShows(apiKey, query)
            if (response.isSuccessful) {
                response.body()?.results
            } else {
                Log.e("MoviesRepository", "Error searching TV shows by name: ${response.errorBody()?.string()}")
                null
            }
        } catch (e: Exception) {
            Log.e("MoviesRepository", "Exception searching TV shows by name", e)
            null
        }
    }

    suspend fun getTopRatedTvShows(): List<TvShow>? {
        return try {
            val response = tmdbApi.getTopRatedTvShows(apiKey)
            if (response.isSuccessful) {
                response.body()?.results
            } else {
                Log.e("MoviesRepository", "Error fetching top-rated TV shows: ${response.errorBody()?.string()}")
                null
            }
        } catch (e: Exception) {
            Log.e("MoviesRepository", "Exception fetching top-rated TV shows", e)
            null
        }
    }

    suspend fun getOnTheAirTvShows(): List<TvShow>? {
        return try {
            val response = tmdbApi.getOnTheAirTvShows(apiKey)
            if (response.isSuccessful) {
                response.body()?.results
            } else {
                Log.e("MoviesRepository", "Error fetching on-the-air TV shows: ${response.errorBody()?.string()}")
                null
            }
        } catch (e: Exception) {
            Log.e("MoviesRepository", "Exception fetching on-the-air TV shows", e)
            null
        }
    }

    suspend fun insertTvShows(tvShows: TvShow) {
        // Insert a single TvShows into the database
        tvShowsDao.insertTvShows(tvShows)
    }

    suspend fun deleteTvShow(tvShows: TvShow) {
        tvShowsDao.deleteTvShow(tvShows)
    }

    suspend fun updateTvShowsRating(id: Long, rating: Float) {
        // Update the rating in the database
        tvShowsDao.updateTvShowsRating(id, rating)
    }

    suspend fun getTvShowsById(tvShowsId: Long): TvShow? {
        return tvShowsDao.getTvShowById(tvShowsId)
    }

    suspend fun deleteAllTvShows() {
        tvShowsDao.deleteAllTvShows()
    }

    fun uploadTvShowToFirebase(tvShow: TvShow) {
        val tvShowId = tvShow.id.toString()
        firebaseTvShows.child(tvShowId).setValue(tvShow)
    }

    fun deleteTvShowFromFirebase(tvShowId: Long) {

        // Find the tvShow with the given ID and remove it
        firebaseTvShows.orderByChild("id").equalTo(tvShowId.toDouble()).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (childSnapshot in snapshot.children) {
                    childSnapshot.ref.removeValue()
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    fun updateTvShowFieldInFirebase(id: Long, fieldName: String, newValue: Any) {

        // Find the tvShow with the given id and update the passed in field
        firebaseTvShows.orderByChild("id").equalTo(id.toDouble()).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (childSnapshot in snapshot.children) {
                    childSnapshot.ref.child(fieldName).setValue(newValue)
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    fun deleteAllTvShowsFromFirebase() {
        firebaseTvShows.removeValue()
    }
}