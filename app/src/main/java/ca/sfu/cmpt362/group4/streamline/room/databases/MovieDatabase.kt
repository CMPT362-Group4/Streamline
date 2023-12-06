package ca.sfu.cmpt362.group4.streamline.room.databases

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ca.sfu.cmpt362.group4.streamline.data_models.Movie
import ca.sfu.cmpt362.group4.streamline.room.DAOs.MovieDao

@Database(entities = [Movie::class], version = 5)
abstract class MovieDatabase : RoomDatabase() {
    abstract val movieDao: MovieDao

    companion object{
        private val instances: MutableMap<String, MovieDatabase> = mutableMapOf()

        fun getInstance(context: Context, userId: String) : MovieDatabase{
            synchronized(this){
                var instance = instances[userId]
                if(instance == null){
                    instance = Room.databaseBuilder(context.applicationContext,
                        MovieDatabase::class.java, "movies_$userId")
                        .fallbackToDestructiveMigration()
                        .build()
                    instances[userId] = instance
                }
                return instance
            }
        }
    }
}