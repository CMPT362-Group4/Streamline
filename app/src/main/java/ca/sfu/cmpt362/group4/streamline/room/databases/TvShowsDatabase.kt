package ca.sfu.cmpt362.group4.streamline.room.databases

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ca.sfu.cmpt362.group4.streamline.data_models.TvShow
import ca.sfu.cmpt362.group4.streamline.room.DAOs.TvShowsDao

@Database(entities = [TvShow::class], version = 6)
abstract class TvShowsDatabase : RoomDatabase() {
    abstract val tvShowsDao: TvShowsDao

    companion object{
        private val instances: MutableMap<String, TvShowsDatabase> = mutableMapOf()

        fun getInstance(context: Context, userId: String) : TvShowsDatabase{
            synchronized(this){
                var instance = TvShowsDatabase.instances[userId]
                if(instance == null){
                    instance = Room.databaseBuilder(context.applicationContext,
                        TvShowsDatabase::class.java, "tv_shows_$userId")
                        .fallbackToDestructiveMigration()
                        .build()
                    instances[userId] = instance
                }
                return instance
            }
        }
    }
}