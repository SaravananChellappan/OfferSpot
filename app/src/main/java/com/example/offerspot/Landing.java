package com.example.offerspot;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomnavigation.BottomNavigationView;


/**
 * A simple {@link Fragment} subclass.
 */
public class Landing extends Fragment {


    Fragment feedFrag,profileFrag;


    public Landing() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_landing, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        feedFrag = new Feed();

        profileFrag = new Profile();

        BottomNavigationView nav =getView().findViewById(R.id.navBar);

        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.navContainer,feedFrag).commit();

        nav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener(){

           @Override
           public boolean onNavigationItemSelected(@NonNull MenuItem item) {
               Fragment selectedFragment = null;

               switch (item.getItemId()){
                   case R.id.navigation_feed:
                       selectedFragment=feedFrag;
                       break;

                   case R.id.navigation_profile:
                       selectedFragment=profileFrag;
                       break;
               }

               getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.navContainer,selectedFragment).commit();

               return true;
           }
       });
    }
}
