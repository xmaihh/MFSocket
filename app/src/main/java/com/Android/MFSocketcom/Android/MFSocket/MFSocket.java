package com.Android.MFSocketcom.Android.MFSocket;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.Android.MFSocketcom.R;

import org.fmod.example.MainActivity;

public class MFSocket extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    void onClick(View v) {
        startActivity(new Intent(MFSocket.this, MainActivity.class));
    }
}
