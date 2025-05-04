package com.example.flo

import android.app.*
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.*

class ForegroundService : Service() {
    private val channelId = Constant.CHANNEL_ID
    private val notiId = Constant.MUSIC_NOTIFICATION_ID
    private val scope = CoroutineScope(Dispatchers.Default + Job())

    override fun onBind(intent: Intent): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val chan = NotificationChannel(channelId, "Music", NotificationManager.IMPORTANCE_LOW)
            getSystemService(NotificationManager::class.java).createNotificationChannel(chan)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // 0부터 시작 알림
        startForeground(notiId, buildNotification(0))
        // 코루틴으로 1..1000 카운트
        scope.launch {
            for (i in 1..1000) {
                delay(50)
                (getSystemService(NOTIFICATION_SERVICE) as NotificationManager)
                    .notify(notiId, buildNotification(i))
            }
        }
        return START_STICKY
    }

    private fun buildNotification(progress: Int): Notification {
        // 메인 화면으로 복귀만 하면 충분하므로 MainActivity 사용
        val pi = PendingIntent.getActivity(
            this, 0,
            Intent(this, MainActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK),
            PendingIntent.FLAG_IMMUTABLE
        )
        return NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.app_icon)       // 실제 리소스 확인!
            .setContentTitle("음악 재생 중")
            .setContentText("진행: $progress / 1000")
            .setProgress(1000, progress, false)
            .setOnlyAlertOnce(true)
            .setOngoing(true)
            .setContentIntent(pi)
            .build()
    }

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }
}