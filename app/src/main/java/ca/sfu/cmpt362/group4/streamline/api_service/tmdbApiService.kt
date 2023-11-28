package ca.sfu.cmpt362.group4.streamline.api_service

import ca.sfu.cmpt362.group4.streamline.data_models.MovieResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface TmdbApiService {
    @GET("movie/popular")
    fun getPopularMovies(@Query("api_key") apiKey: String): Call<MovieResponse>
}