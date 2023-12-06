package ca.sfu.cmpt362.group4.streamline.repositories

import android.util.Log
import ca.sfu.cmpt362.group4.streamline.api_service.TmdbApiService
import ca.sfu.cmpt362.group4.streamline.data_models.Movie
import ca.sfu.cmpt362.group4.streamline.room.DAOs.MovieDao
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.Flow
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.awaitResponse
import retrofit2.converter.gson.GsonConverterFactory



class MoviesRepository(private val movieDao: MovieDao) {

    private val apiKey = "40a2698ae5f721766169b5862398f409"

    private val uid = FirebaseAuth.getInstance().currentUser!!.uid
    private val firebaseMovies = FirebaseDatabase.getInstance().reference.child("users").child(uid).child("movies")

    val savedMovies: Flow<List<Movie>> = movieDao.getAllMovies()

    val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    val httpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    private val tmdbApi: TmdbApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org/3/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TmdbApiService::class.java)
    }


    suspend fun getPopularMovies(): List<Movie>? {
        return try {
            val response = tmdbApi.getPopularMovies(apiKey).awaitResponse()
            if (response.isSuccessful) {
                println(response.body()?.results)
                response.body()?.results
            } else {
                Log.e("MoviesRepository", "Error fetching movies: ${response.errorBody()?.string()}")
                null
            }
        } catch (e: Exception) {
            Log.e("MoviesRepository", "Exception fetching movies", e)
            null
        }
    }

    suspend fun getTopRatedMovies(): List<Movie>? {
        return try {
            val response = tmdbApi.getTopRatedMovies(apiKey)
            if (response.isSuccessful) {
                response.body()?.results
            } else {
                Log.e("MoviesRepository", "Error fetching top-rated movies: ${response.errorBody()?.string()}")
                null
            }
        } catch (e: Exception) {
            Log.e("MoviesRepository", "Exception fetching top-rated movies", e)
            null
        }
    }

    suspend fun getNowPlayingMovies(): List<Movie>? {
        return try {
            val response = tmdbApi.getNowPlayingMovies(apiKey)
            if (response.isSuccessful) {
                response.body()?.results
            } else {
                Log.e("MoviesRepository", "Error fetching now playing movies: ${response.errorBody()?.string()}")
                null
            }
        } catch (e: Exception) {
            Log.e("MoviesRepository", "Exception fetching now playing movies", e)
            null
        }
    }

    suspend fun searchMoviesByNameFromTmdb(query: String): List<Movie>? {
        return try {
            val response = tmdbApi.searchMovies(apiKey, query)
            if (response.isSuccessful) {
                response.body()?.results
            } else {
                Log.e("MoviesRepository", "Error searching movies by name: ${response.errorBody()?.string()}")
                null
            }
        } catch (e: Exception) {
            Log.e("MoviesRepository", "Exception searching movies by name", e)
            null
        }
    }

    suspend fun insertMovie(movie: Movie) {
        // Insert a single movie into the database
        movieDao.insertMovie(movie)
    }

    suspend fun deleteMovie(movie: Movie) {
        movieDao.deleteMovie(movie)
    }

    suspend fun updateMovieRating(id: Long, rating: Float) {
        // Update the rating in the database
        movieDao.updateMovieRating(id, rating)
    }

    suspend fun getMovieById(movieId: Long): Movie? {
        return movieDao.getMovieById(movieId)
    }

    suspend fun deleteAllMovies() {
        movieDao.deleteAllMovies()
    }

    suspend fun searchMoviesByName(movieName: String): List<Movie> {
        return movieDao.searchMoviesByName(movieName)
    }


    fun uploadMovieToFirebase(movie: Movie) {
        val movieId = movie.id.toString()
        firebaseMovies.child(movieId).setValue(movie)
    }

    fun deleteMovieFromFirebase(movieId: Long) {

        // Find the movie with the given ID and remove it
        firebaseMovies.orderByChild("id").equalTo(movieId.toDouble()).addListenerForSingleValueEvent(object :
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

    fun updateMovieFieldInFirebase(id: Long, fieldName: String, newValue: Any) {

        // Find the movie with the given roomId and update the passed in field
        firebaseMovies.orderByChild("id").equalTo(id.toDouble()).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (childSnapshot in snapshot.children) {
                    childSnapshot.ref.child(fieldName).setValue(newValue)
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    fun deleteAllMoviesFromFirebase() {
        firebaseMovies.removeValue()
    }
}