package com.example.offerspot;


import android.content.res.Resources;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.NavGraph;
import androidx.navigation.NavHostController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

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
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static androidx.navigation.ui.NavigationUI.setupActionBarWithNavController;


/**
 * A simple {@link Fragment} subclass.
 */
public class Login extends Fragment {


    EditText emailText,passwordText;

    LoadingDialog dialog;


    public Login() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_login, container, false);

        // Inflate the layout for this fragment
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        emailText =  getView().findViewById(R.id.emailField);
        passwordText =  getView().findViewById(R.id.passwordField);

        dialog = new LoadingDialog(getActivity());


        Button registerButton = (Button) view.findViewById(R.id.registerButton);
        registerButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                emailText.getText().clear();
                passwordText.getText().clear();
                Navigation.findNavController(getView()).navigate(R.id.action_login_to_register);
            }
        });

        Button loginButton = (Button) view.findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dialog.ShowDialog();

                String url = "https://offerspotbackend.000webhostapp.com/login.php";

                RequestQueue queue = Volley.newRequestQueue(getContext());

                Map<String, String> params = new HashMap<String, String>();

                params.put("email", emailText.getText().toString());
                params.put("password", passwordText.getText().toString());

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

                                        emailText.getText().clear();
                                        passwordText.getText().clear();

                                    }else {
                                        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();

                                        NavController navController =Navigation.findNavController(getView());
                                        NavGraph navGraph = navController.getNavInflater().inflate(R.navigation.appflow);
                                        navController.setGraph(navGraph);
                                        setupActionBarWithNavController((AppCompatActivity) getActivity(),navController);

                                        //Navigation.findNavController(getView()).navigate(R.id.action_login_to_App,null,new NavOptions.Builder().setPopUpTo(R.id.login, false).setLaunchSingleTop(true).build());
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
