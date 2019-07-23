package app.android.instagramclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    EditText eEmail , ePassword , eRepeat;
    Button btnSignUp;
    ProgressBar loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        eEmail = (EditText) findViewById(R.id.usernameRegister);
        ePassword = (EditText) findViewById(R.id.passwordRegister);
        eRepeat = (EditText) findViewById(R.id.repeatPassword);
        btnSignUp = (Button) findViewById(R.id.signUp);
        loading = (ProgressBar) findViewById(R.id.loading);
        mDatabase = FirebaseDatabase.getInstance().getReference("user");
        mAuth = FirebaseAuth.getInstance();

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerAccountInstagram();
            }
        });

    }

    private void registerAccountInstagram() {
        String email = eEmail.getText().toString().trim();
        String password = ePassword.getText().toString().trim();
        String repeat = eRepeat.getText().toString().trim();

        if (email.isEmpty()){
            eEmail.setError("Email Tidak Boleh Kosong");
            eEmail.requestFocus();
            return;
        }

        if (password.isEmpty()){
            ePassword.setError("Password Tidak Boleh Kosong");
            ePassword.requestFocus();
            return;
        }

        if (repeat.isEmpty()){
            eRepeat.setError("Harap Masukan Ulang Password");
            eRepeat.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            eEmail.setError("Masukan Email Anda Dengan Benar");
            eEmail.requestFocus();
            return;
        }

        if (password.length() < 6){
            ePassword.setError("Password Tidak Boleh Kurang Dari 6 Karakter");
            ePassword.requestFocus();
            return;
        }

        if (!password.equals(repeat)){
            eRepeat.setError("Password Tidak Sama");
            return;
        }

        loading.setVisibility(View.VISIBLE);

        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        loading.setVisibility(View.GONE);
                        if (task.isSuccessful()){

                            String userId = mAuth.getUid();
                            User user = new User(userId);
                            mDatabase.child(userId).setValue(user);

                            finish();
                            Intent intent = new Intent(RegisterActivity.this , LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);

                        }else {
                            String error = task.getException().toString();
                            Toast.makeText(RegisterActivity.this, error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }
}
