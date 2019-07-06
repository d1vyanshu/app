package com.example.mymoney;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.database.Cursor;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;

public class ApplicationClass extends Application {
    int totalSpent = 10;
    public static String TAG = "ApplicationClass";

    public static ArrayList<Budget> budget_list;
    public static ArrayList<home_budget> home_budget_list;

    public static ArrayList<Transaction> transaction_list;

    private final String CHANNEL_ID = "personal notifications";


    @Override
    public void onCreate() {
        super.onCreate();
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        BudgetDatabaseHelper bdhelper = new BudgetDatabaseHelper(this);
        Cursor cursor = (Cursor) databaseHelper.getTransactions();
        Cursor bdcursor = (Cursor) bdhelper.getTransactions();
        Calendar cal = Calendar.getInstance();
        int currentMonth = cal.get(Calendar.MONTH);

        StringBuffer sb = new StringBuffer();
        Integer[] months = new Integer[13];
        for (int i = 0; i < 13; i++) {
            months[i] = 0;
        }
        while (cursor.moveToNext()) {
            sb.append(cursor.getString(1) + "---> " + cursor.getString(2) + "\n");
            int month = Integer.parseInt("" + sb.charAt(5) + sb.charAt(6));
            months[month] += Integer.parseInt(cursor.getString(2));
            months[12] = Integer.parseInt("" + sb.charAt(0) + sb.charAt(1) + sb.charAt(2) + sb.charAt(3));
            String monthString = new DateFormatSymbols().getMonths()[month-1];
        }

        Log.d(TAG, "Current Month   --- " + currentMonth);

        Log.d(TAG, sb.toString());

        ArrayList<String> arrayList = new ArrayList<String>();
        for (int s : months) {
            arrayList.add(String.valueOf(s));
        }

        totalSpent = months[currentMonth];


        budget_list = new ArrayList<Budget>();

        while(bdcursor.moveToNext()){
            String month;
            String amount;
            month = bdcursor.getString(0);
            amount = bdcursor.getString(1);
            String monthString = new DateFormatSymbols().getMonths()[Integer.parseInt(month)-1];
            budget_list.add(new Budget(amount,monthString));

        }

        home_budget_list = new ArrayList<home_budget>();
        home_budget_list.add(new home_budget("Budget:5000","Spent :" + Integer.toString(totalSpent), "Spend Today:500","1 June 2019","30 June 2019", "2 Days Left"));


        transaction_list = new ArrayList<Transaction>();
        int emptydb = 1;

        Cursor cursor2 = (Cursor) databaseHelper.ReverseDB();
        StringBuffer sb2 = new StringBuffer();
        while (cursor2.moveToNext()) {
            emptydb = 0;
            sb2.append(cursor2.getString(1) + "---> " + cursor2.getString(2) + "\n");
            int month = Integer.parseInt("" + sb2.charAt(5) + sb2.charAt(6));
            String monthString = new DateFormatSymbols().getMonths()[month-1];
            String date = cursor2.getString(1).charAt(8)+""+cursor2.getString(1).charAt(9)+" "+monthString;
            String amount = cursor2.getString(2);

            if(amount.charAt(0) == '-') {
                amount = amount.substring(1);
                Log.d("dadad","cleared array");
                transaction_list.add(new Transaction("Paytm to Ambani", date, amount, "0"));
            }
            else
                transaction_list.add(new Transaction("Paytm to Ambani",date,"0",amount));
        }

        if(emptydb == 1)
        transaction_list.add(new Transaction("No transactions to Show","","0","0"));

    }

        public void notify (View view){
        createchannel();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID);
                builder.setSmallIcon(R.drawable.ic_icon);
                builder.setContentTitle("My notification");
                builder.setContentText("Hello World!");
                builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        notificationManager.notify(1, builder.build());
    }

    public void createchannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "Personal Notifcations";
            String desc = "Include all notificatons";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID,name,importance);
            notificationChannel.setDescription(desc);
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }
}
