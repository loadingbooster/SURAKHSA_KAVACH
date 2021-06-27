package com.example.suraksha.surakhsakavach;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import java.io.File;

public class splashscreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);

        getSupportActionBar().hide();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(fileExist("userphone")){
                    Intent openhome = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(openhome);
                }else{
                    Intent openuseract = new Intent(getApplicationContext(), loginactivity.class);
                    startActivity(openuseract);
                }

            }
        }, 1000);
    }

    private boolean fileExist(String userphone) {
        File file = getBaseContext().getFileStreamPath("userphone");
        return file.exists();
    }
}