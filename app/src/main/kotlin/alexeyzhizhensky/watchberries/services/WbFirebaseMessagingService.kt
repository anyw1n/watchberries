package alexeyzhizhensky.watchberries.services

import com.google.firebase.messaging.FirebaseMessagingService

class WbFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        // TODO: 10/30/2021 send token to server
    }
}
