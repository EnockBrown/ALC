package com.example.andelaf;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ListActivity extends AppCompatActivity {

    ArrayList<TravelDeal> deals;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private ChildEventListener childEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.new_deal,menu);

        MenuItem  newDeal=menu.findItem(R.id.new_deal);
        if (Common.isAdmin==true){
            newDeal.setVisible(true);
        }
        else
        {
            newDeal.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.new_deal:
                Intent i = new Intent(ListActivity.this, NewDeal.class);
                startActivity(i);
                return true;
            case R.id.logout:
                signout();
                Common.detach();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void signout() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d("SignOut","User Looged out successfully");
                    }
                });

    }

    @Override
    protected void onPause() {
        super.onPause();
        Common.detach();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Common.openFbReffrence("traveldeal",this);
        firebaseDatabase=Common.firebaseDatabase;
        databaseReference=Common.databaseReference;


        RecyclerView rvdeals=findViewById(R.id.rvDeals);
        final DealAdapter adapter=new DealAdapter();
        rvdeals.setAdapter(adapter);
        LinearLayoutManager dealManager =new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        rvdeals.setLayoutManager(dealManager);

        Common.attachListenr();
    }

    public void showMenu() {
        invalidateOptionsMenu();
    }
}
