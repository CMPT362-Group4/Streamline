package ca.sfu.cmpt362.group4.streamline.repositories
import ca.sfu.cmpt362.group4.streamline.data_models.Movie
import ca.sfu.cmpt362.group4.streamline.data_models.SharedContent
import ca.sfu.cmpt362.group4.streamline.room.DAOs.MovieDao
import ca.sfu.cmpt362.group4.streamline.room.DAOs.SharedDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map


class SharedRepository(private val movieDao: MovieDao) {

    private val apiKey = "40a2698ae5f721766169b5862398f409"

    val savedContent: Flow<List<Movie>> = movieDao.getAllMovies()



    suspend fun getSharedContentById(movieId: Long): Movie? {
        return movieDao.getMovieById(movieId)
    }

}