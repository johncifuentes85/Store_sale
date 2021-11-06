package com.example.store_sale;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();

        Context context = getApplicationContext();
        SharedPreferences sharedPref1 = context.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        Boolean session = sharedPref1.getBoolean("session",false);
        String tipo = sharedPref1.getString("tipo","");
        //Toast.makeText(getApplicationContext(), "session: "+session, Toast.LENGTH_SHORT).show();

        TimerTask Star = new TimerTask() {
            @Override
            public void run() {
                if(session.equals(true)){
                    if(tipo.equals("Usuario")){
                        Intent intent = new Intent(MainActivity.this,ListProductUserActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    else if(tipo.equals("Vendedor")){
                        Intent intent = new Intent(MainActivity.this,ListProductActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }else{
                    Intent intent = new Intent(MainActivity.this,SessionActivity.class);
                    startActivity(intent);
                    finish();
                }

            }
        };

        Timer time = new Timer();
        time.schedule(Star,1300);
    }

}