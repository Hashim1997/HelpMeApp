package com.example.helpme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.example.helpme.model.Helper;
import com.example.helpme.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.Objects;

public class SignIn extends AppCompatActivity {

    private SharedPreferences loginPref;
    private String type,substring,password;
    TextView signUpSwitch,forgetPassword;
    Button loginBtn;
    EditText loginEmail,loginPassword;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        signUpSwitch=findViewById(R.id.signupSwitch);
        loginEmail=findViewById(R.id.loginEmail);
        loginPassword=findViewById(R.id.loginPassword);
        loginBtn=findViewById(R.id.loginBtn);
        progressBar=findViewById(R.id.progressBarlogIn);
        forgetPassword=findViewById(R.id.forgetPassword);

        forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SignIn.this,Profile.class);
                startActivity(intent);
            }
        });



        Bundle bundle=getIntent().getExtras();
        type= Objects.requireNonNull(bundle).getString("type");

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                password=loginPassword.getText().toString().trim();
                substring=loginEmail.getText().toString().trim();


                if (validation()){
                    progressBar.setVisibility(View.VISIBLE);
                    loginBtn.setVisibility(View.GONE);
                    int index=substring.indexOf(".");
                    String result=substring.substring(0,index);
                    if (type.equals("user")){
                        retrieveUserAccount(result,password);
                    }
                    else if (type.equals("helper")){
                        retrieveHelperAccount(result,password);
                    }
                }
                else
                    Toast.makeText(getApplicationContext(),"Please Input Correct Data",Toast.LENGTH_LONG).show();
            }
        });


        signUpSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signUpIntent=new Intent(SignIn.this,SignUP.class);
                signUpIntent.putExtra("type",type);
                signUpIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(signUpIntent);
            }
        });
    }

    private void retrieveUserAccount(final String email, final String password){
        FirebaseDatabase database=FirebaseDatabase.getInstance();
        DatabaseReference reference=database.getReference();

        loginPref=getSharedPreferences("login", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor=loginPref.edit();

        reference.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(email).exists()){
                    User user=dataSnapshot.child(email).getValue(User.class);
                    if (Objects.requireNonNull(user).getPassword().equals(password)){
                        Toast.makeText(getApplicationContext(),"Successful Login",Toast.LENGTH_SHORT).show();
                        editor.putString("email",email);
                        editor.putString("password",password);
                        editor.putString("type","Users");
                        editor.apply();
                        Intent intentUser=new Intent(SignIn.this,UserHome.class);
                        startActivity(intentUser);
                        SignIn.this.finish();
                    }
                    else
                        Toast.makeText(getApplicationContext(),"The password is inCorrect",Toast.LENGTH_LONG).show();
                }
                else
                    Toast.makeText(getApplicationContext(),"The email is inCorrect",Toast.LENGTH_LONG).show();

                progressBar.setVisibility(View.GONE);
                loginBtn.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),"Sorry Error While Loading",Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
                loginBtn.setVisibility(View.VISIBLE);
            }
        });
    }

    private void retrieveHelperAccount(final String email,final String password){
        FirebaseDatabase database=FirebaseDatabase.getInstance();
        DatabaseReference reference=database.getReference();

        loginPref=getSharedPreferences("login", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor=loginPref.edit();

        reference.child("Helpers").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(email).exists()){
                    Helper helper=dataSnapshot.child(email).getValue(Helper.class);
                    if (Objects.requireNonNull(helper).getPassword().equals(password)){
                        Toast.makeText(getApplicationContext(),"Successful Login",Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                        loginBtn.setVisibility(View.VISIBLE);
                        editor.putString("email",email);
                        editor.putString("password",password);
                        editor.putString("type","Helpers");
                        editor.apply();
                        Intent intent=new Intent(SignIn.this,HelperHome.class);
                        startActivity(intent);
                        SignIn.this.finish();
                    }
                    else
                        Toast.makeText(getApplicationContext(),"The password is inCorrect",Toast.LENGTH_LONG).show();
                }
                else
                    Toast.makeText(getApplicationContext(),"The email is inCorrect",Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
                loginBtn.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private boolean validation(){
        boolean state=true;

        boolean emailState= Patterns.EMAIL_ADDRESS.matcher(substring).matches();

        if (substring.isEmpty() || !emailState){
            loginEmail.setError("Please input email");
            state=false;
        }
        if (password.isEmpty()){
            loginPassword.setError("Please input password");
            state=false;
        }

        return state;
    }

}
