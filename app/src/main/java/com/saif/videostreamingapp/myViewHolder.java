package com.saif.videostreamingapp;

import android.app.Application;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.util.Util;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class myViewHolder extends RecyclerView.ViewHolder {
ExoPlayer exoPlayer;
PlayerView exoplayerView;
TextView videoName, like_no;
ImageView like_btn, comment_btn;
DatabaseReference likeReference;

    public myViewHolder(@NonNull View itemView) {
        super(itemView);
        exoplayerView = itemView.findViewById(R.id.ExoPlayerVIew);
        videoName = itemView.findViewById(R.id.videoName);
        like_btn = itemView.findViewById(R.id.like_btn);
        like_no = itemView.findViewById(R.id.like_no);
        comment_btn = itemView.findViewById(R.id.comment_btn);
    }

    public void getLikeButtonStatus(final String uId, final String videoId){
        likeReference = FirebaseDatabase.getInstance().getReference("likes");
        likeReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(videoId).hasChild(uId)){
                    int likeCount = (int)snapshot.child(videoId).getChildrenCount();
                    like_no.setText(likeCount + "likes");
                    like_btn.setImageResource(R.drawable.favourite_fill);

                }else{
                    int likeCount = (int)snapshot.child(videoId).getChildrenCount();
                    like_no.setText(likeCount + "likes");
                    like_btn.setImageResource(R.drawable.favourite_outline);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    void prefareExoPlayer(Application application, String vName, String vUrl){
        try {
            videoName.setText(vName);
           exoPlayer = new ExoPlayer.Builder(application).build();
           exoplayerView.setPlayer(exoPlayer);

           Uri videoUri = Uri.parse(vUrl);

            MediaItem mediaItem = MediaItem.fromUri(videoUri);
// Set the media item to be played.
            exoPlayer.setMediaItem(mediaItem);
// Prepare the player.
            exoPlayer.prepare();
// Start the playback.
            exoPlayer.setPlayWhenReady(false);

        } catch (Exception e) {
            // below line is used for handling our errors.
            Log.e("TAG", "Error: " + e.toString());
        }

    }

}
