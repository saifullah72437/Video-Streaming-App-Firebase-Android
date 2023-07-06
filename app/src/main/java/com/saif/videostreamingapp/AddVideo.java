package com.saif.videostreamingapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.MediaController;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
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
import com.saif.videostreamingapp.databinding.ActivityAddVideoBinding;

public class AddVideo extends AppCompatActivity {
ActivityAddVideoBinding binding;
Uri videoUri;
StorageReference storageReference;
DatabaseReference databaseReference;
MediaController mediaController;
ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddVideoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());



        storageReference = FirebaseStorage.getInstance().getReference("myVideos");
        databaseReference = FirebaseDatabase.getInstance().getReference();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Video Uploader");
        progressDialog.setCancelable(false);


        mediaController = new MediaController(this);
        binding.videoView.setMediaController(mediaController);
        binding.videoView.start();



//        Browse Button click
        binding.browse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dexter.withContext(AddVideo.this)
                        .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                                Intent intent = new Intent();
                                intent.setType("video/*");
                                intent.setAction(Intent.ACTION_GET_CONTENT);
                                startActivityForResult(intent, 102);
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

//        Upload Button Click
        binding.upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               processVideoUpload();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 102 && resultCode == RESULT_OK && data != null && data.getData() != null){
            videoUri = data.getData();
            binding.videoView.setVideoURI(videoUri);
        }

    }

    public static String getFileExtensionFromUri(Context context, Uri uri) {
        String extension = null;

        // Handle content scheme URIs
        if (ContentResolver.SCHEME_CONTENT.equals(uri.getScheme())) {
            ContentResolver contentResolver = context.getContentResolver();
            MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
            extension = mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
        }
        // Handle file scheme URIs
        else if (ContentResolver.SCHEME_FILE.equals(uri.getScheme())) {
            String path = uri.getPath();
            int lastDotIndex = path.lastIndexOf(".");
            if (lastDotIndex != -1) {
                extension = path.substring(lastDotIndex + 1);
            }
        }

        return extension;
    }
    public void processVideoUpload(){
        if (videoUri == null){
            Toast.makeText(AddVideo.this, "Please Browse Video", Toast.LENGTH_SHORT).show();
        }else{
            String videoTitle = binding.titleEt.getText().toString();
            if (videoTitle.isEmpty()){
                binding.titleEt.setError("required!");
            }else{
                progressDialog.show();

                String videoExtension = getFileExtensionFromUri(getApplicationContext(), videoUri);

                StorageReference videoRef = storageReference.child("video :"+System.currentTimeMillis() +"."+ videoExtension);
                videoRef.putFile(videoUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        progressDialog.dismiss();
                        videoRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String videoUrl = uri.toString();
                                videoUploadModel model = new videoUploadModel(videoTitle, videoUrl);
                                DatabaseReference videoDBRef = databaseReference.child("myVideos");
                                videoDBRef.push().setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(AddVideo.this, "Data Uploaded", Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }
                        });

                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        float per = (snapshot.getBytesTransferred() * 100) / snapshot.getTotalByteCount();
                        progressDialog.setMessage("Uploading: "+(int)per +"%");
                    }
                });
            }
        }
    }

}