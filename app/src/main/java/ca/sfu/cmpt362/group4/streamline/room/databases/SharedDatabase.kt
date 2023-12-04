package ca.sfu.cmpt362.group4.streamline.room.databases

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ca.sfu.cmpt362.group4.streamline.data_models.SharedContent
import ca.sfu.cmpt362.group4.streamline.room.DAOs.SharedDao

@Database(entities = [SharedContent::class], version = 2)
abstract class SharedDatabase : RoomDatabase() {
    abstract val sharedDao: SharedDao

    companion object{
        @Volatile
        private var INSTANCE: SharedDatabase? = null

        fun getInstance(context: Context) : SharedDatabase{
            synchronized(this){
                var instance = INSTANCE
                if(instance == null){
                    instance = Room.databaseBuilder(context.applicationContext,
                        SharedDatabase::class.java, "shared")
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}