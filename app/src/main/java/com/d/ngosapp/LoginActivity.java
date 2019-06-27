package com.d.ngosapp;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.concurrent.ExecutionException;

public class LoginActivity extends AppCompatActivity {

    public static final String prefname = "MySaredPreferenceFile";
    Spinner ngos;
    String[] list;
    String[] ngoname;
    String result, selectedngo, selectedaddress, selectednumber, selectedpassword;
    EditText password;
    Button login, newacc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        SharedPreferences settings = getSharedPreferences(LoginActivity.prefname,0);
        boolean hasloggedin = settings.getBoolean("hasloggedin",false);
        if(hasloggedin) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.putExtra("ngoname", settings.getString("ngoname", ""));
            intent.putExtra("ngoaddress", settings.getString("ngoaddress", ""));
            intent.putExtra("ngonumber", settings.getString("ngonumber", ""));
            intent.putExtra("ngopassword", settings.getString("ngopassword", ""));
            startActivity(intent);
            finish();
        }
        ngos = findViewById(R.id.ngos);
        password = findViewById(R.id.password);
        login = findViewById(R.id.login);
        newacc = findViewById(R.id.newacc);

        populateAutoComplete();
        BackgroundWorker1 backgroundWorker3 = new BackgroundWorker1(this);
        backgroundWorker3.execute("sendmessage");
        try {
            result = backgroundWorker3.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        list = result.split(":");
        for ( int k = 0 ; k < list.length; k++) {
            System.out.println(list[k]);
        }
        ngoname = new String[list.length/4];
        for ( int i = 0 , j = 0; i < (list.length) ; i = i + 4, j = j + 1) {
            ngoname[j] = list[i];
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, ngoname);
        ngos.setAdapter(dataAdapter);
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
            requestPermissions(new String[]{Manifest.permission.READ_SMS, Manifest.permission.READ_CONTACTS, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 666);
        }
        return false;


    }

    public void login(View view) {
        int i = ngos.getSelectedItemPosition();
        selectedngo = list[i*4];
        selectedaddress = list[(i*4)+1];
        selectednumber = list[(i*4)+2];
        selectedpassword = list[(i*4)+3];
        System.out.println(selectedngo + selectedaddress + selectednumber + selectedpassword);
        if(password.getText().toString().equals(selectedpassword)) {
            SharedPreferences settings = getSharedPreferences(LoginActivity.prefname, 0);
            SharedPreferences.Editor editor = settings.edit();
            //Set "hasloggedin" to true
            editor.putBoolean("hasloggedin", true);
            editor.putString("ngoname", selectedngo);
            editor.putString("ngoaddress", selectedaddress);
            editor.putString("ngonumber", selectednumber);
            editor.putString("ngopassword", selectedpassword);
            //Commit the edits
            editor.commit();
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.putExtra("ngoname", selectedngo);
            intent.putExtra("ngoaddress", selectedaddress);
            intent.putExtra("ngonumber", selectednumber);
            intent.putExtra("ngopassword", selectedpassword);
            startActivity(intent);
            finish();
        } else {
            password.setError("Incorrect Password");
        }
    }

    public void newacc(View view) {
        Intent intent = new Intent(getApplicationContext(), NewAccount.class);
        startActivity(intent);
    }
}
