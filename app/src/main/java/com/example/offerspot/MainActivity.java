package com.example.offerspot;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.NavHostController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.app.ActionBar;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import static androidx.navigation.ui.NavigationUI.setupActionBarWithNavController;
import static androidx.navigation.ui.NavigationUI.setupWithNavController;


public class MainActivity extends AppCompatActivity {

    NavController nav;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        NavHostFragment host = (NavHostFragment)getSupportFragmentManager().findFragmentById(R.id.navHost);
        nav = host.getNavController();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);


        setSupportActionBar(toolbar);
        setupActionBarWithNavController(this,nav);

    }


    @Override
    public boolean onSupportNavigateUp() {
        return nav.navigateUp() || super.onSupportNavigateUp();
    }
}
