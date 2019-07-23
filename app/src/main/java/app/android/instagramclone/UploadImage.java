package app.android.instagramclone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

public class UploadImage extends AppCompatActivity {

    public static final int IMAGE_CHOOSE = 1;
    EditText captionPost;
    Button btnUpload;
    ImageView photoUpload;
    StorageReference storageRef;
    DatabaseReference feedRef , randomRef;
    StorageTask uploadTask;
    ProgressBar loading;
    Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_image);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String idUser = currentUser.getUid();

        photoUpload = (ImageView) findViewById(R.id.imagePilihan);
        btnUpload = (Button) findViewById(R.id.btnUpload);
        loading = (ProgressBar) findViewById(R.id.loading);
        storageRef = FirebaseStorage.getInstance().getReference("feed");
        feedRef = FirebaseDatabase.getInstance().getReference("feed").child(idUser);
        randomRef = FirebaseDatabase.getInstance().getReference("random");
        captionPost = (EditText) findViewById(R.id.caption);

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (uploadTask!= null && uploadTask.isInProgress()){
                    Toast.makeText(UploadImage.this, "Sedang Mengupload", Toast.LENGTH_SHORT).show();
                }else {
                    uploadImage();
                }
            }
        });

        photoUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pilihGambar();
            }
        });
    }

    public void handlerProgressbar(){
        Handler hd = new Handler();
        hd.postDelayed(new Runnable() {
            @Override
            public void run() {
                loading.setProgress(0);
            }
        },500);
    }

    private void uploadImage() {
        if (imageUri!= null){
            final StorageReference storageReference =
                    storageRef.child(System.currentTimeMillis()+"."+"jpg");

            uploadTask = storageReference.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            handlerProgressbar();
                            Toast.makeText(UploadImage.this, "Upload Success", Toast.LENGTH_SHORT).show();

                            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String idPost = feedRef.push().getKey();
                                    String caption = captionPost.getText().toString().trim();
                                    String urlPostPicture = uri.toString();
                                    Upload upload = new Upload(idPost,caption,urlPostPicture);
                                    feedRef.child(idPost).setValue(upload);
                                    randomRef.child(idPost).setValue(upload);
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(UploadImage.this, "Terjadi Kesalahan", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progres =(100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            loading.setProgress((int) progres);
                        }
                    });
        }else{
            Toast.makeText(this, "No Image Selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void pilihGambar() {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent , IMAGE_CHOOSE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_CHOOSE && resultCode == RESULT_OK && data != null && data.getData() != null){
            imageUri = data.getData();
            Glide.with(this).load(imageUri).into(photoUpload);
        }
    }
}
