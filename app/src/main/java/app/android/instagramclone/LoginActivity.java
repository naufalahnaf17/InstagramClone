package app.android.instagramclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    EditText eEmail , ePassword;
    Button login;
    TextView goToSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        eEmail = (EditText) findViewById(R.id.username);
        ePassword = (EditText) findViewById(R.id.password);
        login = (Button) findViewById(R.id.login);
        goToSignUp = (TextView)findViewById(R.id.goToSignUp);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("user");


        goToSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this , RegisterActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginAccountInstagram();
            }
        });

    }

    private void loginAccountInstagram() {
        String email = eEmail.getText().toString().trim();
        String password = ePassword.getText().toString().trim();

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

        mAuth.signInWithEmailAndPassword(email , password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            String userId = mAuth.getUid();
                            User user = new User(userId);
                            mDatabase.child(userId).setValue(user);

                            finish();
                            Intent intent = new Intent(LoginActivity.this , MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }
                    }
                });

    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mAuth.getCurrentUser()!=null){
            finish();
            startActivity(new Intent(LoginActivity.this , MainActivity.class));
        }

    }
}
