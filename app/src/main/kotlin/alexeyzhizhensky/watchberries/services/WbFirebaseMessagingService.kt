package alexeyzhizhensky.watchberries.services

import alexeyzhizhensky.watchberries.data.UserRepository
import com.google.firebase.messaging.FirebaseMessagingService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
@ExperimentalCoroutinesApi
class WbFirebaseMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var userRepository: UserRepository

    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)

    override fun onNewToken(token: String) {
        serviceScope.launch(Dispatchers.IO) {
            if (userRepository.isUserExists()) {
                userRepository.updateToken(token)
            } else {
                userRepository.createUser(token)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        serviceJob.cancel()
    }
}
