package alexeyzhizhensky.watchberries.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface UserDao {

    @Query("SELECT * FROM user LIMIT 1")
    suspend fun get(): User?

    @Insert
    suspend fun insert(user: User)

    @Update
    suspend fun update(user: User)
}
