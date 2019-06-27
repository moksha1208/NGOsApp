package com.d.ngosapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import java.util.ArrayList;

public class Pop2Activity extends AppCompatActivity {

    String repname, timefrom, timeto, msg, contactinfo;
    EditText name, from, to, contact;
    String ngoname, ngoaddress, ngonumber, ngopassword;
    String donor_no, textbody;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop2);
        name = findViewById(R.id.name);
        contact = findViewById(R.id.contact);
        from = findViewById(R.id.from);
        to = findViewById(R.id.to);

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
        donor_no = getIntent().getStringExtra("number");
        textbody = getIntent().getStringExtra("body");
        ngoname = getIntent().getStringExtra("ngoname");
        ngoaddress = getIntent().getStringExtra("ngoaddress");
        ngonumber = getIntent().getStringExtra("ngonumber");
        ngopassword = getIntent().getStringExtra("ngopassword");
    }

    public void sendsms(View view) {
        repname = name.getText().toString();
        contactinfo = contact.getText().toString();
        timefrom = from.getText().toString();
        timeto = to.getText().toString();
        msg = "Acknowledgement. We have accepted your donation. Our Delivery Executive " + repname + " will pick up the donation between " + timefrom + " and " + timeto +". You can contact our representative at " + contactinfo + " Thank You.";
        SmsManager sm = SmsManager.getDefault();
        ArrayList<String> parts =sm.divideMessage(msg);
        sm.sendMultipartTextMessage(donor_no, null, parts, null, null);
//        sm.sendTextMessage(number, null, msg, null, null);
//        Intent intent = new Intent(getApplicationContext(), PopActivity.class);
//        intent.putExtra("number", donor_no);
//        intent.putExtra("body", textbody);
//        intent.putExtra("ngoname", ngoname);
//        intent.putExtra("ngoaddress", ngoaddress);
//        intent.putExtra("ngonumber", ngonumber);
//        intent.putExtra("ngopassword", ngopassword);
//        startActivity(intent);
        this.finish();
    }

//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        Intent intent = new Intent(getApplicationContext(), PopActivity.class);
//        intent.putExtra("number", number);
//        intent.putExtra("ngoname", ngoname);
//        intent.putExtra("ngoaddress", ngoaddress);
//        intent.putExtra("ngonumber", ngonumber);
//        intent.putExtra("ngopassword", ngopassword);
//        startActivity(intent);
//        finish();
//    }
}
