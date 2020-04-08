package io.github.froger.instamaterial.ui.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.drawerlayout.widget.DrawerLayout;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.BindDimen;
import io.github.froger.instamaterial.R;
import io.github.froger.instamaterial.model.User;
import io.github.froger.instamaterial.ui.utils.CircleTransformation;

/**
 * Created by Miroslaw Stanek on 15.07.15.
 */
public class BaseDrawerActivity extends BaseActivity {
    FirebaseDatabase database=FirebaseDatabase.getInstance();
    DatabaseReference myref=database.getReference("Users");
    @BindView(R.id.drawerLayout)
    DrawerLayout drawerLayout;
    @BindView(R.id.vNavigation)
    NavigationView vNavigation;
 public    Boolean clicked=true;
ListView usernames_list;
    EditText username_global;
    private  ImageView seachbtn;

    @BindDimen(R.dimen.global_menu_avatar_size)
    int avatarSize;
ArrayAdapter<String> adapter;
    List <String> usernames;

    //Cannot be bound via Butterknife, hosting view is initialized later (see setupHeader() method)
    private ImageView ivMenuUserProfilePhoto;
    private ImageView searchtry;
    private String TAG="BASEdadad";

    @Override
    public void setContentView(int layoutResID) {
        super.setContentViewWithoutInject(R.layout.activity_drawer);
        ViewGroup viewGroup = (ViewGroup) findViewById(R.id.flContentRoot);

        LayoutInflater.from(this).inflate(layoutResID, viewGroup, true);
        bindViews();

        setupUsernames();
        setupHeader();
    }

    private void setupUsernames() {

    }

    @Override
    protected void setupToolbar() {
        super.setupToolbar();
        if (getToolbar() != null) {
            getToolbar().setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawerLayout.openDrawer(Gravity.LEFT);
                }
            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        usernames=new ArrayList<>();
        username_global.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.i(TAG, "beforeTextChanged: "+s+start+"DD"+before+"dd"+count);
                adapter.getFilter().filter(s);

            }

            @Override
            public void afterTextChanged(Editable s) {

                Log.i(TAG, "beforeTextChangedd: "+s.toString());

            }
        });
    }

    private void setupHeader() {

        myref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot:dataSnapshot.getChildren())
                { String key =snapshot.getKey();
                    Log.i(TAG, "onDataChange: "+key);
            User user=        snapshot.child("basicInfo").getValue(User.class);
                    Log.i(TAG, "onDataChange: "+user.getName());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        SharedPreferences uid=getSharedPreferences("UID", Context.MODE_PRIVATE);
        View headerView = vNavigation.getHeaderView(0);
        usernames_list =headerView.findViewById(R.id.list_usernames);
         username_global= headerView.findViewById(R.id.username_global);
        String uidSaved=uid.getString("UID",null);
        searchtry =  headerView.findViewById(R.id.search_try);
        ivMenuUserProfilePhoto =  headerView.findViewById(R.id.ivMenuUserProfilePhoto);


        myref.child(uidSaved).child("basicInfo").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    username_global.setHint(user.getUsername().trim());
                Picasso.with(BaseDrawerActivity.this)
                        .load(user.getAvatar())
                        .placeholder(R.drawable.img_circle_placeholder)
                        .resize(avatarSize, avatarSize)
                        .centerCrop()
                        .transform(new CircleTransformation())
                        .into(ivMenuUserProfilePhoto);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




               headerView.findViewById(R.id.ivMenuUserProfilePhoto).setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       onGlobalMenuHeaderClick(v);

                   }
               });

        headerView.findViewById(R.id.search_try).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clicked==true) {
                    username_global.setVisibility(View.VISIBLE);
              ImageView view=      headerView.findViewById(R.id.search_try);
              view.setVisibility(View.INVISIBLE);

                    Log.i(TAG, "onClick: here we are !!");
                    Query query = database.getReference().child("usernames");
                    query.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                String a = snapshot.getValue(String.class);
                                Log.i(TAG, "onDataChangddasde: " + a);
                                usernames.add(a);
                            }
                            adapter = new ArrayAdapter<>(BaseDrawerActivity.this, R.layout.list_got, R.id.dd, usernames);
                            ViewGroup.LayoutParams params = usernames_list.getLayoutParams();
                            usernames_list.setAdapter(adapter);
                                params.height = 1200;
                                usernames_list.setLayoutParams(params);
                           view.setImageResource(R.drawable.cross);
                            headerView.findViewById(R.id.search_try).setVisibility(View.VISIBLE);


                            clicked=false;
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
                else
                {
                    ViewGroup.LayoutParams params = usernames_list.getLayoutParams();
                    params.height = 12;
                    usernames_list.setLayoutParams(params);
                    usernames_list.setTranslationY(100);
                    username_global.setVisibility(View.GONE);
         ImageView     view=      headerView.findViewById(R.id.search_try);
         view.setImageResource(R.drawable.ic_global_menu_search);
                    clicked=true;

                }
            }
        });



        headerView.findViewById(R.id.username_global).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
            }
        });

    }

    private void searchUsers(String username_for_search) {
        Log.i(TAG, "searchUsers: "+username_for_search);
        //TODO create fucn to seach for users in mysql database
    }

    public void onGlobalMenuHeaderClick(final View v) {
        drawerLayout.closeDrawer(Gravity.LEFT);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                int[] startingLocation = new int[2];
                v.getLocationOnScreen(startingLocation);
                startingLocation[0] += v.getWidth() / 2;
                UserProfileActivity.startUserProfileFromLocation(startingLocation, BaseDrawerActivity.this);
                overridePendingTransition(0, 0);
            }
        }, 200);
    }
    public  void onsearchclick(View view)
    {
        Log.i("DADA dajsd",  username_global.getText().toString().trim());
    }

}
