package ca.sfu.cmpt362.group4.streamline.repositories

import android.util.Log
import ca.sfu.cmpt362.group4.streamline.api_service.TmdbApiService
import ca.sfu.cmpt362.group4.streamline.data_models.TvShows
import retrofit2.Retrofit
import retrofit2.awaitResponse
import retrofit2.converter.gson.GsonConverterFactory

class TvShowsRepository() {
    private val apiKey = "40a2698ae5f721766169b5862398f409"

    private val tmdbApi: TmdbApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org/3/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TmdbApiService::class.java)
    }

    suspend fun getPopularTv(): List<TvShows>? {
        return try {
            val response = tmdbApi.getPopularTv(apiKey).awaitResponse()
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
}