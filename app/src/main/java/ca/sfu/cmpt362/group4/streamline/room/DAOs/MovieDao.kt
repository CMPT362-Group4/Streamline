package ca.sfu.cmpt362.group4.streamline.room.DAOs

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import ca.sfu.cmpt362.group4.streamline.data_models.Movie
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDao {
    @Query("SELECT * FROM movies ORDER BY databaseId DESC")
    fun getAllMovies(): Flow<List<Movie>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMovie(movie: Movie)

    @Delete
    suspend fun deleteMovie(movie: Movie)

    //use spi service id because it is unique for each movie
    @Query("SELECT * FROM movies WHERE id = :movieId")
    suspend fun getMovieById(movieId: Long): Movie?

    @Query("DELETE FROM movies")
    suspend fun deleteAllMovies()

    @Query("UPDATE movies SET rating = :newRating WHERE databaseId = :databaseId")
    suspend fun updateMovieRating(databaseId: Long, newRating: Float)

}