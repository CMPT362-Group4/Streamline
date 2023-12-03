package ca.sfu.cmpt362.group4.streamline.api_service

import ca.sfu.cmpt362.group4.streamline.data_models.SpecialGamesResponse
import retrofit2.Call
import retrofit2.http.GET

interface SteamService {
    @GET("featuredcategories/?l=english")
    fun getFeaturedCategories(): Call<SpecialGamesResponse>

    @GET("featuredcategories/?l=english")
    fun getRaw():Call<Map<String, Any>>

}