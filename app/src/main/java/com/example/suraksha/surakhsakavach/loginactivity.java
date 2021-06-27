package com.example.suraksha.surakhsakavach;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class loginactivity extends AppCompatActivity {

    Button login;
    EditText number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginactivity);

        getSupportActionBar().hide();

        login = findViewById(R.id.loginbutton);
        number = findViewById(R.id.editTextNumber);


        File file = getBaseContext().getFileStreamPath("userphone");
        if(file.exists()){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phonenumber;
                phonenumber = number.getText().toString();
                if(number.getText().toString().length() < 10){

                    Toast.makeText(loginactivity.this, "Please enter a valid number", Toast.LENGTH_SHORT).show();
                }
                else{
                    FileOutputStream fos;
                    try {
                        String filename = "userphone";
                        String data = number.getText().toString();
                        fos = openFileOutput(filename, Context.MODE_PRIVATE);
                        //default mode is PRIVATE, can be APPEND etc.
                        fos.write(data.getBytes());
                        fos.close();
                        Toast.makeText(getApplicationContext(),filename + "saved",
                                Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                        startActivity(intent);

                    } catch (FileNotFoundException e) {e.printStackTrace();}
                    catch (IOException e) {e.printStackTrace();
                    }

                }

            }
        });

    }
}