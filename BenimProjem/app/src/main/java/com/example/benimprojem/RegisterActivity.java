package com.example.benimprojem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.core.Tag;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    EditText mEmailEt, mPasswordEt, mPasswordEt2;
    Button mRegisterBtn;
    TextView mHaveAccount;
    //TextView tvPasswordStrength;


    //kullanıcı kaydedilirken ekranda görüntülenecek
    ProgressDialog progressDialog;

    private FirebaseAuth mAuth;
    // private Object Tag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //actionbar and its title
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Hesap Oluşturun");
        //enable back button
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        mAuth = FirebaseAuth.getInstance();

        //init
        mEmailEt = findViewById(R.id.emailEt);
        mPasswordEt = findViewById(R.id.passwordEt);
        mPasswordEt2 = findViewById(R.id.passwordEt2);
        mRegisterBtn = findViewById(R.id.registerBtn);
        mHaveAccount = findViewById(R.id.have_account);
        // tvPasswordStrength = findViewById(R.id.tvStrength);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Kaydediliyor...");

        mHaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
            }
        });

        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = mEmailEt.getText().toString().trim();
                final String password = mPasswordEt.getText().toString().trim();
                final String password2 = mPasswordEt2.getText().toString().trim();

                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    mEmailEt.setError("Geçersiz Email!!");
                    mEmailEt.setFocusable(true);
                } else if (password.length() < 6) {
                    mPasswordEt.setError("Şifre 6 karakterden küçük!!!");
                    mPasswordEt.setFocusable(true);

                } else {
                    registerUser(email, password, password2);
                }
            }
        });


    }

    private void registerUser(String email, String password, String password2)
    {
        //şifrelerin aynı olduguna bakıyor
        if (password.equals(password2))
        {
            progressDialog.show();

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                progressDialog.dismiss();

                                FirebaseUser user = mAuth.getCurrentUser();


                                //firebase kısmı
                                String email =user.getEmail();
                                String uid =user.getUid();

                                HashMap<Object, String> hashMap = new HashMap<>();
                                hashMap.put("email", email);
                                hashMap.put("uid", uid);
                                hashMap.put("name", "");  // will add later(editprofil )
                                hashMap.put("onlineStatus", "online");
                                hashMap.put("typingTo", "noOne");
                                hashMap.put("phone", ""); // will add later(editprofil )
                                hashMap.put("image", ""); // will add later(editprofil )
                                hashMap.put("cover", ""); // will add later(editprofil )

                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                DatabaseReference reference = database.getReference("Users");

                                reference.child(uid).setValue(hashMap);
                                Toast.makeText(RegisterActivity.this, "Hesap Başarılı bir şekilde oluşturuldu", Toast.LENGTH_SHORT).show();
                                user = mAuth.getCurrentUser();
                                //firebase kısmı bitti. sonradan eklendi


                                Toast.makeText(RegisterActivity.this, "Kaydedildi...\n" + user.getEmail(), Toast.LENGTH_SHORT).show();

                                startActivity(new Intent(RegisterActivity.this, DashboardActivity.class));
                                finish();


                            } else {
                                progressDialog.dismiss();

                                Toast.makeText(RegisterActivity.this, "Doğrulama hatalı!!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(RegisterActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

                }
            });

        }
        //registerUser dan sonraki ilk if in else dir. if te şifreler aynı ise işlemleri yap diyoruz
        //else de ise şifreler farklı ise ekrana aynı değil yazdırıyoruz.
        else {

            progressDialog.dismiss();
            Toast.makeText(RegisterActivity.this, "Şifreler aynı değil!!", Toast.LENGTH_SHORT).show();
        }


    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();  //go previous activity
        return super.onSupportNavigateUp();
    }
}