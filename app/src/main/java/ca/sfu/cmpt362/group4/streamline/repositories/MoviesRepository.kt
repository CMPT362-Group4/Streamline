package ca.sfu.cmpt362.group4.streamline.repositories

import android.util.Log
import ca.sfu.cmpt362.group4.streamline.api_service.TmdbApiService
import ca.sfu.cmpt362.group4.streamline.data_models.Movie
import retrofit2.Retrofit
import retrofit2.awaitResponse
import retrofit2.converter.gson.GsonConverterFactory


class MoviesRepository() {

    private val apiKey = "40a2698ae5f721766169b5862398f409"

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
}