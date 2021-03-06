package io.github.froger.instamaterial.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

import io.github.froger.instamaterial.R;
import io.github.froger.instamaterial.model.User;
import io.github.froger.instamaterial.ui.utils.OnSwipeTouchListener;


public class RealRegisterActivity extends AppCompatActivity {
    static final boolean  debugmode=false;//TODO change it for production
    private static final String TAG ="register act" ;
    EditText t_email,t_password;
   public boolean firebaseregisterdone=false;
    Button Login_btn;
    Button Register_btn;
    FirebaseAuth loginAuth;
    FirebaseDatabase firebaseDatabase= FirebaseDatabase.getInstance();
    DatabaseReference loginRef=firebaseDatabase.getReference("Users");
    ImageView imageView;
    FirebaseRemoteConfig mFirebaseRemoteConfig;
    TextView textView;
   public ProgressDialog progressDialog;
    int count = 0;
public static  String  MysqlURL="dono";


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //TODO create a key "serverside" and child key "mysqlurl" with value "you domain name with https://" ,else "https://localhost"
        DatabaseReference serverSideChanges=firebaseDatabase.getReference("serversidechanges");
        serverSideChanges.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot:dataSnapshot.getChildren())
                {
                    if (snapshot.getKey().trim().equals("mysqlurl"))
                    {
                        MysqlURL=snapshot.getValue(String.class).trim();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(3600)
                .build();
        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_real_register);
        if (debugmode)
        {
            startActivity(new Intent(this,UsernameSelect.class));
        }
        t_email=findViewById(R.id.edit_email);
        t_password=findViewById(R.id.edit_pass);
        Register_btn=findViewById(R.id.register_btn);
        Login_btn =findViewById(R.id.login_btn);
        Register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean check_login=      register(t_email.getText().toString().trim(),
                        "null",
                        t_password.getText().toString().trim()
                );
            }
        });
        Login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RealRegisterActivity.this, LoginActivity.class));
            }
        });
        imageView = findViewById(R.id.imageVIEW);
        textView = findViewById(R.id.textView);
        imageView.setOnTouchListener(new OnSwipeTouchListener(this.getApplicationContext()) {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return super.onTouch(v, event);
            }

            public void onSwipeTop() {

            }

            @SuppressLint("ClickableViewAccessibility")
            public void onSwipeRight() {
                if (count == 0) {
                    imageView.setImageResource(R.drawable.good_night_img);
                    textView.setText("Night");
                    count = 1;
                } else {
                    imageView.setImageResource(R.drawable.good_morning_img);
                    textView.setText("Morning");
                    count = 0;
                }
            }

            public void onSwipeLeft() {
                if (count == 0) {
                    imageView.setImageResource(R.drawable.good_night_img);
                    textView.setText("Night");
                    count = 1;
                } else {
                    imageView.setImageResource(R.drawable.good_morning_img);
                    textView.setText("Morning");
                    count = 0;
                }
            }

            public void onSwipeBottom() {
            }

        });
    }
    private Boolean register(String s_email, String s_name, String s_password) {
        final Boolean[] is_sucess = new Boolean[1];
        loginAuth =FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(RealRegisterActivity.this);
        progressDialog.show();
        loginAuth.createUserWithEmailAndPassword(s_email,s_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful())
                {
                    FirebaseUser user=loginAuth.getCurrentUser();

                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(s_name)
                            .setPhotoUri(Uri.parse("https://i7.pngguru.com/preview/867/694/539/user-profile-default-computer-icons-network-video-recorder-avatar-cartoon-maker.jpg"))
                            .build();//Todo copy this to username class
                    is_sucess[0] =true;
                    if (user != null) {
                        user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Snackbar snackbar= Snackbar.make(Register_btn, "profile created",1000 );
                                snackbar.show();
                                createUser(user);

                            }
                        });
                    }


                }
                else
                    is_sucess[0]=false;
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                is_sucess[0] =false;
                Toast.makeText(RealRegisterActivity.this, "server error",
                        Toast.LENGTH_SHORT).show();
            }
        });
        return  is_sucess[0];
    }

    private void register_mysql(String uid, String s_email, String s_password) {
        String url=MysqlURL+
                "/instamagic/registeract.php?uid="
                +uid+
                "&email="
                +s_email+
                "&password="
                +s_password;
        Log.i(TAG, "register_mysql: "+url);
      new task_mysql_register().execute(url);
    }
    //anfaas-com.stackstaging.com

    private void createUser(FirebaseUser user) {
        User databaseUser=new User(user.getDisplayName().trim(),
                user.getUid().trim(),
                user.getPhotoUrl().toString().trim(),
                user.getEmail().trim(),
                "null"
        );
        loginRef.child(user.getUid()).child("basicInfo").setValue(databaseUser);
        String  biostring="hey i am using instaclone";//TODO change it asap
        loginRef.child(user.getUid()).child("bio").child(user.getUid()).setValue(biostring);
        loginRef.child(user.getUid()).child("followers").child("his userid").setValue("usernamewa");
        loginRef.child(user.getUid()).child("followers").child("his userid2").setValue("lucjy");
        loginRef.child(user.getUid()).child("followers").child("his userid3").setValue("dandia");
        loginRef.child(user.getUid()).child("following").child("his userid").setValue("usernamewa");
        loginRef.child(user.getUid()).child("following").child("his userid2").setValue("lucjy");
        loginRef.child(user.getUid()).child("following").child("his userid3").setValue("dandia");
        SharedPreferences UIDsave =getSharedPreferences("UID", Context.MODE_PRIVATE);
        SharedPreferences.Editor uidadd =UIDsave.edit();
        uidadd.putString("UID",user.getUid().trim());
        uidadd.apply();
        //Todo add here mysql code to store username password and image
        firebaseregisterdone=true;
        register_mysql(user.getUid().trim(),user.getEmail().trim(),t_password.getText().toString().trim());
        //Todo after addding the usename and password set a primary key to the username




    }
    public class task_mysql_register extends AsyncTask<String,Void,Void>{


        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                Document doc = Jsoup.connect(strings[0]).get();
                Log.i(TAG, "doInBackground: "+doc.text());
                //TODO add a if doc==registerd sucess case
                startActivity(new Intent(RealRegisterActivity.this, UsernameSelect.class));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
