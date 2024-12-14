package com.example.musicservice07122024;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
//https://www.youtube.com/watch?v=BXwDM5VVuKA
//https://www.youtube.com/watch?v=YZL-_XJSClc
//https://developer.android.com/develop/background-work/services/fgs/stop-fgs

public class MusicService extends Service {

    private static final String CHANNEL_ID = "CanalMusica";
    private static final int NOTIFICATION_ID = 1;
    private MediaPlayer mediaPlayer;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        criaNotificacaoCanal();
        mediaPlayer = MediaPlayer.create(this, R.raw.mp3);
        if (mediaPlayer != null) {
            mediaPlayer.setLooping(true);
        }
        Log.i("MusicService", "Service created");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                notificationIntent,
                PendingIntent.FLAG_IMMUTABLE
        );


        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Música APP")
                .setContentText("Reproduzindo")
                //<a href="https://www.flaticon.com/br/icones-gratis/nota-musical" title="nota musical ícones">Nota musical ícones criados por Freepik - Flaticon</a>
                .setSmallIcon(R.drawable.nota_musical)
                .setContentIntent(pendingIntent)
                .build();

        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            Log.i("MusicService", "Service started");
        }

        startForeground(NOTIFICATION_ID, notification);
        return START_STICKY;
    }

    private void criaNotificacaoCanal() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Música Tocando",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            notificationChannel.setDescription("Canal para serviço de música");

            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {

                manager.createNotificationChannel(notificationChannel);
                Log.i("MusicService", "Canal de notificação criado: " + CHANNEL_ID);
            } else {
                Log.e("MusicService", "Erro ao obter NotificationManager");
            }
        }
    }

    @Override
    public void onDestroy() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
            Log.i("MusicService", "Service destroyed");
        }
        stopForeground(STOP_FOREGROUND_REMOVE);
        super.onDestroy();
    }
}
