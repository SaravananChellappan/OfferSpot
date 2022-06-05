package com.example.offerspot;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;


/**
 * A simple {@link Fragment} subclass.
 */
public class Profile extends Fragment {


    public Profile() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String name = ((HomeActivity) getActivity()).mAuth.getCurrentUser().getDisplayName();

        ((TextView) getActivity().findViewById(R.id.profileName)).setText(name);

        Button button = (Button) view.findViewById(R.id.signoutButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance()
                        .signOut();

                Intent myIntent = new Intent(getContext(), MainActivity.class);
                startActivity(myIntent);
                getActivity().finish();
                Toast.makeText(getContext(), "User Sign Out!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
