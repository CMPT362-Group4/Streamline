package ca.sfu.cmpt362.group4.streamline.room.databases

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ca.sfu.cmpt362.group4.streamline.data_models.TvShows
import ca.sfu.cmpt362.group4.streamline.room.DAOs.TvShowsDao

@Database(entities = [TvShows::class], version = 2)
abstract class TvShowsDatabase : RoomDatabase() {
    abstract val tvShowsDao: TvShowsDao

    companion object{
        @Volatile
        private var INSTANCE: TvShowsDatabase? = null

        fun getInstance(context: Context) : TvShowsDatabase{
            synchronized(this){
                var instance = INSTANCE
                if(instance == null){
                    instance = Room.databaseBuilder(context.applicationContext,
                        TvShowsDatabase::class.java, "tv_shows")
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}