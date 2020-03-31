package io.github.froger.instamaterial.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import io.github.froger.instamaterial.R;
import io.github.froger.instamaterial.model.User;

public class RegisterActivity extends AppCompatActivity {

    EditText t_email,t_password,t_name;
    TextView lnklogin;
    Button b_login;
    FirebaseAuth loginAuth;
    FirebaseDatabase firebaseDatabase= FirebaseDatabase.getInstance();
    DatabaseReference loginRef=firebaseDatabase.getReference("Users");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        t_email=findViewById(R.id.txtEmail);
        t_name=findViewById(R.id.txtName);
        t_password=findViewById(R.id.txtPwd);
        b_login=findViewById(R.id.btnLogin);
        lnklogin =findViewById(R.id.lnkLogin);

        b_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
          Boolean check_login=      register(t_email.getText().toString().trim(),
                        t_name.getText().toString().trim(),
                        t_password.getText().toString().trim()
                );
            }
        });
        lnklogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });
    }

    private Boolean register(String s_email, String s_name, String s_password) {
        final Boolean[] is_sucess = new Boolean[1];
        loginAuth =FirebaseAuth.getInstance();
        loginAuth.createUserWithEmailAndPassword(s_email,s_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful())
                {
                    FirebaseUser user=loginAuth.getCurrentUser();
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(s_name)
                            .setPhotoUri(Uri.parse("https://i7.pngguru.com/preview/867/694/539/user-profile-default-computer-icons-network-video-recorder-avatar-cartoon-maker.jpg"))
                            .build();
                       is_sucess[0] =true;
                    if (user != null) {
                        user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Snackbar snackbar= Snackbar.make(b_login, "profile created",1000 );
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
                Toast.makeText(RegisterActivity.this, "server error",
                        Toast.LENGTH_SHORT).show();
            }
        });
return  is_sucess[0];
    }

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
startActivity(new Intent(RegisterActivity.this, UsernameSelect.class));


    }
}
