package alexeyzhizhensky.watchberries.services

import com.google.firebase.messaging.FirebaseMessagingService
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WbFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        // TODO: 10/30/2021 send token to server
    }
}
