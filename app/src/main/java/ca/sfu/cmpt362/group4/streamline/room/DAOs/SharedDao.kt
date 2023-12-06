package ca.sfu.cmpt362.group4.streamline.room.DAOs

import androidx.room.Dao
import androidx.room.Query
import ca.sfu.cmpt362.group4.streamline.data_models.SharedContent
import kotlinx.coroutines.flow.Flow

@Dao
interface SharedDao {
    @Query("SELECT * FROM shared ORDER BY databaseId DESC")
    fun getAllSharedContent(): Flow<List<SharedContent>>


}