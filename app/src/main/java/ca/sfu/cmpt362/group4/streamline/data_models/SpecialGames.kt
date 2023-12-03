package ca.sfu.cmpt362.group4.streamline.data_models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
data class SpecialGamesResponse(
    val specials: List<SpecialGames>
)

@Parcelize
data class SpecialGames(
    val id: Int,
    val type: Int,
    val name: String,
    val discounted: Boolean,
    val discount_percent: Int,
    val original_price: Int,
    val final_price: Int,
    val currency: String,
    val large_capsule_image: String,
    val small_capsule_image: String,
    val windows_available: Boolean,
    val linux_available: Boolean,
    val streamingvideo_available: Boolean,
    val discount_expiration: Long,
    val header_image: String,
    val controller_support: String
) :Parcelable

