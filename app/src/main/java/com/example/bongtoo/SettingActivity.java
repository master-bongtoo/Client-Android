package com.example.bongtoo;

import androidx.appcompat.app.AppCompatActivity;

import android.media.Image;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
public class SettingActivity extends AppCompatActivity {
    int member_num;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        member_num = getIntent().getIntExtra("member_num",-1);
        ImageView btnHeaderBack = findViewById(R.id.btnHeaderBack);
        btnHeaderBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        SettingsFragment settingsFragment = new SettingsFragment();
        Bundle bundle = new Bundle(2);
        bundle.putInt("member_num", member_num);
        settingsFragment.setArguments(bundle);
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragment_place, settingsFragment).addToBackStack(null);
        ft.commit();
    }
    
}