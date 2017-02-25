package es.ithrek.syncadaptercurrencies;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.NotificationCompat;
import android.view.View;

import es.ithrek.syncadaptercurrencies.activities.MainActivity;

/**
 * Created by Mikel on 25/02/17.
 */

public class Util {
    private static Context context;

    public void useStaticContext(Context context) {
        this.context = context;
    }

    public static void notify(View v, String title, String content) {

        // Create the notification
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
        mBuilder.setSmallIcon(R.drawable.logo);
        mBuilder.setContentTitle(title);
        mBuilder.setContentText(content);

        // Disappear when clicked
        mBuilder.setAutoCancel(true);

        // Optional, notification priority from -2 to 2
        // 2 is the first icon
        // -2 does not shot icon, but it appears in notification tab
        mBuilder.setPriority(2); // default

        // This will be called from the notification
        Intent resultIntent = new Intent(context, MainActivity.class);

        // We create a stack for the new Activity
        // when we enter the notification and click back we will appear in the main.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        // // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        // This SETS the Activity to be called when clicking on notification
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Build notification and NOTIFY
        // notifyId allows us to update the notification, changing the number
        mNotificationManager.notify(0, mBuilder.build());
    }
}
