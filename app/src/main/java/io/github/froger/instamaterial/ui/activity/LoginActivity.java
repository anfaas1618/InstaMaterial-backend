package io.github.froger.instamaterial.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import io.github.froger.instamaterial.R;

public class LoginActivity extends AppCompatActivity {
    EditText t_email,t_password;
    Button b_login;
    FirebaseAuth myauth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        t_email =findViewById(R.id.l_email);
        t_password=findViewById(R.id.l_password);
        b_login= findViewById(R.id.l_btnLogin);
        b_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              login(t_email.getText().toString().trim(),
                      t_password.getText().toString().trim());

            }
        });

    }

    public void login(String s_email, String s_password) {
        myauth =FirebaseAuth.getInstance();
        myauth.signInWithEmailAndPassword(s_email,s_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful())
                {
                    Toast.makeText(LoginActivity.this, "login sucessfull", Toast.LENGTH_LONG).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LoginActivity.this, "login failed"+e.getMessage(), Toast.LENGTH_LONG).show();

            }
        });
    }
}
