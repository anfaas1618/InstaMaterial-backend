package io.github.froger.instamaterial.ui.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import de.hdodenhof.circleimageview.CircleImageView;
import io.github.froger.instamaterial.R;
import io.github.froger.instamaterial.model.User;


public class ImageProfileActivity extends AppCompatActivity {
    Button btnChooseImg;
    Button btnUpload;
    private static final int PICK_IMAGE_REQUEST=1;
    Uri urlupload;
    Uri imageuri;
    Button okdone;;
    String email,password;
    CircleImageView setImageCircle;

    DatabaseReference myref;
    StorageReference reference;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_profile);
firebaseAuth= FirebaseAuth.getInstance();
firebaseUser=firebaseAuth.getCurrentUser();

        btnChooseImg=findViewById(R.id.choseimg);
        btnUpload=findViewById(R.id.uploadimg);
        okdone=findViewById(R.id.btnDoneUpload);
        setImageCircle=(CircleImageView)findViewById(R.id.profile_image);
        //  setimage=findViewById(R.id.imagePreview);
        reference = FirebaseStorage.getInstance().getReference("uploads");
        FirebaseDatabase database=FirebaseDatabase.getInstance();
        myref=database.getReference("Users");
        btnChooseImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadFirebase();
            }
        });
        okdone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Log.i("msg",email);
                }catch (Exception e)
                {
                    e.printStackTrace();
                }
                if (imageuri!=null)
                    startActivity(   new Intent(ImageProfileActivity.this, MainActivity.class));
                else
                    Toast.makeText(ImageProfileActivity.this, "please choose a image", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void uploadFirebase() {
        if (imageuri!=null)
        {
            final StorageReference ref=reference.child(System.currentTimeMillis()+"."+"PNG");
            ref.putFile(imageuri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(ImageProfileActivity.this, "sucess", Toast.LENGTH_SHORT).show();
                    try {



                        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Log.i("urlis",uri.toString());
                                Toast.makeText(ImageProfileActivity.this, "got it do it now", Toast.LENGTH_SHORT).show();
                                urlupload=uri;
                                final     SharedPreferences UsernameSave= getSharedPreferences("USERNAME", Context.MODE_PRIVATE);
                           String username_saved=     UsernameSave.getString("USERNAME",null);
                                User myuser =new User(firebaseUser.getDisplayName(),
                                                    firebaseUser.getUid(),
                                                    urlupload.toString(),
                                                   firebaseUser.getEmail(),
                                                   username_saved);

                                myref.child(firebaseUser.getUid()).child("basicInfo").setValue(myuser);
                            }
                        });

                    }
                    catch ( Exception e)
                    {
                        Toast.makeText(ImageProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }
            });

        }
    }

    public  void checkimage(View view)
    {
        try
        {
            Picasso.with(this).load(imageuri).centerCrop().fit().into(setImageCircle);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    private  void uploadImage()
    {
//    Intent intent=new Intent();
//    intent.setType("image/*");
//    intent.setAction(Intent.ACTION_GET_CONTENT);
//    startActivityForResult(intent,PICK_IMAGE_REQUEST);
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1,1)
                .start(this);

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null) {



            //  imageuri=data.getData();
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imageuri = result.getUri();
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
        Log.i("anfaas", String.valueOf(imageuri));
        //  setimage.setImageURI(imageuri);
    }

}
