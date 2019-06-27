package com.d.ngosapp;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import static android.Manifest.permission.INTERNET;
import static android.Manifest.permission.READ_SMS;
import static android.Manifest.permission.SEND_SMS;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> smsMessagesList = new ArrayList<>();
    ListView messages;
    ArrayAdapter arrayAdapter;
    String[] phonenumberlist;
    String result;
    int lastshownitem;
    int indexBody, indexAddress;
    Button track;
    String ngoname, ngoaddress, ngonumber, ngopassword;
    String msgbody;
    String[] junk, number;
    String donor_no, textbody;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        Intent intent = new Intent(getApplicationContext(), Main2Activity.class);
//        startActivity(intent);
        messages = findViewById(R.id.messages);
        track = findViewById(R.id.track);
        track.setVisibility(View.INVISIBLE);
        populateAutoComplete();
        ngoname = getIntent().getStringExtra("ngoname");
        ngoaddress = getIntent().getStringExtra("ngoaddress");
        ngonumber = getIntent().getStringExtra("ngonumber");
        ngopassword = getIntent().getStringExtra("ngopassword");
        SharedPreferences settings = getSharedPreferences(PopActivity.sharedPreferences,0);
        boolean startedtracking = settings.getBoolean("startedtracking",false);
        if(startedtracking) {
//            Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
//            startActivity(intent);
            track.setVisibility(View.VISIBLE);
        }

        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, smsMessagesList);
        messages.setAdapter(arrayAdapter);
        BackgroundWorker1 backgroundWorker1 = new BackgroundWorker1(this);
        backgroundWorker1.execute("getphonenumber");
        try {
            result = backgroundWorker1.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        phonenumberlist = result.split(":");
        System.out.println("No" +phonenumberlist[0]);
        refreshSmsInbox();

        messages.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                lastshownitem = position;
                Object abc = messages.getItemAtPosition(position);
                msgbody = abc.toString();
                junk = msgbody.split("SMS From: ");
                System.out.println("number"+junk[0]);
                System.out.println("dk"+junk[1]);
                number = junk[1].split("\n");
                System.out.println("num" +number[0]);
                System.out.println("msg"+number[1]);
                Intent intent = new Intent(getApplicationContext(), PopActivity.class);
                intent.putExtra("position", position);
                intent.putExtra("number", number[0]);
                intent.putExtra("body", number[1]);
                intent.putExtra("ngoname", ngoname);
                intent.putExtra("ngoaddress", ngoaddress);
                intent.putExtra("ngonumber", ngonumber);
                intent.putExtra("ngopassword", ngopassword);
                startActivity(intent);
            }
        });
    }

    private void populateAutoComplete() {
        if (!mayRequestReadSMS()) {
            return;
        }
    }

    private boolean mayRequestReadSMS() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            requestPermissions(new String[]{Manifest.permission.READ_SMS, Manifest.permission.READ_CONTACTS}, 666);
        }
        return false;


    }

    public  void refreshSmsInbox() {
        Cursor cursor = getContentResolver().query(Uri.parse("content://sms/inbox"), null, null, null, null);
//        if (cursor.moveToFirst()) { // must check the result to prevent exception
//            do {
//                String msgData = "";
//                for(int idx=0;idx<cursor.getColumnCount();idx++)
//                {
//                    msgData += " " + cursor.getColumnName(idx) + ":" + cursor.getString(idx);
//                }
//                // use msgData
//                messages.setText(msgData);
//            } while (cursor.moveToNext());
//        } else {
//            // empty box, no SMS
//        }

        indexBody = cursor.getColumnIndex("body");
        indexAddress = cursor.getColumnIndex("address");
//        System.out.println(" number" + cursor.getString(indexAddress));
        if (indexBody < 0 || !cursor.moveToFirst()) return;
        System.out.println("working");
        arrayAdapter.clear();
        System.out.println("Working1");
            System.out.println("index" + cursor.getString(indexAddress));
                do {
//                    System.out.println("in do");
                    System.out.println("index" + cursor.getString(indexAddress));
//                    System.out.println("no" + phonenumberlist[0]);
                    for (int i = 0; i < (phonenumberlist.length); i++) {
                        System.out.println("index" + cursor.getString(indexAddress));
                        if (cursor.getString(indexAddress).equals("+91" + phonenumberlist[i])) {
                            String str = "SMS From: " + cursor.getString(indexAddress) + "\n" + cursor.getString(indexBody) + "\n";
                            arrayAdapter.add(str);
                        }
                    }
                } while (cursor.moveToNext());
            }

    public void track(View view) {
        Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
        intent.putExtra("number", donor_no);
        intent.putExtra("body", textbody);
        intent.putExtra("ngoname", ngoname);
        intent.putExtra("ngoaddress", ngoaddress);
        intent.putExtra("ngonumber", ngonumber);
        intent.putExtra("ngopassword", ngopassword);
        startActivity(intent);
    }

    public void logout(View view) {
        SharedPreferences settings = getSharedPreferences(LoginActivity.prefname, 0);
        SharedPreferences.Editor editor = settings.edit();
        //Set "hasloggedin" to true
        editor.putBoolean("hasloggedin", false);
        //Commit the edits
        editor.commit();
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        finish();
    }


//        do {
//            String str = "SMS From: " + cursor.getString(indexAddress) + "\n" + cursor.getString(indexBody) + "\n";
//            arrayAdapter.add(str);
//        } while (cursor.moveToNext());
//    }
}
