package alexeyzhizhensky.watchberries.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface UserDao {

    @Query("SELECT EXISTS(SELECT * FROM user)")
    suspend fun isUserExists(): Boolean

    @Query("SELECT * FROM user LIMIT 1")
    suspend fun get(): User

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(user: User)

    @Update
    suspend fun update(user: User)
}
