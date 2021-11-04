package alexeyzhizhensky.watchberries.data

import alexeyzhizhensky.watchberries.api.WatchberriesApiService
import alexeyzhizhensky.watchberries.utils.asSuspend
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@ExperimentalCoroutinesApi
class UserRepository @Inject constructor(
    private val userDao: UserDao,
    private val service: WatchberriesApiService
) {

    suspend fun isUserExists() = userDao.isUserExists()

    suspend fun getUser() = userDao.get()

    suspend fun createUser(token: String) {
        val user = service.createUser(TokenRequestBody(token)).asSuspend().body()
            ?: return

        userDao.insert(user)
    }

    suspend fun updateToken(token: String) {
        val user = getUser()
        val updatedUser = service.updateUser(
            user.id,
            user.key,
            TokenRequestBody(token)
        ).asSuspend().body() ?: return

        userDao.update(updatedUser)
    }

    // TODO: 11/4/2021 add network listener
    suspend fun addSku(sku: Int) {
        val user = getUser()
        val updatedUser = service.addSku(
            user.id,
            user.key,
            SkuRequestBody(sku)
        ).asSuspend().body() ?: return

        userDao.update(updatedUser)
    }

    suspend fun removeSku(sku: Int) {
        val user = getUser()
        val updatedUser = service.deleteSku(
            user.id,
            user.key,
            SkuRequestBody(sku)
        ).asSuspend().body() ?: return

        userDao.update(updatedUser)
    }
}
