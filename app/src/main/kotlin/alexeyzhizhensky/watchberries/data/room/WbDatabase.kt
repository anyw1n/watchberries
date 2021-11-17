package alexeyzhizhensky.watchberries.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [
        User::class,
        ProductRemoteKey::class,
        Product::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class WbDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun productRemoteKeyDao(): ProductRemoteKeyDao
    abstract fun productDao(): ProductDao

    companion object {

        private const val DATABASE_NAME = "watchberries-db"

        fun create(context: Context) =
            Room.databaseBuilder(context, WbDatabase::class.java, DATABASE_NAME).build()
    }
}
