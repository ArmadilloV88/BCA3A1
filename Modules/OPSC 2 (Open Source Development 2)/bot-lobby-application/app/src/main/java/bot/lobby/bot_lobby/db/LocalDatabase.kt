package bot.lobby.bot_lobby.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

import bot.lobby.bot_lobby.dao.SessionDao
import bot.lobby.bot_lobby.dao.TeamDao
import bot.lobby.bot_lobby.models.IdAndRoleConverters
import bot.lobby.bot_lobby.models.Session
import bot.lobby.bot_lobby.models.SessionConverters

import bot.lobby.bot_lobby.models.Team

@Database(
    entities = [Team::class, /*User::class,*/ Session::class],
    version = 6
)
@TypeConverters(IdAndRoleConverters::class, /*TeamIdsConverters::class,*/ SessionConverters::class)
abstract class LocalDatabase : RoomDatabase() {
    abstract val teamDao: TeamDao

//    abstract val userDao: UserDao

    abstract val sessionDao: SessionDao

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: LocalDatabase? = null

        fun getDatabase(context: Context): LocalDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LocalDatabase::class.java,
                    name = "local-db"

                ).fallbackToDestructiveMigration()
                    .build()
                    
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}