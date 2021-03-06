package com.example.offerspot;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class Register extends Fragment {

    LoadingDialog dialog;

    public Register() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register, container, false);
    }




    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dialog = new LoadingDialog(getActivity());

        Button button = (Button) view.findViewById(R.id.createAccount);
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                dialog.ShowDialog();



                boolean firebaseMode = true;

                if(firebaseMode){

                    final String name = ((EditText)getView().findViewById(R.id.editText_FirstName)).getText().toString() + " " +  ((EditText)getView().findViewById(R.id.editText_LastName)).getText().toString();
                    String email = ((EditText)getView().findViewById(R.id.editText_Email)).getText().toString();
                    String password = ((EditText)getView().findViewById(R.id.editText_Password)).getText().toString();

                    ((MainActivity)getActivity()).mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>(){
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    dialog.HideDialog();

                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        // Log.d(TAG, "createUserWithEmail:success");
                                        FirebaseUser user = ((MainActivity)getActivity()).mAuth.getCurrentUser();

                                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                .setDisplayName(name).build();

                                        user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(getContext(), "Register Success!!",
                                                            Toast.LENGTH_SHORT).show();

                                                    Navigation.findNavController(getView()).navigateUp();
                                                }
                                            }
                                        });

                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Toast.makeText(getContext(), task.getException().getMessage(),
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });


                    return;
                }




                String url = "https://offerspotbackend.000webhostapp.com/register.php";

                RequestQueue queue = Volley.newRequestQueue(getContext());

                Map<String, String> params = new HashMap<String, String>();

                String name = ((EditText)getView().findViewById(R.id.editText_FirstName)).getText().toString() + " " +  ((EditText)getView().findViewById(R.id.editText_LastName)).getText().toString();
                String email = ((EditText)getView().findViewById(R.id.editText_Email)).getText().toString();
                String password = ((EditText)getView().findViewById(R.id.editText_Password)).getText().toString();

                if(name.equals("")|| email.equals("") || password.equals("")){
                    Toast.makeText(getContext(), "Please fill the details", Toast.LENGTH_SHORT).show();
                    dialog.HideDialog();
                    return;
                }

                params.put("name",name);
                params.put("email", email);
                params.put("password", password);


                JsonObjectRequest jsonobj = new JsonObjectRequest(Request.Method.POST, url,new JSONObject(params),
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject res) {

                                dialog.HideDialog();

                                try {
                                    Boolean isError = res.getBoolean("isError");
                                    String message = res.getString("message");

                                    if(isError){
                                        Toast.makeText(getContext(), "Error : " + message, Toast.LENGTH_SHORT).show();

                                        ((EditText)getView().findViewById(R.id.editText_FirstName)).getText().clear();
                                        ((EditText)getView().findViewById(R.id.editText_LastName)).getText().clear();
                                        ((EditText)getView().findViewById(R.id.editText_Email)).getText().clear();
                                        ((EditText)getView().findViewById(R.id.editText_Password)).getText().clear();

                                    }else {
                                        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                                        Navigation.findNavController(getView()).navigateUp();
                                    }
                                }
                                catch (JSONException e) {
                                e.printStackTrace();

                                }

                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                dialog.HideDialog();

                                Toast.makeText(getContext(), "Error : " + error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                ){
                };

                queue.add(jsonobj);
            }
        });
    }

}
