package com.example.andelaf;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class DealAdapter extends RecyclerView.Adapter<DealAdapter.DealViewHolder> {

    ArrayList<TravelDeal> deals;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private ChildEventListener childEventListener;
    private ImageView imageDeal;


    public DealAdapter(){
        //Common.openFbReffrence("traveldeal",);
        firebaseDatabase=Common.firebaseDatabase;
        databaseReference=Common.databaseReference;
        deals=Common.mDeals;
        childEventListener=new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                TravelDeal td=dataSnapshot.getValue(TravelDeal.class);
                Log.d("Deals",td.getTitle());
                td.setId(dataSnapshot.getKey());
                deals.add(td);
                notifyItemInserted(deals.size()-1);//notify that the item has been inserted

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
        databaseReference.addChildEventListener(childEventListener);
    }

    @NonNull
    @Override
    public DealViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context=viewGroup.getContext();
        View itemview= LayoutInflater.from(context)
                .inflate(R.layout.rv_row,viewGroup,false);
        return  new DealViewHolder(itemview);
    }

    @Override
    public void onBindViewHolder(@NonNull DealViewHolder dealViewHolder, int i) {
        TravelDeal deal=deals.get(i);
        dealViewHolder.bind(deal);
    }

    @Override
    public int getItemCount() {
        return deals.size();
    }

    public class DealViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView tvTitle,tvDesc,tvPrice;
        public DealViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle=itemView.findViewById(R.id.title);
            tvDesc=itemView.findViewById(R.id.desc);
            tvPrice=itemView.findViewById(R.id.price);
            imageDeal=itemView.findViewById(R.id.travelDealImage);
            itemView.setOnClickListener(this);

        }

        public void bind(TravelDeal deal){
            tvTitle.setText(deal.getTitle());
            tvDesc.setText(deal.getDescription());
            tvPrice.setText(deal.getPrice());
            showImage(deal.getImageUrl());
        }


        @Override
        public void onClick(View v) {
            int pos=getAdapterPosition();
            TravelDeal selectedDeal=deals.get(pos);
            Intent i=new Intent(v.getContext(),NewDeal.class);
            i.putExtra("Deal",selectedDeal);
            v.getContext().startActivity(i);
        }
        private  void showImage(String downloadUrl){
            if (downloadUrl !=null && downloadUrl.isEmpty()==false){
                Picasso.with(imageDeal.getContext())
                        .load(downloadUrl)
                        .resize(160,160)
                        .centerCrop()
                        .into(imageDeal);
            }
        }
    }

}
