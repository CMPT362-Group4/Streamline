package ca.sfu.cmpt362.group4.streamline.data_models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

data class TvResponse(
    val results: List<TvShows>
)
@Parcelize
data class TvShows(
    val id: Int,
    val name: String,
    val poster_path: String,
    val backdrop_path: String,
    val first_air_date: String,
    val overview: String,
) :Parcelable
