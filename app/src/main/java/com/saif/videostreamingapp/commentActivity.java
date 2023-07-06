package com.saif.videostreamingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.saif.videostreamingapp.databinding.ActivityCommentBinding;

import android.app.Application;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Random;

public class commentActivity extends AppCompatActivity {
ActivityCommentBinding binding;
DatabaseReference userProfileRef, commentRef;
    String uId;
String videoId;
    ExoPlayer exoPlayer;
    PlayerView exoplayerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCommentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        videoId = getIntent().getStringExtra("videoId");

        uId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        userProfileRef = FirebaseDatabase.getInstance().getReference().child("Users/profile");
        commentRef = FirebaseDatabase.getInstance().getReference().child("myVideos/"+videoId+"/comments");




        binding.recyclerView2.setLayoutManager(new LinearLayoutManager(this));





        binding.sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userProfileRef.child(uId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            String userName = snapshot.child("name").getValue().toString();
                            String userProfile = snapshot.child("imagePath").getValue().toString();
                            processComment(userName, userProfile);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });



    }

    private void processComment(String userName, String userProfile) {
        String commentText = binding.commentEt.getText().toString();

        String randompostkey = uId+""+new Random().nextInt(1000);
        Calendar datevalue = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-mm-yy");
        String cdate = dateFormat.format (datevalue.getTime());

        SimpleDateFormat timeFormat = new SimpleDateFormat( "HH:mm");
        String ctime = timeFormat.format(datevalue.getTime());

        HashMap commentMap = new HashMap();
        commentMap.put("uid", uId);
        commentMap.put("username", userName);
        commentMap.put("userprofile", userProfile);
        commentMap.put("commentText", commentText);
        commentMap.put("date", cdate);
        commentMap.put("time", ctime);

        commentRef.child(randompostkey).updateChildren(commentMap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()){
                    Toast.makeText(commentActivity.this, "Comment Added!", Toast.LENGTH_SHORT).show();
                    binding.commentEt.setText("");
                }else{
                    Toast.makeText(commentActivity.this, "Commend Failed!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<commentModel> options =
                new FirebaseRecyclerOptions.Builder<commentModel>()
                        .setQuery(commentRef, commentModel.class)
                        .build();

        FirebaseRecyclerAdapter<commentModel,commentViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<commentModel, commentViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull commentViewHolder holder, int position, @NonNull commentModel model) {
                holder.cuname.setText(model.getUsername());
                holder.cumessage.setText(model.getCommentText());
                holder.cudt.setText("Date :"+model.getDate()+" Time :"+model.getTime());
                Glide.with(holder.cuimage.getContext()).load(model.getUserprofile()).into(holder.cuimage);
            }

            @NonNull
            @Override
            public commentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_single_row,parent,false);
                return  new commentViewHolder(view);
            }
        };

        firebaseRecyclerAdapter.startListening();
        binding.recyclerView2.setAdapter(firebaseRecyclerAdapter);

    }



}