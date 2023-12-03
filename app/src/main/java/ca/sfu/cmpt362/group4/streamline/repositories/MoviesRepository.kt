package ca.sfu.cmpt362.group4.streamline.repositories

import android.util.Log
import ca.sfu.cmpt362.group4.streamline.api_service.TmdbApiService
import ca.sfu.cmpt362.group4.streamline.data_models.Movie
import ca.sfu.cmpt362.group4.streamline.room.DAOs.MovieDao
import kotlinx.coroutines.flow.Flow
import retrofit2.Retrofit
import retrofit2.awaitResponse
import retrofit2.converter.gson.GsonConverterFactory


class MoviesRepository(private val movieDao: MovieDao) {

    private val apiKey = "40a2698ae5f721766169b5862398f409"

    val savedMovies: Flow<List<Movie>> = movieDao.getAllMovies()

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

    suspend fun insertMovie(movie: Movie) {
        // Insert a single movie into the database
        movieDao.insertMovie(movie)
    }

    suspend fun deleteMovie(movie: Movie) {
        movieDao.deleteMovie(movie)
    }

    suspend fun updateMovieRating(databaseId: Long, rating: Float) {
        // Update the rating in the database
        movieDao.updateMovieRating(databaseId, rating)
    }

    suspend fun getMovieById(movieId: Long): Movie? {
        return movieDao.getMovieById(movieId)
    }

    suspend fun deleteAllMovies() {
        movieDao.deleteAllMovies()
    }
}