package com.example.benimprojem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    //views
    EditText mEmailEt, mPasswordEt;
    TextView notHaveAccount, mRecoverPass;
    Button mLoginBtn;


    private FirebaseAuth mAuth;

    ProgressDialog pd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //actionbar and its title
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Üye Girişi");
        //enable back button
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);




        mAuth= FirebaseAuth.getInstance();

        mEmailEt =findViewById(R.id.emailEt);
        mPasswordEt =findViewById(R.id.passwordEt);
        notHaveAccount=findViewById(R.id.nothave_account);
        mRecoverPass=findViewById(R.id.recoverPass);
        mLoginBtn=findViewById(R.id.loginBtn);



        //login butonuna tıklama
        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //veri girişi
                String email =mEmailEt.getText().toString().trim();
                String passw=mPasswordEt.getText().toString().trim();
                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    mEmailEt.setError("Geçersiz Email");
                    mEmailEt.setFocusable(true);
                }else{
                    loginUser(email, passw);
                }
            }
        });

        //hesabım yok texview e tıklanma
        notHaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                finish();
            }
        });

        //recover pass a tıklandığında
        mRecoverPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRecoverPasswordDialog();
            }
        });
        // progress dialog başlar

        pd= new ProgressDialog(this);
    }

    private void showRecoverPasswordDialog() {

        //alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Şİfrenizi Sıfırlayın");
        //set layout lineer layout
        LinearLayout linearLayout = new LinearLayout(this);
        // views to set in dialog
        final EditText emailEt = new EditText(this);
        emailEt.setHint("Email");
        emailEt.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

        emailEt.setMinEms(16);

        linearLayout.addView(emailEt);
        linearLayout.setPadding(10,10,10,10);
        builder.setView(linearLayout);

        //button recover
        builder.setPositiveButton("Sıfırlayın", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //input email
                String email = emailEt.getText().toString().trim();
                beginRecovery(email);
            }
        });
        //button recover
        builder.setNegativeButton("Vazgeç", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //dismiss
                dialogInterface.dismiss();
            }
        });

        //show dialog
        builder.create().show();

    }
    private void beginRecovery(String email) {

        //show progress dialog
        pd.setMessage("Email Gönderiliyor..");
        pd.show();
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        pd.dismiss();
                        if(task.isSuccessful()){
                            Toast.makeText(LoginActivity.this, "Email gönder", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(LoginActivity.this, "Başarısız", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                //get and show proper error message
                Toast.makeText(LoginActivity.this,""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loginUser(String email, String passw) {
        //show progress dialog
        pd.setMessage("Giriş Yapılıyor...");
        pd.show();
        mAuth.signInWithEmailAndPassword(email, passw)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //dismiss progress
                            pd.dismiss();

                            // Sign in success, update UI with the signed-in user's information

                            FirebaseUser user = mAuth.getCurrentUser();

                            //kullanıcı girdikten sonra loginActivity başlar
                            startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
                            finish();

                        } else {
                            // If sign in fails, display a message to the user.
                            pd.dismiss();
                            Toast.makeText(LoginActivity.this, "Giriş Başarısız",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //hata mesajları
                pd.dismiss();
                Toast.makeText(LoginActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();  //go previous activity
        return super.onSupportNavigateUp();
    }
}
