package ca.sfu.cmpt362.group4.streamline.data_models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

data class TvResponse(
    val results: List<TvShows>
)

@Entity(tableName = "tv_shows")
@Parcelize
data class TvShows(
    //id for saved tvShows database
    @PrimaryKey(autoGenerate = true)
    val databaseId: Long,

    //this is id from api service
    val id: Long,
    val name: String,
    val poster_path: String,
    val backdrop_path: String,
    val first_air_date: String,
    val overview: String,
    var rating: Float
) :Parcelable
