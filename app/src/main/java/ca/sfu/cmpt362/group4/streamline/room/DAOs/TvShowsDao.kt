package ca.sfu.cmpt362.group4.streamline.room.DAOs

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ca.sfu.cmpt362.group4.streamline.data_models.TvShows
import kotlinx.coroutines.flow.Flow

@Dao
interface TvShowsDao {
    @Query("SELECT * FROM tv_shows ORDER BY databaseId DESC")
    fun getAllTvShows(): Flow<List<TvShows>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTvShows(tvShows: TvShows)

    @Delete
    suspend fun deleteTvShows(tvShows: TvShows)

    //use spi service id because it is unique for each TvShows
    @Query("SELECT * FROM tv_shows WHERE id = :tvShowsId")
    suspend fun getTvShowsById(tvShowsId: Long): TvShows?

    @Query("DELETE FROM tv_shows")
    suspend fun deleteAllTvShows()

    @Query("UPDATE tv_shows SET rating = :newRating WHERE databaseId = :databaseId")
    suspend fun updateTvShowsRating(databaseId: Long, newRating: Float)

}