package com.example.offerspot;


import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import com.google.android.libraries.places.api.model.Place;
import com.google.firebase.FirebaseApp;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.model.value.GeoPointValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rtchagas.pingplacepicker.PingPlacePicker;
import com.sucho.placepicker.Constants;
import com.sucho.placepicker.MapType;
import com.sucho.placepicker.PlacePicker;

import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class NewPost extends Fragment implements OnMapReadyCallback {


    FirebaseFirestore db;
    StorageReference mStorageRef;

    LoadingDialog dialog;

    Uri imageURI;

    LatLng geoPoint;


    GoogleMap map;


    public NewPost() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        db = FirebaseFirestore.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();



        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_new_post, container, false);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);



        if(requestCode==1&& resultCode==RESULT_OK && data!=null){
            ImageView view = getView().findViewById(R.id.newPostImage);
            imageURI = data.getData();
            view.setImageURI(data.getData());
        }

        if(requestCode==2 && resultCode==RESULT_OK && data!=null) {
            Place place = PingPlacePicker.getPlace(data);
            if (place != null) {
                map.clear();
                map.addMarker(new MarkerOptions().position(place.getLatLng()).title(place.getName()));
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(),15));
                geoPoint = place.getLatLng();
                Toast.makeText(getContext(), "You selected the place: " + place.getName(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    void Post(){
        Map<String, Object> post = new HashMap<>();
        post.put("userName",  ((HomeActivity) getActivity()).mAuth.getCurrentUser().getDisplayName().toString());
        post.put("time", Timestamp.now());
        post.put("description", ((TextView)getView().findViewById(R.id.descriptionText)).getText().toString());
        post.put("hasImage",imageURI!=null);
        post.put("mapPoint", new GeoPoint(geoPoint.latitude,geoPoint.longitude));

        db.collection("posts").add(post).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                postId = documentReference.getId();
                UploadImage();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.HideDialog();
                Navigation.findNavController(getView()).navigateUp();

                Toast.makeText(getContext(),e.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }



    String postId;


    void UploadImage(){

        if(imageURI==null){
            dialog.HideDialog();
            Navigation.findNavController(getView()).navigateUp();

            Toast.makeText(getContext(),"Posted Successfully",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        mStorageRef.child("posts/"+ postId).putFile(imageURI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                dialog.HideDialog();
                Navigation.findNavController(getView()).navigateUp();

                Toast.makeText(getContext(),"Posted Successfully",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dialog = new LoadingDialog(getActivity());

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapView);

        mapFragment.getMapAsync(this);

        ImageButton button = (ImageButton) view.findViewById(R.id.backToFeedButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(getView()).navigateUp();
            }
        });


        Button selectImage = (Button) view.findViewById(R.id.selectImage);
        selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent,1);
            }
        });

        Button selectLoc = (Button) view.findViewById(R.id.selectLoc);
        selectLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.e("Places","Clicked");

                PingPlacePicker.IntentBuilder builder = new PingPlacePicker.IntentBuilder();
                builder.setAndroidApiKey("AIzaSyDTnZ0B37z6grbD80II6CbrN_KGlfyz_JI")
                        .setMapsApiKey("AIzaSyDTnZ0B37z6grbD80II6CbrN_KGlfyz_JI");

                // If you want to set a initial location rather then the current device location.
                // NOTE: enable_nearby_search MUST be true.
                // builder.setLatLng(new LatLng(37.4219999, -122.0862462))

                try {
                    Intent placeIntent = builder.build(getActivity());
                    startActivityForResult(placeIntent, 2);
                }
                catch (Exception ex) {
                    // Google Play services is not available...
                }

//
//                Intent intent =new PlacePicker.IntentBuilder()
//                        .setLatLong(40.748672, -73.985628)  // Initial Latitude and Longitude the Map will load into
//                        .showLatLong(true)  // Show Coordinates in the Activity
//                        .setMapZoom(12.0f)  // Map Zoom Level. Default: 14.0
//                        .setAddressRequired(true) // Set If return only Coordinates if cannot fetch Address for the coordinates. Default: True
//                        .hideMarkerShadow(true) // Hides the shadow under the map marker. Default: False
//                        .setMarkerDrawable(R.drawable.ic_place_black_24dp) // Change the default Marker Image
//                        .setMarkerImageImageColor(R.color.colorPrimary)
//                        .setFabColor(R.color.colorAccent)
//                        .setPrimaryTextColor(R.color.colorPrimary) // Change text color of Shortened Address
//                        .setSecondaryTextColor(R.color.colorPrimary) // Change text color of full Address
//                        .setBottomViewColor(R.color.colorAccent) // Change Address View Background Color (Default: White)
////                        .setMapRawResourceStyle(R.raw.map_style)  //Set Map Style (https://mapstyle.withgoogle.com/)
//                        .setMapType(MapType.NORMAL)
//                        .setPlaceSearchBar(true, "AIzaSyDTnZ0B37z6grbD80II6CbrN_KGlfyz_JI") //Activate GooglePlace Search Bar. Default is false/not activated. SearchBar is a chargeable feature by Google
//                        .onlyCoordinates(true)  //Get only Coordinates from Place Picker
//                        .hideLocationButton(true)   //Hide Location Button (Default: false)
//                        .disableMarkerAnimation(true)   //Disable Marker Animation (Default: false)
//                        .build(getActivity());

                //startActivityForResult(intent, Constants.PLACE_PICKER_REQUEST);

//                Intent locationPickerIntent =new LocationPickerActivity.Builder()
//                        .withLocation(41.4036299, 2.1743558)
//                        .withGeolocApiKey("AIzaSyDTnZ0B37z6grbD80II6CbrN_KGlfyz_JI")
//                        .withSearchZone("es_ES")
//                        .withDefaultLocaleSearchZone()
//                        .shouldReturnOkOnBackPressed()
//                        .withStreetHidden()
//                        .withCityHidden()
//                        .withZipCodeHidden()
//                        .withSatelliteViewHidden()
//                        .withGooglePlacesEnabled()
//                        .withGoogleTimeZoneEnabled()
//                        .withVoiceSearchHidden()
//                        .withUnnamedRoadHidden()
//                        .build(getContext());



                //startActivityForResult(locationPickerIntent, 3);

//                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
//
//                try {
//                    startActivityForResult(builder.build(getActivity()),3);
//                } catch (GooglePlayServicesRepairableException e) {
//                    Log.e("Places",e.getMessage());
//                } catch (GooglePlayServicesNotAvailableException e) {
//                    Log.e("Places",e.getMessage());
//                }
            }
        });


        ImageButton postButton = (ImageButton) view.findViewById(R.id.postButton);
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(geoPoint!=null){
                    dialog.ShowDialog();
                    Post();
                }else {
                    Toast.makeText(getContext(),"Please select a location",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        map = googleMap;

        LatLng loc= new LatLng(0,0);

        map.addMarker(new MarkerOptions().position(loc).title("Test"));

        map.moveCamera(CameraUpdateFactory.newLatLng(loc));

        map.getUiSettings().setScrollGesturesEnabled(false);

    }
}
