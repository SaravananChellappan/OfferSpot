package com.example.offerspot;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;


public class HomeActivity extends AppCompatActivity {



    public FirebaseAuth mAuth;


    @Nullable
    @Override
    public View onCreateView(@NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs) {

        return super.onCreateView(name, context, attrs);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e("Info","Start");
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();

        setContentView(R.layout.activity_home);

        Log.e("Info","Creation");





    }
}
