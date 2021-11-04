package alexeyzhizhensky.watchberries.data

import alexeyzhizhensky.watchberries.utils.Constants.DATABASE_NAME
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@TypeConverters(Converters::class)
@Database(entities = [User::class], version = 1, exportSchema = false)
abstract class WbDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao

    companion object {

        @Volatile
        private var instance: WbDatabase? = null

        fun getInstance(context: Context) = instance ?: synchronized(this) {
            instance ?: Room.databaseBuilder(context, WbDatabase::class.java, DATABASE_NAME)
                .build().also { instance = it }
        }
    }
}
