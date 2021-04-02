package com.tahadroid.tripaway.alarm;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RemoteViews;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;
import androidx.core.app.NotificationCompat;

import com.tahadroid.tripaway.R;
import com.tahadroid.tripaway.models.Note;
import com.tahadroid.tripaway.models.Trip;
import com.tahadroid.tripaway.ui.MainActivity;
import com.tahadroid.tripaway.ui.map.FloatWidgetService;
import com.tahadroid.tripaway.ui.map.Maps;

import java.util.ArrayList;
import java.util.List;

public class AlarmService extends JobIntentService {
    private static Trip trip;
    private WindowManager mWindowManager;
    private View mFloatingWidget;
    private View collapsedView;
    private View expandedView;
    public static final String BROADCAST_ACTION = "magicbox";
    private static final int MAX_CLICK_DURATION = 200;
    private long startClickTime;
    ImageButton closeButton;
    Button snoozeButton;
    Button startButton;
    TextView titleText;
    TextView notificationText;
    Button notoficationButton;
    static String title;
    static Double lon;
    static Double lat;


    public static void enqueueWork(Context context, Intent intent) {
        enqueueWork(context, AlarmService.class, 1, intent);
        title = intent.getStringExtra("event");
        lon = Double.valueOf(intent.getStringExtra("lon"));
        lat = Double.valueOf(intent.getStringExtra("lat"));
    }


    @Override
    public IBinder onBind(Intent intent) {


        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return START_STICKY;

    }

    @Override
    public void onCreate() {
        super.onCreate();
        trip = new Trip();

        mFloatingWidget = LayoutInflater.from(this).inflate(R.layout.layout_alarm_dialog, null);

        closeButton = mFloatingWidget.findViewById(R.id.btn_close);
        snoozeButton = mFloatingWidget.findViewById(R.id.btn_snooze);
        startButton = mFloatingWidget.findViewById(R.id.btn_start);
        titleText = mFloatingWidget.findViewById(R.id.text_title);
        String text = AlarmService.title;
        titleText.setText(text);



        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
                        ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                        : WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.CENTER;
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mWindowManager.addView(mFloatingWidget, params);


        Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        Ringtone ringtone = RingtoneManager.getRingtone(getApplicationContext(), notificationSound);
        startAlarmRingTone(ringtone);

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mWindowManager.removeView(mFloatingWidget);
                stopAlarmRingTone(ringtone);
            }
        });

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mapIntent = new Intent(getApplicationContext() , MainActivity.class);
                mapIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(mapIntent);
                mWindowManager.removeView(mFloatingWidget);
                stopAlarmRingTone(ringtone);


            }
        });

        snoozeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mWindowManager.removeView(mFloatingWidget);
                stopAlarmRingTone(ringtone);

                Intent intent1 = new Intent(getApplicationContext(), MainActivity.class);
                intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 1, intent1, PendingIntent.FLAG_ONE_SHOT);
                NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), "notify_001");


                RemoteViews contentView = new RemoteViews(getApplicationContext().getPackageName(), R.layout.notification_layout);
                contentView.setImageViewResource(R.id.image, R.drawable.logo);
                contentView.setTextViewText(R.id.text_notification , title);


                mBuilder.setSmallIcon(R.drawable.logo);
                mBuilder.setAutoCancel(true);
                mBuilder.setOngoing(true);
                mBuilder.setPriority(Notification.PRIORITY_HIGH);
                mBuilder.setOnlyAlertOnce(true);
                mBuilder.build().flags = Notification.FLAG_NO_CLEAR | Notification.PRIORITY_HIGH;
                mBuilder.setContent(contentView);
                mBuilder.setContentIntent(pendingIntent);


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    String channelId = "channel_id";
                    NotificationChannel channel = new NotificationChannel(channelId, "channel name", NotificationManager.IMPORTANCE_HIGH);
                    channel.enableVibration(true);
                    notificationManager.createNotificationChannel(channel);
                    mBuilder.setChannelId(channelId);
                }
                Notification notification = mBuilder.build();
                notificationManager.notify(1, notification);
            }
        });
    }


    @Override
    public void onDestroy() {

//        if (mFloatingWidget != null) mWindowManager.removeView(mFloatingWidget);
        super.onDestroy();
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {

    }


    public void startAlarmRingTone(Ringtone r) {
        r.play();
    }

    public void stopAlarmRingTone(Ringtone r) {
        r.stop();
    }


}