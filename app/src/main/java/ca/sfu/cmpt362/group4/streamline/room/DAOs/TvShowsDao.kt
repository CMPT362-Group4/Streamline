package ca.sfu.cmpt362.group4.streamline.room.DAOs

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ca.sfu.cmpt362.group4.streamline.data_models.TvShow
import kotlinx.coroutines.flow.Flow

@Dao
interface TvShowsDao {
    @Query("SELECT * FROM tv_shows ORDER BY roomId DESC")
    fun getAllTvShows(): Flow<List<TvShow>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTvShows(tvShows: TvShow)

    @Delete
    suspend fun deleteTvShow(tvShows: TvShow)

    //use spi service id because it is unique for each TvShow
    @Query("SELECT * FROM tv_shows WHERE id = :tvShowId")
    suspend fun getTvShowById(tvShowId: Long): TvShow?

    @Query("DELETE FROM tv_shows")
    suspend fun deleteAllTvShows()

    @Query("UPDATE tv_shows SET rating = :newRating WHERE id = :id")
    suspend fun updateTvShowsRating(id: Long, newRating: Float)

}