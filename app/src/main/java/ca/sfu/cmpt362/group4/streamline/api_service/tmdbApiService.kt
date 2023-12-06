package ca.sfu.cmpt362.group4.streamline.api_service

import ca.sfu.cmpt362.group4.streamline.data_models.MovieResponse
import ca.sfu.cmpt362.group4.streamline.data_models.TvResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface TmdbApiService {
    @GET("movie/popular")
    fun getPopularMovies(@Query("api_key") apiKey: String): Call<MovieResponse>

    @GET("movie/now_playing")
    suspend fun getNowPlayingMovies(@Query("api_key") apiKey: String): Response<MovieResponse>

    @GET("movie/top_rated")
    suspend fun getTopRatedMovies(@Query("api_key") apiKey: String): Response<MovieResponse>

    @GET("search/movie")
    suspend fun searchMovies(
        @Query("api_key") apiKey: String,
        @Query("query") query: String
    ): Response<MovieResponse>

    @GET("tv/popular")
    suspend fun getPopularTvShows(@Query("api_key") apiKey: String): Response<TvResponse>

    @GET("tv/on_the_air")
    suspend fun getOnTheAirTvShows(@Query("api_key") apiKey: String): Response<TvResponse>

    @GET("tv/top_rated")
    suspend fun getTopRatedTvShows(@Query("api_key") apiKey: String): Response<TvResponse>

    @GET("search/tv")
    suspend fun searchTvShows(
        @Query("api_key") apiKey: String,
        @Query("query") query: String
    ): Response<TvResponse>


}