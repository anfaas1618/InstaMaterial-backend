package io.github.froger.instamaterial.ui.activity;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.HashMap;
import io.github.froger.instamaterial.R;
import io.github.froger.instamaterial.model.User;

public class UsernameSelect extends AppCompatActivity {
    private static final String TAG ="asdw" ;
    EditText txt_username;
    Button btn_check;
    FirebaseAuth myauth;
    FirebaseUser user;
    Context context;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference usernamesRef=database.getReference("usernames");
    FirebaseDatabase usersDatabase=FirebaseDatabase.getInstance();
    DatabaseReference userRef=usersDatabase.getReference("Users");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_username_select);
     String s= RealRegisterActivity.MysqlURL;
        Log.i(TAG, "onCreate: "+s);
        Toast.makeText(UsernameSelect.this, s, Toast.LENGTH_SHORT).show();
        btn_check=findViewById(R.id.btn_check_username);
        txt_username =findViewById(R.id.txt_username);
        btn_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkUsers(txt_username.getText().toString().trim());
            }
        });
        myauth=FirebaseAuth.getInstance();
     user=   myauth.getCurrentUser();

       // Log.i("anfaas",user.getEmail());

    }

    public void checkUsers(String username_try) {
        usernamesRef.addValueEventListener(new ValueEventListener() {
            int x=0;
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot username : dataSnapshot.getChildren()) {
                    if (username_try.equals(username.getValue().toString().trim())) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(UsernameSelect.this);
                        builder.setTitle("OOPS!");
                        builder.setIcon(R.drawable.cross);
                        x=1;
                        builder.setMessage("username is already taken.");
                        builder.setPositiveButton("damn it!", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.dismiss();
                            }
                        });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    }
                }
                if (x != 1) {
                    addUsernameToDatabase(username_try);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
               Log.i("anfaas",databaseError.getMessage());
            }
        });
    }

    private void addUsernameToDatabase(String username_try) {

        HashMap usernameMAP = new HashMap();
        usernameMAP.put(user.getUid(), username_try);
        Dialog d = new Dialog(UsernameSelect.this);
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        d.setContentView(R.layout.dialog_layout);// custom layour for dialog.
      //  Button thankyou = (Button) d.findViewById(R.id.thankyou);
        d.show();
//        LayoutInflater inflater = (LayoutInflater) this
//                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View dialoglayout = inflater.inflate(R.layout.dialog_layout, null,true);
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setView(dialoglayout);
//        builder.setCancelable(false);
//        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog,
//                                                int which) {
//                                usernamesRef.child(user.getUid()).
//
//                                        setValue(username_try);
//                                User localUser= new User(user.getDisplayName(),
//                                        user.getUid(),
//                                        user.getPhotoUrl().toString(),
//                                        user.getEmail(),username_try);
//                                userRef.child(user.getUid()).child("basicInfo").setValue(localUser);
//                                SharedPreferences UsernameSave= getSharedPreferences("USERNAME", Context.MODE_PRIVATE);
//                                SharedPreferences.Editor useredit =UsernameSave.edit();
//                                useredit.putString("USERNAME",username_try);
//                                useredit.commit();
//                                startActivity( new Intent(UsernameSelect.this,ImageProfileActivity.class));
//                            }
//                        });
//        builder.setNegativeButton(
//                        "No,let me choose",
//                        new DialogInterface
//                                .OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog,
//                                                int which) {
//                                 btn_check.setVisibility(View.VISIBLE);
//                                // If user click no
//                                // then dialog box is canceled.
//                                dialog.cancel();
//                            }
//                        });
//        builder.show();



    }


}
