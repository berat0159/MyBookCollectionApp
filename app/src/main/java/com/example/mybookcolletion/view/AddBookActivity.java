package com.example.mybookcolletion.view;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.FileObserver;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import com.example.mybookcolletion.adapter.BookAdapter;
import com.example.mybookcolletion.databinding.ActivityAddBookBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

public class AddBookActivity extends AppCompatActivity {
    private ActivityAddBookBinding binding;
    ActivityResultLauncher<Intent> activityResultLauncher;
    ActivityResultLauncher<String> activityPermissionLauncher;
   // Bitmap selectedImage;
   Uri imageData;

    private FirebaseStorage storage;
    private StorageReference storageReference;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityAddBookBinding.inflate(getLayoutInflater());
        View view=binding.getRoot();
        setContentView(view);
        registerLauncher();
        //this give storage reference

        firebaseFirestore=FirebaseFirestore.getInstance();
        auth=FirebaseAuth.getInstance();
        storage=FirebaseStorage.getInstance();
        storageReference=storage.getReference();




    }
    public void selectImage(View view){
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){

            if(ActivityCompat.shouldShowRequestPermissionRationale(AddBookActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE)){
                Snackbar.make(view,"You Should Give Permission for Upload Your Images",Snackbar.LENGTH_INDEFINITE).setAction("Give Permission", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                            //request permission
                        activityPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                    }
                }).show();

            }else {
                // just request permission
                activityPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);

            }



        }else {
            //intent to gallery
            Intent intent=new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            activityResultLauncher.launch(intent);

        }

    }



    public void registerLauncher(){
        activityResultLauncher=registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                //check result
                if(result.getResultCode()==RESULT_OK){
                    Intent intent=result.getData();
                        if (intent.getData()!=null){
                            imageData=intent.getData();
                            binding.addImage.setImageURI(imageData);
                        }

                }
            }
        });

        activityPermissionLauncher=registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
                if(result){
                    Intent intent=new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    activityResultLauncher.launch(intent);
                }else {
                    Toast.makeText(AddBookActivity.this,"You Should Give Permission for Uploud Your Images",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void saveButton(View view){

        if(imageData!=null){



            //give random idName for images
            UUID uuid=UUID.randomUUID();
            String imageName="images/"+uuid+".jpg";
            storageReference.child(imageName).putFile(imageData).addOnSuccessListener( AddBookActivity.this,new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    //Download Url

                    StorageReference newReference=storage.getReference(imageName);
                    newReference.getDownloadUrl().addOnSuccessListener(AddBookActivity.this, new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            //get uri from storage
                            String downLoadUrl=uri.toString();
                            String bookName=binding.nameText.getText().toString();
                            String writerName=binding.writerText.getText().toString();

                            FirebaseUser firebaseUser=auth.getCurrentUser();
                            String email=firebaseUser.getEmail();
                            HashMap<String,Object> bookData=new HashMap<>();
                            bookData.put("email",email);
                            bookData.put("image",downLoadUrl);
                            bookData.put("bookName",bookName);
                            bookData.put("writerName",writerName);
                            bookData.put("date", FieldValue.serverTimestamp());
                            //add data to collection
                            firebaseFirestore.collection("Books").add(bookData).addOnSuccessListener( new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Intent intent=new Intent(AddBookActivity.this,FeedActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);


                                }
                            }).addOnFailureListener( new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(AddBookActivity.this,e.getLocalizedMessage(),Toast.LENGTH_LONG).show();

                                }
                            });


                        }
                    });


                }
            }).addOnFailureListener(AddBookActivity.this, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(AddBookActivity.this,e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                }
            });


        }



    }



}