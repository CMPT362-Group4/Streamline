package ca.sfu.cmpt362.group4.streamline.data_models
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
data class MovieResponse(
    val results: List<Movie>
)

@Entity(tableName = "movies")
@Parcelize
data class Movie(
    //id for saved movies database
    @PrimaryKey(autoGenerate = true)
    val databaseId: Long,

    //this is id from api service
    val id: Long,
    val title: String,
    val poster_path: String,
    val backdrop_path: String,
    val release_date: String,
    val overview: String,
    val rating: Float
) :Parcelable