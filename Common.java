package com.example.andelaf;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Common {
    public static FirebaseDatabase firebaseDatabase;
    public static DatabaseReference databaseReference;
    private static Common common;
    public static final int SIGN_IN_REQUEST_CODE =1001 ;
    public static final int PICK_IMAGE_REQUEST =1002 ;
    public static final int REQUEST_IMAGE_CAPTURE = 1;
    public static FirebaseStorage storage;
    public static StorageReference storageReffrence;
    public static boolean isAdmin;
    public static final int RC_SIGN_IN = 123;
    public static FirebaseAuth firebaseAth;
    private static ListActivity caller;
    public static FirebaseAuth.AuthStateListener mAuthListener;

    public static final int PERMISSION_REQUEST_CODE=1000;

    public static ArrayList<TravelDeal> mDeals;



    private Common (){}
    public static void connectStorage(){
        storage=FirebaseStorage.getInstance();
        storageReffrence=storage.getReference().child("images");
    }

    public static void openFbReffrence (String ref, final ListActivity callerActivity){
        if(common==null){
            common=new Common();
            firebaseDatabase=FirebaseDatabase.getInstance();
            firebaseAth=FirebaseAuth.getInstance();
            mAuthListener=new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    if (firebaseAuth.getCurrentUser()==null){

                        List<AuthUI.IdpConfig> providers = Arrays.asList(
                                new AuthUI.IdpConfig.EmailBuilder().build(),
                                new AuthUI.IdpConfig.GoogleBuilder().build());
                        caller=callerActivity;
// Create and launch sign-in intent
                        caller.startActivityForResult(
                                AuthUI.getInstance()
                                        .createSignInIntentBuilder()
                                        .setAvailableProviders(providers)
                                        .build(),
                                RC_SIGN_IN);
                    }
                    else
                    {
                        String uid=firebaseAuth.getUid();
                        checkUser(uid);
                    }
                    Toast.makeText(callerActivity.getBaseContext(), "WelcomeBack", Toast.LENGTH_LONG).show();

                }
            };

            connectStorage();

        }
        mDeals= new ArrayList<TravelDeal>();
        databaseReference=firebaseDatabase.getReference().child(ref);
    }

    private static void checkUser(String uid) {
        Common.isAdmin=false;
        DatabaseReference dbrefrrence=firebaseDatabase.getReference().child("administrators").child(uid);
        ChildEventListener ls=new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Common.isAdmin=true;
               // caller.showMenu();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        dbrefrrence.addChildEventListener(ls);
    }

    public static void attachListenr(){
        firebaseAth.addAuthStateListener(mAuthListener);
    }
    public static void detach(){
        firebaseAth.removeAuthStateListener(mAuthListener );
    }
}
