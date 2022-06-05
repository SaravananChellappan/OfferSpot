package com.example.offerspot;


import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


class ListAdapter extends ArrayAdapter<PostData> {


    StorageReference mStorageRef;

    public ListAdapter(@NonNull Context context, @NonNull ArrayList<PostData> objects) {
        super(context, 0, objects);
        mStorageRef = FirebaseStorage.getInstance().getReference();
    }



    String GetFileExtention(Uri uri){
        ContentResolver contentResolver = getContext().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }


    @Nullable
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listitemView = null;
        if (listitemView == null) {
            listitemView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_post_item, parent, false);

            final PostData dataModal = getItem(position);

            final ImageView image= ((ImageView)listitemView.findViewById(R.id.postItemImage));


            ((TextView)listitemView.findViewById(R.id.userNameText)).setText(dataModal.userName);
            ((TextView)listitemView.findViewById(R.id.descriptionText)).setText(dataModal.description);

            if(!dataModal.hasImage){
                image.setVisibility(View.GONE);
            }else {

//                Glide.with(getContext())
//                        .load( mStorageRef.child("posts/" + dataModal.postID+".jpeg"))
//                        .into(image);


                mStorageRef.child("posts/" + dataModal.postID).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        Picasso.get().load(uri).into(image);

                        Log.e("Path",uri.getPath());
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            ((Button)listitemView.findViewById(R.id.viewLocationButton)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/search/?api=1&query=\""+dataModal.mapPoint.getLatitude()+","+dataModal.mapPoint.getLongitude()+"\""));
                    getContext().startActivity(browserIntent);
                    //Toast.makeText(getContext(), "Item clicked is : " + dataModal.mapPoint.toString(), Toast.LENGTH_SHORT).show();
                }
            });

        }

        return listitemView;
    }

}

/**
 * A simple {@link Fragment} subclass.
 */
public class Feed extends Fragment {

    View loadingIndicator;
    ListView postList;
    ArrayList<PostData> postData;
    FirebaseFirestore db;

    public Feed() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_feed, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        loadingIndicator = getView().findViewById(R.id.loadingFeed);

        postList = getView().findViewById(R.id.postList);
        postData = new ArrayList<>();
        db = FirebaseFirestore.getInstance();

        FloatingActionButton button = (FloatingActionButton) view.findViewById(R.id.newPostButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(getView()).navigate(R.id.action_landing_to_newPost);
            }
        });


        LoadList();
    }


    void LoadList(){
        db.collection("posts").orderBy("time", Query.Direction.DESCENDING).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(!queryDocumentSnapshots.isEmpty()){
                    List<DocumentSnapshot> docs = queryDocumentSnapshots.getDocuments();

                    for(DocumentSnapshot item : docs){
                        PostData data = new PostData();
                        data.postID = item.getId();
                        data.userName = item.getString("userName");
                        data.description = item.getString("description");
                        data.time = item.getTimestamp("time");
                        data.mapPoint = item.getGeoPoint("mapPoint");
                        data.hasImage = item.getBoolean("hasImage");

                        postData.add(data);
                    }


                    ListAdapter adapter = new ListAdapter(getContext(), postData);

                    // after passing this array list to our adapter
                    // class we are setting our adapter to our list view.
                    postList.setAdapter(adapter);

                    loadingIndicator.setVisibility(View.GONE);
                }else {
                    loadingIndicator.setVisibility(View.GONE);
                }
            }
        });
    }



}
