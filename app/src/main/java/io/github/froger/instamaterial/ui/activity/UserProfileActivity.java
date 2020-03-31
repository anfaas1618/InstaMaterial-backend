package io.github.froger.instamaterial.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.logging.Logger;

import butterknife.BindView;
import de.hdodenhof.circleimageview.CircleImageView;
import io.github.froger.instamaterial.R;
import io.github.froger.instamaterial.model.User;
import io.github.froger.instamaterial.ui.adapter.UserProfileAdapter;
import io.github.froger.instamaterial.ui.utils.CircleTransformation;
import io.github.froger.instamaterial.ui.view.RevealBackgroundView;

/**
 * Created by Miroslaw Stanek on 14.01.15.
 */
public class UserProfileActivity extends BaseDrawerActivity implements RevealBackgroundView.OnStateChangeListener {
    public static final String ARG_REVEAL_START_LOCATION = "reveal_start_location";

    private static final int USER_OPTIONS_ANIMATION_DELAY = 300;
    private static final Interpolator INTERPOLATOR = new DecelerateInterpolator();

    @BindView(R.id.vRevealBackground)
    RevealBackgroundView vRevealBackground;
    @BindView(R.id.rvUserProfile)
    RecyclerView rvUserProfile;

    @BindView(R.id.tlUserProfileTabs)
    TabLayout tlUserProfileTabs;

    @BindView(R.id.p_userimage)
    CircleImageView p_userimage;
    @BindView(R.id.p_bio)
    TextView p_bio;
    @BindView(R.id.p_realname)
    TextView p_realname;
    @BindView(R.id.p_followers)
    TextView p_followers;
    @BindView(R.id.p_following)
    TextView p_following;
    @BindView(R.id.p_username)
    TextView p_username;
    @BindView(R.id.vUserDetails)
    View vUserDetails;
    @BindView(R.id.btnFollow)
    Button btnFollow;
    @BindView(R.id.vUserStats)
    View vUserStats;
    @BindView(R.id.vUserProfileRoot)
    View vUserProfileRoot;

    private int avatarSize;
    private String profilePhoto;
    private UserProfileAdapter userPhotosAdapter;

    public static void startUserProfileFromLocation(int[] startingLocation, Activity startingActivity) {
        Intent intent;
        intent = new Intent(startingActivity, UserProfileActivity.class);
        intent.putExtra(ARG_REVEAL_START_LOCATION, startingLocation);
        startingActivity.startActivity(intent);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        FirebaseDatabase basicdatabase=FirebaseDatabase.getInstance();
        SharedPreferences UIDsave =getSharedPreferences("UID", Context.MODE_PRIVATE);
        String uidSaved=UIDsave.getString("UID",null);
        DatabaseReference basicInfoRef=basicdatabase.getReference("Users");
        Log.i("check",uidSaved.trim());
        basicInfoRef.child(uidSaved).child("basicInfo").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    User user = dataSnapshot.getValue(User.class);
                        p_realname.setText(user.getName());
                        p_username.setText(user.getUsername());


                Picasso.with(UserProfileActivity.this).load(user.getAvatar().trim()).centerCrop().fit().into(p_userimage);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        basicInfoRef.child(uidSaved).child("bio").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot streing:dataSnapshot.getChildren()) {
                    String a=streing.getValue(String.class);
                    p_bio.setText(a);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        setupTabs();
        setupUserProfileGrid();
        setupRevealBackground(savedInstanceState);
    }

    private void setupTabs() {
        tlUserProfileTabs.addTab(tlUserProfileTabs.newTab().setIcon(R.drawable.ic_grid_on_white));
        tlUserProfileTabs.addTab(tlUserProfileTabs.newTab().setIcon(R.drawable.ic_list_white));
        tlUserProfileTabs.addTab(tlUserProfileTabs.newTab().setIcon(R.drawable.ic_place_white));
        tlUserProfileTabs.addTab(tlUserProfileTabs.newTab().setIcon(R.drawable.ic_label_white));
    }

    private void setupUserProfileGrid() {
        final StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        rvUserProfile.setLayoutManager(layoutManager);
        rvUserProfile.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                userPhotosAdapter.setLockedAnimations(true);
            }
        });
    }

    private void setupRevealBackground(Bundle savedInstanceState) {
        vRevealBackground.setOnStateChangeListener(this);
        if (savedInstanceState == null) {
            final int[] startingLocation = getIntent().getIntArrayExtra(ARG_REVEAL_START_LOCATION);
            vRevealBackground.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    vRevealBackground.getViewTreeObserver().removeOnPreDrawListener(this);
                    vRevealBackground.startFromLocation(startingLocation);
                    return true;
                }
            });
        } else {
            vRevealBackground.setToFinishedFrame();
            userPhotosAdapter.setLockedAnimations(true);
        }
    }

    @Override
    public void onStateChange(int state) {
        if (RevealBackgroundView.STATE_FINISHED == state) {
            rvUserProfile.setVisibility(View.VISIBLE);
            tlUserProfileTabs.setVisibility(View.VISIBLE);
            vUserProfileRoot.setVisibility(View.VISIBLE);
            userPhotosAdapter = new UserProfileAdapter(this);
            rvUserProfile.setAdapter(userPhotosAdapter);
            animateUserProfileOptions();
            animateUserProfileHeader();
        } else {
            tlUserProfileTabs.setVisibility(View.INVISIBLE);
            rvUserProfile.setVisibility(View.INVISIBLE);
            vUserProfileRoot.setVisibility(View.INVISIBLE);
        }
    }

    private void animateUserProfileOptions() {
        tlUserProfileTabs.setTranslationY(-tlUserProfileTabs.getHeight());
        tlUserProfileTabs.animate().translationY(0).setDuration(300).setStartDelay(USER_OPTIONS_ANIMATION_DELAY).setInterpolator(INTERPOLATOR);
    }

    private void animateUserProfileHeader() {

    }
}
