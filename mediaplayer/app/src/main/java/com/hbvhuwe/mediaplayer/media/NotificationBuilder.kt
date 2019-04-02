package com.hbvhuwe.mediaplayer.media

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat.*
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.media.session.MediaButtonReceiver
import com.hbvhuwe.mediaplayer.R
import com.hbvhuwe.mediaplayer.isPlaying
import com.hbvhuwe.mediaplayer.ui.MusicPlayerActivity

const val NOW_PLAYING_CHANNEL: String = "com.hbvhuwe.mediaplayer.music.NOW_PLAYING"
const val NOW_PLAYING_NOTIFICATION: Int = 0xb339
const val REQUEST_CODE = 501

class NotificationBuilder(private val context: Context) {
    private val platformNotificationManager: NotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    private val skipToPreviousAction = NotificationCompat.Action(
        R.drawable.exo_controls_previous,
        context.getString(R.string.notification_skip_to_previous),
        MediaButtonReceiver.buildMediaButtonPendingIntent(context, ACTION_SKIP_TO_PREVIOUS)
    )

    private val playAction = NotificationCompat.Action(
        R.drawable.exo_controls_play,
        context.getString(R.string.notification_play),
        MediaButtonReceiver.buildMediaButtonPendingIntent(context, ACTION_PLAY)
    )

    private val pauseAction = NotificationCompat.Action(
        R.drawable.exo_controls_pause,
        context.getString(R.string.notification_pause),
        MediaButtonReceiver.buildMediaButtonPendingIntent(context, ACTION_PAUSE)
    )

    private val skipToNextAction = NotificationCompat.Action(
        R.drawable.exo_controls_next,
        context.getString(R.string.notification_skip_to_next),
        MediaButtonReceiver.buildMediaButtonPendingIntent(context, ACTION_SKIP_TO_NEXT)
    )

    private val stopPendingIntent =
        MediaButtonReceiver.buildMediaButtonPendingIntent(context, ACTION_STOP)

    fun buildNotification(mediaSession: MediaSessionCompat): Notification {
        if (shouldCreateNowPlayingChannel()) {
            createNowPlayingChannel()
        }

        val controller = MediaControllerCompat(context, mediaSession.sessionToken)
        val description = controller.metadata.description
        val playbackState = controller.playbackState

        val builder = NotificationCompat.Builder(context, NOW_PLAYING_CHANNEL)

        builder.addAction(skipToPreviousAction)

        if (playbackState.isPlaying) {
            builder.addAction(pauseAction)
        } else {
            builder.addAction(playAction)
        }

        builder.addAction(skipToNextAction)

        val mediaStyle = androidx.media.app.NotificationCompat.MediaStyle()
            .setCancelButtonIntent(stopPendingIntent)
            .setMediaSession(mediaSession.sessionToken)
            .setShowActionsInCompactView(1, 2)
            .setShowCancelButton(true)

        return builder.setContentIntent(createContentIntent())
//            .setColor(ContextCompat.getColor(context, R.color.notification_bg))
            .setContentTitle(description.title)
            .setContentText("${description.subtitle} - ${description.description}")
            .setDeleteIntent(stopPendingIntent)
            .setShowWhen(false)
            .setLargeIcon(description.iconBitmap)
            .setOnlyAlertOnce(true)
            .setSmallIcon(R.drawable.ic_notification)
            .setStyle(mediaStyle)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build()
    }

    private fun shouldCreateNowPlayingChannel() =
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !nowPlayingChannelExists()

    @RequiresApi(Build.VERSION_CODES.O)
    private fun nowPlayingChannelExists() =
        platformNotificationManager.getNotificationChannel(NOW_PLAYING_CHANNEL) != null

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNowPlayingChannel() {
        val notificationChannel = NotificationChannel(
            NOW_PLAYING_CHANNEL,
            context.getString(R.string.notification_channel),
            NotificationManager.IMPORTANCE_LOW
        )
            .apply {
                description = context.getString(R.string.notification_channel_description)
            }

        platformNotificationManager.createNotificationChannel(notificationChannel)
    }

    private fun createContentIntent(): PendingIntent {
        val openUi = Intent(context, MusicPlayerActivity::class.java)
        openUi.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        return PendingIntent.getActivity(context, REQUEST_CODE, openUi, PendingIntent.FLAG_CANCEL_CURRENT)
    }
}
