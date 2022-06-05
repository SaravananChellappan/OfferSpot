package com.example.offerspot;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;

public class PostData {
    public String postID;
    public String userName;
    public String description;
    public Timestamp time;
    public GeoPoint mapPoint;
    public  boolean hasImage;
}
