package ca.sfu.cmpt362.group4.streamline.repositories

import android.util.Log
import ca.sfu.cmpt362.group4.streamline.api_service.SteamService
import ca.sfu.cmpt362.group4.streamline.data_models.SpecialGames
import retrofit2.Retrofit
import retrofit2.awaitResponse
import retrofit2.converter.gson.GsonConverterFactory

class SteamRepository {

    private val steamApi: SteamService by lazy {
        Retrofit.Builder()
            .baseUrl("https://store.steampowered.com/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SteamService::class.java)
    }

    suspend fun getSpecialGames(): List<SpecialGames>? {
        try {
            val response = steamApi.getRaw().awaitResponse()

            return if (response.isSuccessful) {
                val specials = response.body()?.get("specials") as Map<*, *>
                specials["items"] as List<SpecialGames>
            } else {
                Log.e(
                    "SteamRepository",
                    "Error fetching special games: ${response.errorBody()?.string()}"
                )
                null
            }
        } catch (e: Exception) {
            Log.e("SteamRepository", "Exception fetching special games", e)
            return null
        }
    }
}