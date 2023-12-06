package ca.sfu.cmpt362.group4.streamline.data_models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

data class TvResponse(
    val results: List<TvShow>
)

@Entity(tableName = "tv_shows")
@Parcelize
data class TvShow(
    //id for saved tvShows database
    @PrimaryKey(autoGenerate = true)
    val roomId: Long,

    //this is id from api service
    val id: Long,
    val name: String,
    val poster_path: String,
    val backdrop_path: String,
    val first_air_date: String,
    val overview: String,
    var rating: Float
) :Parcelable {
    // No-argument constructor
    constructor() : this(0, 0, "", "", "", "", "", 0.0f)
}

