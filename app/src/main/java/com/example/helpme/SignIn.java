package com.example.helpme;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.helpme.model.Helper;
import com.example.helpme.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;
import java.util.regex.Pattern;

//an activity for log in for existing account
public class SignIn extends AppCompatActivity {

    //define object and variable
    private SharedPreferences loginPref;
    private String type,substring,password;
    private Button loginBtn;
    private EditText loginEmail;
    private EditText loginPassword;
    private ProgressBar progressBar;
    private String resetPasswordText, emailResetText, rePasswordText;
    private String accountType;
    private ProgressBar progressBarReset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        //bind view to id
        loginEmail=findViewById(R.id.loginEmail);
        loginPassword=findViewById(R.id.loginPassword);
        loginBtn=findViewById(R.id.loginBtn);
        progressBar=findViewById(R.id.progressBarlogIn);

        TextView signUpSwitch = findViewById(R.id.signupSwitch);
        TextView forgetPassword = findViewById(R.id.forgetPassword);

        //call forgetPassWordDialog method
        forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forgetPassWordDialog();
            }
        });



        //receive data from previous activity by key
        Bundle bundle=getIntent().getExtras();
        type= Objects.requireNonNull(bundle).getString("type");


        //if user it user account else helper account
        assert type != null;
        if (type.equals("user"))
            accountType="Users";
        else
            accountType="Helpers";

        //log in button
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                password=loginPassword.getText().toString().trim();
                substring=loginEmail.getText().toString().trim();

                //validation for input data
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


        //go to sign up activity
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

    //show dialog to change user or helper password
    private void forgetPassWordDialog() {

        Dialog dialog=new Dialog(SignIn.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.forget_password_dialog);

        final EditText emailReset=dialog.findViewById(R.id.resetEmail);
        final EditText passwordReset=dialog.findViewById(R.id.resetPassword);
        final EditText rePassword=dialog.findViewById(R.id.rePassword);
        final Button resetBtn=dialog.findViewById(R.id.resetBtn);
        progressBarReset=dialog.findViewById(R.id.progressBarReset);
        progressBarReset.setVisibility(View.GONE);



        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                resetPasswordText=passwordReset.getText().toString().trim();
                rePasswordText=rePassword.getText().toString().trim();
                emailResetText=emailReset.getText().toString().trim();
                progressBarReset.setVisibility(View.VISIBLE);
                resetBtn.setClickable(false);


                if (resetValidation(emailResetText,resetPasswordText,rePasswordText)){
                    int index=emailResetText.indexOf(".");
                    String resultEmail=emailResetText.substring(0,index);
                    checkUserAccountExist(resultEmail,resetPasswordText);
                }
                else{
                    Toast.makeText(getApplicationContext(),"Please Check input info",Toast.LENGTH_LONG).show();
                    progressBarReset.setVisibility(View.GONE);
                    resetBtn.setClickable(true);
                }
            }
        });

        dialog.show();
    }

    //check if user account is already exist before overwrite old password
    private void checkUserAccountExist(final String email, final String password){

        //create database
        FirebaseDatabase database=FirebaseDatabase.getInstance();
        DatabaseReference reference=database.getReference();
        reference.child(accountType).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(email).exists()){
                    saveResetPassword(email, password);
                }
                else{
                    Toast.makeText(getApplicationContext(),"You don't have account",Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),"Error Occurred",Toast.LENGTH_SHORT).show();
            }
        });
    }

    //save reset password
    private void saveResetPassword(String email, String password) {
        FirebaseDatabase database=FirebaseDatabase.getInstance();
        DatabaseReference reference=database.getReference();
        reference.child(accountType).child(email).child("password").setValue(password).
                addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getApplicationContext(),"SuccessFull",Toast.LENGTH_SHORT).show();
                progressBarReset.setVisibility(View.GONE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),"Sorry Failed while update",Toast.LENGTH_LONG).show();
                progressBarReset.setVisibility(View.GONE);
            }
        });
    }

    //validate password pattern
    private boolean resetValidation(String email, String password, String re_password){
        boolean state=true;
        boolean emailState= Patterns.EMAIL_ADDRESS.matcher(email).matches();

        if (email.isEmpty() || !emailState){
            state=false;
        }
        if (password.isEmpty() || !isValidPassWord(password) || !password.equals(re_password))
            state=false;

        return state;
    }

    //retrieve user account in log in click and open user home
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
                        intentUser.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intentUser.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intentUser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intentUser.putExtra("EXIT",true);
                        startActivity(intentUser);

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

    //retrieve helper account in log in click and open helper home
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
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("EXIT",true);
                        startActivity(intent);

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

    //validate log in input
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

    //validate reset password
    private boolean isValidPassWord(String passwords){
        Pattern PASSWORD_PATTERN= Pattern.compile("^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#%^&+=!*])(?=\\S+$).{8,16}$");
        return PASSWORD_PATTERN.matcher(passwords).matches();
    }

}
