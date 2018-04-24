package id.suksesit.pensil;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Created by ozi on 12/09/2017.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "FCM Service";
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        Log.d(TAG, "Notif: " + remoteMessage.getNotification().getBody());
        handleNotification(remoteMessage.getNotification().getBody());
        /*JSONObject message = new JSONObject();
        Set<String> keys = remoteMessage.getData().keySet();
        for (String key : keys) {
            try {
                message.put(key, remoteMessage.getData().get(key));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }*/


        String title="", bodyMessage="", image="", action="", dataAction="";
        /*try {
            title = message.getString("title");
            bodyMessage = message.getString("message");
            image = message.getString("image");
            action = message.getString("action");
            dataAction = message.getString("data_action");
        } catch (JSONException e) {
            e.printStackTrace();
        }*/

        sendNotification(title, bodyMessage, image, action, dataAction);
    }

    private void handleNotification(String body) {
        Intent pushNotification = new Intent(NotifConfig.STR_PUSH);
        pushNotification.putExtra("message",body);
        LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);
    }


    private void sendNotification(String title, String bodyMessage, String image, String action, String dataAction) {

        Intent goPage;

        if(action.equals("sync-and-go")){
            goPage = new Intent(this, MainActivity.class);
            goPage.putExtra("action", action);
            goPage.putExtra("data_action", dataAction);
        } else {
            goPage = new Intent(this, MainActivity.class);
        }


        goPage.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 , goPage,
                PendingIntent.FLAG_ONE_SHOT);


        //Setung notification sound
        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        //Getting bigPicture
        Bitmap remote_picture = null;
        NotificationCompat.BigPictureStyle notificationBuilderPicture;

        if(!image.equals("")){
            notificationBuilderPicture = new
                    NotificationCompat.BigPictureStyle();
            try {
                remote_picture = BitmapFactory.decodeStream(
                        (InputStream) new URL(image).getContent());

            } catch (IOException e) {
                e.printStackTrace();
            }
            notificationBuilderPicture.bigPicture(remote_picture).setSummaryText(bodyMessage);
        } else {
            notificationBuilderPicture = null;
        }

        //Build the notification
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setColor(Color.parseColor("#000000"))
                .setContentTitle(title) //top title
                .setContentText(bodyMessage) //message-text from firebaseconsole
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setStyle(notificationBuilderPicture)
                .setContentIntent(pendingIntent)
                /*.setSmallIcon(image)*/;


        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(1, notificationBuilder.build());

    }
}
