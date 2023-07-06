package com.saif.videostreamingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.saif.videostreamingapp.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
ActivityMainBinding binding;
FirebaseAuth auth;
DatabaseReference likeReferece;
Boolean testClick = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        auth = FirebaseAuth.getInstance();
        likeReferece = FirebaseDatabase.getInstance().getReference("likes");


        binding.logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
            }
        });

        binding.addVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, AddVideo.class));
            }
        });

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FirebaseRecyclerOptions<videoUploadModel> options =
                new FirebaseRecyclerOptions.Builder<videoUploadModel>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("myVideos"), videoUploadModel.class)
                        .build();

        FirebaseRecyclerAdapter<videoUploadModel, myViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<videoUploadModel, myViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull myViewHolder holder, int position, @NonNull videoUploadModel model) {
                Log.e("TAG", "videotitle: " + model.getVideoTitle().toString());

                holder.prefareExoPlayer(getApplication(), model.getVideoTitle().toString(), model.getVideoUrl());

                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                String uId = firebaseUser.getUid();

                String videoId = getRef(position).getKey();

                holder.getLikeButtonStatus(uId, videoId);

                // like button click
                holder.like_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        testClick = true;
                        likeReferece.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                               if (testClick == true){
                                   if (snapshot.child(videoId).hasChild(uId)){
                                       likeReferece.child(videoId).removeValue();
                                       testClick = false;
                                   }else{
                                       likeReferece.child(videoId).child(uId).setValue(true);
                                       testClick = false;
                                   }
                               }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                });

                //comment btn click
                holder.comment_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent intent = new Intent(MainActivity.this, commentActivity.class);
                        intent.putExtra("videoId", videoId);
                        startActivity(intent);

                    }
                });
            }

            @NonNull
            @Override
            public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_row, parent, false);
                return new myViewHolder(view);
            }
        };
        firebaseRecyclerAdapter.startListening();
        binding.recyclerView.setAdapter(firebaseRecyclerAdapter);
//        binding.signOutBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                auth.signOut();
//                startActivity(new Intent(MainActivity.this, LoginActivity.class));
//                finish();
//            }
//        });
    }
}