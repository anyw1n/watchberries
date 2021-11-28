package alexeyzhizhensky.watchberries

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class WbNotificationManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val notificationManager = NotificationManagerCompat.from(context)

    private val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_notification_icon)
        .setColor(ContextCompat.getColor(context, R.color.blue_bayoux))
        .setColorized(true)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        .setAutoCancel(true)

    init {
        createChannel()
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val name = context.getString(R.string.notification_channel_name)
        val description = context.getString(R.string.notification_channel_description)
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            this.description = description
            lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        }
        notificationManager.createNotificationChannel(channel)
    }

    fun buildNotificationAndPost(
        title: String,
        text: String,
        pendingIntent: PendingIntent,
        largeIcon: Bitmap?
    ) {
        val bigPictureStyle = if (largeIcon != null) {
            NotificationCompat.BigPictureStyle()
                .bigPicture(largeIcon)
                .bigLargeIcon(null)
        } else {
            null
        }
        val notification = notificationBuilder
            .setContentTitle(title)
            .setContentText(text)
            .setLargeIcon(largeIcon)
            .setStyle(bigPictureStyle)
            .setContentIntent(pendingIntent)
            .build()
        notificationManager.notify(Random.nextInt(), notification)
    }

    fun clearNotifications() {
        notificationManager.cancelAll()
    }

    private companion object {

        const val CHANNEL_ID = "PRODUCT_UPDATES"
    }
}
