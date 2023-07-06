package com.saif.videostreamingapp;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.saif.videostreamingapp.databinding.ActivitySignUpBinding;

public class SignUp extends AppCompatActivity {
    ActivitySignUpBinding binding;
    StorageReference storageReference;
    private FirebaseAuth auth;
    Uri imagePath;
    DatabaseReference databaseReference;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        auth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        progressDialog = new ProgressDialog(SignUp.this);
        progressDialog.setTitle("Image Uploading");
        databaseReference = FirebaseDatabase.getInstance().getReference();


        binding.gotologin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignUp.this, LoginActivity.class));

            }
        });


        binding.addImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dexter.withContext(SignUp.this)
                        .withPermission(READ_EXTERNAL_STORAGE)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                                Intent intent = new Intent();
                                intent.setType("image/*");
                                intent.setAction(Intent.ACTION_GET_CONTENT);
                               startActivityForResult(Intent.createChooser(intent, "Select Image"),101);
                            }

                            @Override
                            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                                permissionToken.continuePermissionRequest();
                            }
                        }).check();
            }
        });

        binding.signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(imagePath != null){
                    String name = binding.nameEt.getText().toString();
                    String email = binding.signupEmailET.getText().toString();
                    String password = binding.passwordEt.getText().toString();

                    if (name.isEmpty()){
                        binding.nameEt.setError("required!");
                    }else if (email.isEmpty()){
                        binding.signupEmailET.setError("required!");
                    }else if(password.isEmpty()){
                        binding.passwordEt.setError("required!");
                    }else{
                        progressDialog.show();
                        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()){
                                    StorageReference profile = storageReference.child("Users/profile/" + auth.getCurrentUser().getUid());
                                    profile.putFile(imagePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            progressDialog.dismiss();
                                            DatabaseReference userProfile = databaseReference.child("Users/profile/"+auth.getCurrentUser().getUid());

                                          profile.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                              @Override
                                              public void onSuccess(Uri uri) {
                                                  UsersModel model = new UsersModel(name, email, password, uri.toString());
                                                  userProfile.setValue(model);
                                                  Toast.makeText(SignUp.this, "Account Created Successfully!", Toast.LENGTH_SHORT).show();
                                                  startActivity(new Intent(SignUp.this, MainActivity.class));
                                                  finish();
                                              }
                                          });
                                        }
                                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                                          float percentage = (snapshot.getBytesTransferred() * 100) / snapshot.getTotalByteCount();
                                          progressDialog.setMessage("Uploading " + (int) percentage + "%");
                                        }
                                    });
                                }else{
                                    Toast.makeText(SignUp.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }


                }else{
                    Toast.makeText(SignUp.this, "Image Required", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 101 && resultCode == RESULT_OK && data != null && data.getData() != null){
            imagePath = data.getData();
            binding.profileImage.setImageURI(imagePath);
        }
    }
}