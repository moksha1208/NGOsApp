package com.d.ngosapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class PopActivity extends Activity {

    public static final String sharedPreferences = "MySaredPreferenceFile";
    TextView tv;
    String donor_no, textbody, position;
    Button ack, journey;
    String ngoname, ngoaddress, ngonumber, ngopassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop);
        tv = findViewById(R.id.tv);
        ack = findViewById(R.id.ack);
        journey = findViewById(R.id.journey);
        journey.setVisibility(View.INVISIBLE);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int)(width*.8), (int)(height*.5));
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.CENTER;
        params.x = 0;
        params.y = -20;
        getWindow().setAttributes(params);
        position = getIntent().getStringExtra("position");
        donor_no = getIntent().getStringExtra("number");
        textbody = getIntent().getStringExtra("body");
        ngoname = getIntent().getStringExtra("ngoname");
        ngoaddress = getIntent().getStringExtra("ngoaddress");
        ngonumber = getIntent().getStringExtra("ngonumber");
        ngopassword = getIntent().getStringExtra("ngopassword");
        tv.setText("SMS From: " + donor_no + "\n" + textbody + "\n");
//        junk = string.split("SMS From: ");
//        System.out.println("number"+junk[0]);
//        System.out.println("dk"+junk[1]);
//        number = junk[1].split("\n");
//        System.out.println("num" +number[0]);
//        System.out.println("msg"+number[1]);

    }

    public void ack(View view) {
        Intent intent = new Intent(getApplicationContext(), Pop2Activity.class);
        intent.putExtra("number",donor_no);
        intent.putExtra("body",textbody);
        intent.putExtra("ngoname", ngoname);
        intent.putExtra("ngoaddress", ngoaddress);
        intent.putExtra("ngonumber", ngonumber);
        intent.putExtra("ngopassword", ngopassword);
        startActivity(intent);
//        sm.sendTextMessage(msg[0], null, msg[1], null, null);
        journey.setVisibility(View.VISIBLE);
        ack.setVisibility(View.INVISIBLE);
        position = "1";
    }

    public void journey(View view) {
        SmsManager sm = SmsManager.getDefault();
        String message = "Start Tracking. The representative has begun the journey";
        ArrayList<String> parts =sm.divideMessage(message);
        sm.sendMultipartTextMessage(donor_no, null, parts, null, null);
        SharedPreferences settings = getSharedPreferences(PopActivity.sharedPreferences, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("startedtracking", true);
//        Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
//        startActivity(intent);
        //Commit the edits
        editor.commit();
        Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
        intent.putExtra("number", donor_no);
        intent.putExtra("body", textbody);
        intent.putExtra("ngoname", ngoname);
        intent.putExtra("ngoaddress", ngoaddress);
        intent.putExtra("ngonumber", ngonumber);
        intent.putExtra("ngopassword", ngopassword);
        startActivity(intent);
    }
}
