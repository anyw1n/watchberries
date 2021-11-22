package alexeyzhizhensky.watchberries.data

import alexeyzhizhensky.watchberries.data.room.User
import alexeyzhizhensky.watchberries.data.room.UserDao
import alexeyzhizhensky.watchberries.network.TokenRequest
import alexeyzhizhensky.watchberries.network.WbApiService
import alexeyzhizhensky.watchberries.utils.suspend
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val userDao: UserDao,
    private val service: WbApiService
) {

    private val mutex = Mutex()

    private var user: User? = null

    suspend fun getUser() = mutex.withLock { user ?: initUser() }

    private suspend fun initUser(): User {
        val user = userDao.get() ?: createUser().also { userDao.insert(it) }
        this.user = user
        return user
    }

    private suspend fun createUser() = FirebaseMessaging.getInstance().token.suspend().let {
        service.createUser(TokenRequest(it)).suspend()
    }

    suspend fun updateToken(token: String) {
        getUser().let { user ->
            service.updateUser(user.id, user.key, TokenRequest(token)).suspend().also {
                userDao.update(it)
            }
        }
    }
}
