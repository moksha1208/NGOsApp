package com.d.ngosapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class NewAccount extends AppCompatActivity {

    EditText ngo_name, ngo_address, ngo_number, ngo_password, ngo_repassword;
    Button createacc;
    String ngoname, ngoaddress, ngonumber, ngopassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_account);
        ngo_name = findViewById(R.id.ngoname);
        ngo_address = findViewById(R.id.ngoaddress);
        ngo_number = findViewById(R.id.ngonumber);
        ngo_password = findViewById(R.id.ngopassword);
        ngo_repassword = findViewById(R.id.ngorepassword);
        createacc = findViewById(R.id.createacc);
    }

    public void createacc(View view) {
        if(ngo_name.getText().toString() != null) {
            if(ngo_address.getText().toString() !=null) {
                if(ngo_number.getText().toString() != null) {
                    if(ngo_password.getText().toString().equals(ngo_repassword.getText().toString())) {
                        ngoname = ngo_name.getText().toString();
                        ngoaddress = ngo_address.getText().toString();
                        ngonumber = ngo_number.getText().toString();
                        ngopassword = ngo_password.getText().toString();
                        BackgroundWorker1 backgroundWorker4 = new BackgroundWorker1(this);
                        backgroundWorker4.execute("addngo", ngoname, ngoaddress, ngonumber, ngopassword);
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.putExtra("ngoname", ngoname);
                        intent.putExtra("ngoaddress", ngoaddress);
                        intent.putExtra("ngonumber", ngonumber);
                        intent.putExtra("ngopassword", ngopassword);
                        startActivity(intent);
                        finish();
                    }
                    ngo_repassword.setError("Password not matching");
                }
                ngo_number.setError("Please enter number");
            }
            ngo_address.setError("Please enter address");
        }
        ngo_name.setError("Please enter name");
    }
}
