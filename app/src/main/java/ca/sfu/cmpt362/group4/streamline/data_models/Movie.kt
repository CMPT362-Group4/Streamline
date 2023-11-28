package ca.sfu.cmpt362.group4.streamline.data_models
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
data class MovieResponse(
    val results: List<Movie>
)

@Parcelize
data class Movie(
    val id: Int,
    val title: String,
    val poster_path: String,
    val backdrop_path: String,
    val release_date: String,
    val overview: String,
) :Parcelable