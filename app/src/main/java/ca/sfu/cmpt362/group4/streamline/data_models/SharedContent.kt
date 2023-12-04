package ca.sfu.cmpt362.group4.streamline.data_models
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
data class SharedContentResponse(
    val results: List<SharedContent>
)

@Entity(tableName = "shared")
@Parcelize
data class SharedContent(
    @PrimaryKey(autoGenerate = true)
    val databaseId: Long,

    val title: String,
    val release_date: String,

) :Parcelable