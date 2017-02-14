package com.francis.www.androidservice;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class WebsiteService extends IntentService {

    private static final String TAG = "WebsiteService";
    private static final String URL = "https://androidtutorialpoint.com/lucky_number.php";
    private static final int POLL_INTERVAL = 60 * 1000;

    public WebsiteService(String name) {
        super(name);
    }

    public WebsiteService(){
        super(TAG);
    }




    @Override
    protected void onHandleIntent(Intent intent) {
     if(!isNetworkAvailableAndConnected()){
         return;
     }

     String cancel_req_tag = "login";
        StringRequest strReq = new StringRequest(Request.Method.POST,
                URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register Response: " + response.toString());
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    JSONObject obj = jsonArray.getJSONObject(0);
                    final int luckyNumber = obj.getInt("lucky_number");
                    Notification notification = new NotificationCompat.Builder(WebsiteService.this)
                            .setTicker("Lucky Number")
                            .setSmallIcon(android.R.drawable.ic_menu_report_image)
                            .setContentTitle("Lucky Number: " + luckyNumber)
                            .setAutoCancel(true)
                            .build();
                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(WebsiteService.this);
                    notificationManager.notify(0, notification);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(strReq, cancel_req_tag);
        Log.i(TAG, "Receive an intent: " + intent);
    }

    private boolean isNetworkAvailableAndConnected() {
        ConnectivityManager cm = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
        boolean isNetworkAvailable = cm.getActiveNetworkInfo() != null;
        boolean isNetworkConnected = isNetworkAvailable && cm.getActiveNetworkInfo().isConnected();
        return isNetworkConnected;
    }

   public static Intent newIntent(Context context){
       return new Intent(context, WebsiteService.class);
   }

    public static void setServiceAlarm(Context context, boolean isOn) {
        Intent i = WebsiteService.newIntent(context);
        PendingIntent pi = PendingIntent.getService(context, 0, i, 0);
        AlarmManager alarm = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        if(isOn){
            alarm.setInexactRepeating(AlarmManager.ELAPSED_REALTIME,
                    SystemClock.elapsedRealtime(), POLL_INTERVAL, pi);
        }else {
            alarm.cancel(pi);
            pi.cancel();
        }
    }
}
