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

    EditText mEmailEt, mPasswordEt,mPasswordEt2;
    Button mRegisterBtn;
    TextView mHaveAccount;
    TextView tvPasswordStrength;


    //kullanıcı kaydedilirken ekranda görüntülenecek
    ProgressDialog progressDialog;

    private FirebaseAuth mAuth;
    private Object Tag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //actionbar and its title
        ActionBar actionBar=getSupportActionBar();
        actionBar.setTitle("Hesap Oluşturun");
        //enable back button
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        //init
        mEmailEt =findViewById(R.id.emailEt);
        mPasswordEt = findViewById(R.id.passwordEt);
        mPasswordEt2 = findViewById(R.id.passwordEt2);
        mRegisterBtn = findViewById(R.id.registerBtn);
        mHaveAccount = findViewById(R.id.have_account);
        tvPasswordStrength = findViewById(R.id.tvStrength);

        mAuth =FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Kaydediliyor...");


//degişen kısım basladı





        mPasswordEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            @Override
            public void afterTextChanged(Editable s) {
                calculateStrengthPassword(s.toString());
            }
        });
        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = mEmailEt.getText().toString().trim();
                final String password = mPasswordEt.getText().toString().trim();
                final String password2 = mPasswordEt2.getText().toString().trim();
                signup_Email(email,password,password2);
            }
        });
        mHaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
            }
        });
    }
    private void signup_Email(String email, String password, String password2) {

        if (password.equals(password2)){
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                progressDialog.dismiss();
                                FirebaseUser user = mAuth.getCurrentUser();

                                String email =user.getEmail();
                                String uid =user.getUid();
                                //kullanıcı bilgileri kaydedildiğinde realtime database den bilgiler gelir
                                //HashMap kullanılır

                                HashMap<Object, String> hashMap = new HashMap<>();
                                //put info in hashmap
                                hashMap.put("email", email);
                                hashMap.put("uid", uid);
                                hashMap.put("name", "");  // will add later(editprofil )
                                hashMap.put("onlineStatus", "online");
                                hashMap.put("typingTo", "noOne");
                                hashMap.put("phone", ""); // will add later(editprofil )
                                hashMap.put("image", ""); // will add later(editprofil )
                                hashMap.put("cover", ""); // will add later(editprofil )

                                //firebase database instance
                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                //path to store user data named  "Users"
                                DatabaseReference reference = database.getReference("Users");
                                //put data within hashmap in database
                                reference.child(uid).setValue(hashMap);
                                // Sign in success, update UI with the signed-in user's information
                                Toast.makeText(RegisterActivity.this, "Hesap Başarılı bir şekilde oluşturuldu", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(RegisterActivity.this, DashboardActivity.class));
                                finish();

                                user = mAuth.getCurrentUser();
                            } else {
                                // If sign in fails, display a message to the user.
                                //Log.w(TAG, "hesap oluşturulamadı", task.getException());
                                progressDialog.dismiss();
                                Log.w((String) Tag,"hesap oluşturulamadı!",task.getException());
                                Toast.makeText(RegisterActivity.this, "Doğrulama hatalı!!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }    }
    private void calculateStrengthPassword(String passwordText) {
        int buyukHarf = 0, kucukHarf = 0, sayilar = 0,
                ozelKarakterler = 0, digerKarakterler = 0, gucSeviyeNotu = 0;
        char c;

        int passwordLength = passwordText.length();

        if (passwordLength == 0) {
            tvPasswordStrength.setText("Invalid Password");
            tvPasswordStrength.setBackgroundColor(Color.GRAY);
            return;
        }

        //Parola 5 ten küçükse gucSeviyeNotu =1
        if (passwordLength <= 5) {
            gucSeviyeNotu = 1;
        }
        //Eğer parola uzunluğu  5'ten büyük 10 eşitse gucSeviyeNotu=2
        else if (passwordLength <= 10) {
            gucSeviyeNotu = 2;
        }
        //Eğer parola uzunluğu is >10 set gucSeviyeNotu=3
        else
            gucSeviyeNotu = 3;
        // Şifrenin karakterleri arasında dolaşıyoruz
        for (int i = 0; i < passwordLength; i++) {
            c = passwordText.charAt(i);
            // eğer parola küçük harf içeriyorsa
            // o zaman gucSeviyeNotu 1 arttırlır
            if (c >= 'a' && c <= 'z') {
                if (kucukHarf == 0) gucSeviyeNotu++;
                kucukHarf = 1;
            }
            // eğer parola büyük harf içeriyorsa
            // o zaman gucSeviyeNotu 1 arttırılır
            else if (c >= 'A' && c <= 'Z') {
                if (buyukHarf == 0) gucSeviyeNotu++;
                buyukHarf = 1;
            }
            // parola sayilar içeryorsa
            // o zaman gucSeviyeNotu 1 arttır
            else if (c >= '0' && c <= '9') {
                if (sayilar == 0) gucSeviyeNotu++;
                sayilar = 1;
            }
            //  parola  _ or @ içeryorsa
            // gucSeviyeNotu'bu 1 arttır
            else if (c == '_' || c == '@') {
                if (ozelKarakterler == 0) gucSeviyeNotu += 1;
                ozelKarakterler = 1;
            }
            // Parola özel karakterler içeriyorsa
            // gucSeviyeNotu'nu 1 arttırıyoruz.
            else {
                if (digerKarakterler == 0) gucSeviyeNotu += 2;
                digerKarakterler = 1;
            }
        }

        if (gucSeviyeNotu <= 3) {
            tvPasswordStrength.setText("Parola Gücü : DÜŞÜK");
            tvPasswordStrength.setBackgroundColor(Color.RED);
        } else if (gucSeviyeNotu <= 6) {
            tvPasswordStrength.setText("Parola Gücü : ORTA");
            tvPasswordStrength.setBackgroundColor(Color.YELLOW);
        } else if (gucSeviyeNotu <= 9) {
            tvPasswordStrength.setText("Parola Gücü : YÜKSEK");
            tvPasswordStrength.setBackgroundColor(Color.GREEN);
        }
    }




    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();  //go previous activity
        return super.onSupportNavigateUp();
    }
}
