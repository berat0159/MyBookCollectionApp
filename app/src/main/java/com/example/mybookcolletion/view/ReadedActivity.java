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
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import com.example.mybookcolletion.databinding.ActivityReadedBinding;
import com.example.mybookcolletion.modul.Book;
import com.example.mybookcolletion.modul.OldBook;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.HashMap;
import java.util.UUID;

public class ReadedActivity extends AppCompatActivity {
    private ActivityReadedBinding binding;
    private FirebaseAuth auth;
    private FirebaseStorage storage;
    private FirebaseFirestore fireStore;
    private StorageReference storageReference;

    ActivityResultLauncher<Intent> intentActivityResultLauncher;
    ActivityResultLauncher<String> permissionActivityResultLauncher;
    Book book;
    Uri imageUri;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityReadedBinding.inflate(getLayoutInflater());
        View view=binding.getRoot();
        setContentView(view);

        auth=FirebaseAuth.getInstance();


        storage=FirebaseStorage.getInstance();
        storageReference=storage.getReference();
        fireStore=FirebaseFirestore.getInstance();
        auth=FirebaseAuth.getInstance();
        Intent intent=getIntent();
        String info=intent.getStringExtra("info");
        if(info.equals("new")){
            book=(Book) intent.getSerializableExtra("Book");
            binding.bookName.setText(book.bookName);
            binding.writerText.setText(book.writerName);
            binding.delete.setVisibility(View.GONE);
        }else {
           OldBook book=(OldBook) intent.getSerializableExtra("OldBook");
            Picasso.get().load(book.image).into(binding.readImage);
            binding.bookName.setText(book.bookTxt);
            binding.writerText.setText(book.writerTxt);
            binding.excerpt.setText(book.excreptTxt);
            binding.button.setVisibility(View.INVISIBLE);
        }






        registerLauncher();


    }

    public void selectReadImage(View view){
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){

            if(ActivityCompat.shouldShowRequestPermissionRationale(ReadedActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE)){

                Snackbar.make(view,"You Should Give Permission for Load İmage",Snackbar.LENGTH_INDEFINITE).setAction("Give Permission", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //request permission
                        permissionActivityResultLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);

                    }
                });

            }else {
                //request permission
                permissionActivityResultLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            }




        }else {
            //intent to gallery
            Intent intent=new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intentActivityResultLauncher.launch(intent);

        }


    }


    public void registerLauncher(){
        intentActivityResultLauncher=registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                //check result
                if(result.getResultCode()==RESULT_OK){
                    Intent intent=result.getData();
                    if(intent.getData()!=null){
                        imageUri=intent.getData();
                        binding.readImage.setImageURI(imageUri);
                    }
                }


            }
        });

        permissionActivityResultLauncher=registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {

                if(result){
                    Intent intent=new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intentActivityResultLauncher.launch(intent);
                }else {
                    Toast.makeText(ReadedActivity.this,"You Should Give Permission for Load Images",Toast.LENGTH_LONG).show();
                }


            }
        });

    }

    public void readButton(View view) {

        if (imageUri != null) {
            UUID uuid = UUID.randomUUID();
            String imageName = "oldImages/" + uuid + ".jpg";
            storageReference.child(imageName).putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    //getDownUrl
                    StorageReference newStorageReference = storage.getReference(imageName);
                    newStorageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String downloadUrl = uri.toString();
                            String bookName = binding.bookName.getText().toString();
                            String writerName = binding.writerText.getText().toString();
                            String excrept = binding.excerpt.getText().toString();

                            HashMap<String, Object> putData = new HashMap<>();
                            putData.put("downUrl", downloadUrl);
                            putData.put("bookTxt", bookName);
                            putData.put("writerTxt", writerName);
                            putData.put("excreptTxt", excrept);

                            fireStore.collection("OldBooks").add(putData).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Intent intent = new Intent(ReadedActivity.this, FeedActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);



                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(ReadedActivity.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                                }
                            });


                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ReadedActivity.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                        }
                    });


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ReadedActivity.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }
            });


        }

        String bookName=binding.bookName.getText().toString();
        fireStore.collection("Books").whereEqualTo("bookName",bookName).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if(task.isSuccessful() && !task.getResult().isEmpty()){

                    DocumentSnapshot documentSnapshot=task.getResult().getDocuments().get(0);
                    String docID=documentSnapshot.getId();

                        fireStore.collection("Books").document(docID).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(ReadedActivity.this,"moved to the read shelf of the book",Toast.LENGTH_LONG).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(ReadedActivity.this,e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                            }
                        });


                }


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ReadedActivity.this,e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
            }
        });


    }

    public void deleteButton(View view){


        String bookName=binding.bookName.getText().toString();
        fireStore.collection("OldBooks").whereEqualTo("bookTxt",bookName).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()&& !task.getResult().isEmpty()){
                    DocumentSnapshot documentSnapshot=task.getResult().getDocuments().get(0);
                    String docId=documentSnapshot.getId();

                        fireStore.collection("OldBooks").document(docId).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(ReadedActivity.this,"Deleted Your Data",Toast.LENGTH_LONG).show();
                                Intent intent=new Intent(ReadedActivity.this,FeedActivity.class);
                                startActivity(intent);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(ReadedActivity.this,e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                            }
                        });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ReadedActivity.this,e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
            }
        });





    }

    public void deleteButton2(View view){

        String bookName2=binding.bookName.getText().toString();

        fireStore.collection("Books").whereEqualTo("bookName",bookName2).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if(task.isSuccessful() && !task.getResult().isEmpty()){

                    DocumentSnapshot documentSnapshot=task.getResult().getDocuments().get(0);

                    String docID2=documentSnapshot.getId();


                    fireStore.collection("Books").document(docID2).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Intent intent=new Intent(ReadedActivity.this,FeedActivity.class);
                            startActivity(intent);

                            Toast.makeText(ReadedActivity.this,"To Be Read Book Deleted",Toast.LENGTH_LONG).show();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ReadedActivity.this,e.getLocalizedMessage(),Toast.LENGTH_LONG).show();

                        }
                    });

                }



            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ReadedActivity.this,e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
            }
        });


    }


}