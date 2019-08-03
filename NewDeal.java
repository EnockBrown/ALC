package com.example.andelaf;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.UUID;

public class NewDeal extends AppCompatActivity {

   private FirebaseDatabase firebaseDatabase;
   private DatabaseReference databaseReference;

   EditText txtTitle;
   EditText txtprice;
    Button btnSelect,btnUpload;
   EditText txtdescription;
   TravelDeal deal;

    Uri saveUri;
    Uri downloadUrl;
    ImageView imageview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_deal);

        //init firebase
        firebaseDatabase=Common.firebaseDatabase;
        databaseReference=Common.databaseReference;

        txtTitle=findViewById(R.id.txtName);
        txtdescription=findViewById(R.id.txtDescription);
        txtprice=findViewById(R.id.txtPrice);
        btnSelect=findViewById(R.id.buttonSelectImage);
        imageview=findViewById(R.id.imageView);
        btnUpload=findViewById(R.id.buttonUpload);


        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });
        //retrieve the deal tht was assed
        Intent intent =getIntent();
        TravelDeal deal=(TravelDeal) intent.getSerializableExtra("Deal");
        if (deal==null){
            deal=new TravelDeal();
        }
        this.deal=deal;
        txtTitle.setText(deal.getTitle());
        txtdescription.setText(deal.getDescription());
        txtprice.setText(deal.getPrice());
        showImage(downloadUrl);
    }

    private void chooseImage() {

        Intent intent =new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"select picture"),Common.PICK_IMAGE_REQUEST);
    }


    protected void onActivityResult(int requestCode, int resultCode,Intent data){
        super.onActivityResult(requestCode,resultCode,data);

        if(requestCode==Common.PICK_IMAGE_REQUEST && resultCode==RESULT_OK && data !=null && data.getData() != null)
        {
            saveUri = data.getData();
            btnSelect.setText("Image selected");
        }
    }

    private void uploadImage() {
        if(saveUri != null)
        {
            final ProgressDialog mDialog = new ProgressDialog(this);
            mDialog.setMessage("Uploading");
            mDialog.show();

            String imageName = UUID.randomUUID().toString();
            final StorageReference imageFolder = Common.storageReffrence.child("images/"+imageName);
            imageFolder.putFile(saveUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            mDialog.dismiss();
                            Toast.makeText(NewDeal.this,"Uploaded!!",Toast.LENGTH_SHORT).show();


                            imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                     downloadUrl = uri;
                                    deal.setImageUrl(downloadUrl.toString());

                                    showImage(downloadUrl);
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mDialog.dismiss();
                            Toast.makeText(NewDeal.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0* taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            mDialog.setMessage("uploaded "+progress+"%");
                        }
                    });

        }

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.save_menu:
                saveDeal();
                Toast.makeText(this, "Deal Saved", Toast.LENGTH_SHORT).show();
                clean();
                back();
                return true;
            case R.id.new_deal:
                Intent i = new Intent(NewDeal.this, NewDeal.class);
                startActivity(i);
                return true;
            case  R.id.delete_menu:
                deleteDeal();
                Toast.makeText(this, "Menu deleted successfull.", Toast.LENGTH_SHORT).show();
                default:
                    return super.onOptionsItemSelected(item);
        }

    }

    private void saveDeal() {
        deal.setTitle(txtTitle.getText().toString());
        deal.setDescription(txtdescription.getText().toString());
        deal.setPrice(txtprice.getText().toString());
        deal.setImageUrl(downloadUrl.toString());

        //checking if the deal is new or an existing
        if (deal.getId()==null){
            //its a new deal
            databaseReference.push().setValue(deal);
        }
        else
        {
            databaseReference.child(deal.getId()).setValue(deal);
        }


    }

    private void deleteDeal(){
        if (deal==null){
            Toast.makeText(this, "No travel deal to be deleted", Toast.LENGTH_SHORT).show();
        }
        else{
            databaseReference.child(deal.getId()).removeValue();
        }
    }

    private  void back(){
        startActivity(new Intent(NewDeal.this,ListActivity.class));
    }

    private void clean() {
        txtprice.setText("");
        txtdescription.setText("");
        txtTitle.setText("");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.save_menu,menu);

        if (Common.isAdmin){
            menu.findItem(R.id.delete_menu).setVisible(true);
            menu.findItem(R.id.save_menu).setVisible(true);
            menu.findItem(R.id.new_deal).setVisible(true);
            enableEditText(true);
        }
        else{
            menu.findItem(R.id.delete_menu).setVisible(false);
            menu.findItem(R.id.save_menu).setVisible(false);
            enableEditText(false);
        }
        return  true;
    }

    private void enableEditText(boolean isEnabled){
        txtprice.setEnabled(isEnabled);
        txtdescription.setEnabled(isEnabled);
        txtTitle.setEnabled(isEnabled);
    }
    private void showImage(Uri downloadUrl){
        if (downloadUrl !=null){
            int width= Resources.getSystem().getDisplayMetrics().widthPixels;
            Picasso.with(this)
                    .load(downloadUrl)
                    .resize(width,width*2/3)
                    .centerCrop()
                    .into(imageview);
        }
    }
}
