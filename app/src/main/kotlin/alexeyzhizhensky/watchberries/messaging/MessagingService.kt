package alexeyzhizhensky.watchberries.messaging

import alexeyzhizhensky.watchberries.MainActivity
import alexeyzhizhensky.watchberries.WbNotificationManager
import alexeyzhizhensky.watchberries.data.UserRepository
import android.app.ActivityManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import androidx.core.graphics.drawable.toBitmap
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

    private val pendingIntent by lazy {
        val activityIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_IMMUTABLE
        } else {
            0
        }
        PendingIntent.getActivity(this, 0, activityIntent, flags)
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
                message.productTitle,
                message.productPrice,
                message.productPriceDiff
            )

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
