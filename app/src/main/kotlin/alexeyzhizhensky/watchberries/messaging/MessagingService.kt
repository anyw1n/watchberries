package alexeyzhizhensky.watchberries.messaging

import alexeyzhizhensky.watchberries.R
import alexeyzhizhensky.watchberries.WbNotificationManager
import alexeyzhizhensky.watchberries.data.UserRepository
import android.app.ActivityManager
import android.graphics.Bitmap
import androidx.core.graphics.drawable.toBitmap
import androidx.core.os.bundleOf
import androidx.navigation.NavDeepLinkBuilder
import coil.ImageLoader
import coil.request.ImageRequest
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var userRepository: UserRepository

    @Inject
    lateinit var notificationManager: WbNotificationManager

    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)

    private val navDeepLinkBuilder by lazy {
        NavDeepLinkBuilder(this@MessagingService)
            .setGraph(R.navigation.nav_graph)
            .setDestination(R.id.productDetailFragment)
    }

    private val imageLoader by lazy { ImageLoader.invoke(this) }

    override fun onNewToken(token: String) {
        serviceScope.launch(Dispatchers.IO) {
            runCatching {
                userRepository.updateToken(token)
            }
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        if (isAppInForeground()) return
        serviceScope.launch {
            val message = Message.fromData(remoteMessage.data) ?: return@launch

            val text = getString(
                message.type.textResId,
                message.title,
                message.price,
                message.priceDiff
            )

            val pendingIntent = navDeepLinkBuilder
                .setArguments(bundleOf("sku" to message.sku))
                .createPendingIntent()

            notificationManager.buildNotificationAndPost(
                title = getString(message.type.titleResId),
                text = text,
                pendingIntent = pendingIntent,
                largeIcon = loadImage(message.imageUrl)
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceJob.cancel()
    }

    private fun isAppInForeground(): Boolean {
        val processInfo = ActivityManager.RunningAppProcessInfo()
        ActivityManager.getMyMemoryState(processInfo)
        return processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
    }

    private suspend fun loadImage(url: String): Bitmap? {
        val request = ImageRequest.Builder(this)
            .data(url)
            .build()
        return imageLoader.execute(request).drawable?.toBitmap()
    }
}
