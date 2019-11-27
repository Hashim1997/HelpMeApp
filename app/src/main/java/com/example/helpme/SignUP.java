package com.example.helpme;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.helpme.model.Helper;
import com.example.helpme.model.User;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;
import java.util.regex.Pattern;

public class SignUP extends AppCompatActivity {

    private EditText fullName, email, passWord, carType, carColor, carModel, phoneNumber;
    private String fullNameText, emailText, passWordText, carTypeText, carColorText, carModelText, phoneNumberText, type;
    private String levelOfExperienceText, typeOfExperienceText, locationText;
    private SharedPreferences loginPref;
    private int indexString;
    private Button signUp;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        signUp=findViewById(R.id.signUpBtn);
        fullName=findViewById(R.id.FullName);
        email=findViewById(R.id.signUpEmail);
        passWord=findViewById(R.id.signUpPassword);
        carType=findViewById(R.id.carType);
        carModel=findViewById(R.id.carModel);
        carColor=findViewById(R.id.carColor);
        phoneNumber=findViewById(R.id.phoneNumber);
        progressBar=findViewById(R.id.progressBarSignUP);

        Bundle bundle=getIntent().getExtras();
        type= Objects.requireNonNull(bundle).getString("type");


        assert type != null;
        if (type.equals("helper")){

            carType.setHint(R.string.experience_type);
            carColor.setHint(R.string.experience_level);
            carModel.setHint(R.string.location);
        }


        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (type.equals("user")){
                    fullNameText=fullName.getText().toString().trim();
                    emailText=email.getText().toString().trim();
                    passWordText=passWord.getText().toString().trim();
                    carTypeText=carType.getText().toString().trim();
                    carColorText=carColor.getText().toString().trim();
                    carModelText=carModel.getText().toString().trim();
                    phoneNumberText=phoneNumber.getText().toString().trim();

                    if (userValidation()){
                        signUp.setVisibility(View.GONE);
                        progressBar.setVisibility(View.VISIBLE);
                        indexString=emailText.indexOf(".");
                        final User user=new User();
                        user.setFullName(fullNameText);
                        user.setEmail(emailText.substring(0,indexString));
                        user.setPassword(passWordText);
                        user.setPhoneNum(phoneNumberText);
                        user.setCarColor(carColorText);
                        user.setCarType(carTypeText);
                        user.setCarModel(carModelText);
                        checkUserAccountExist(user.getEmail(),user);
                    }
                    else
                        Toast.makeText(getApplicationContext(),"Please input full data",Toast.LENGTH_LONG).show();
                }

                else if (type.equals("helper")){
                    fullNameText=fullName.getText().toString().trim();
                    emailText=email.getText().toString().trim();
                    passWordText=passWord.getText().toString().trim();
                    typeOfExperienceText=carType.getText().toString().trim();
                    levelOfExperienceText=carColor.getText().toString().trim();
                    locationText=carModel.getText().toString().trim();
                    phoneNumberText=phoneNumber.getText().toString().trim();

                    if (helperValidation()){
                        signUp.setVisibility(View.GONE);
                        progressBar.setVisibility(View.VISIBLE);
                        indexString=emailText.indexOf(".");
                        Helper helper=new Helper();
                        helper.setFullName(fullNameText);
                        helper.setEmail(emailText.substring(0,indexString));
                        helper.setPassword(passWordText);
                        helper.setPhoneNum(phoneNumberText);
                        helper.setTypeOfExperience(typeOfExperienceText);
                        helper.setLevelOfExperience(levelOfExperienceText);
                        helper.setLocation(locationText);
                        helper.setRate(0);
                        checkHelperAccountExist(helper.getEmail(),helper);
                    }
                    else
                        Toast.makeText(getApplicationContext(),"Please input full data",Toast.LENGTH_LONG).show();
                }

            }
        });
    }





    private boolean userValidation(){
        boolean state=true;
        boolean emailState= Patterns.EMAIL_ADDRESS.matcher(emailText).matches();
        boolean passWordState=isValidPassWord(passWordText);

        if (fullNameText.isEmpty()){
            fullName.setError("Please input name");
            state=false;
        }
        if (emailText.isEmpty() || !emailState){
            email.setError("Please input correct email");
            state=false;
        }
        if (passWordText.isEmpty() || !passWordState){
            passWord.setError("Please input correct password");
            Toast.makeText(getApplicationContext(),"Password must contain letter and character & at least 8 digits", Toast.LENGTH_LONG).show();
            state=false;
        }
        if (phoneNumberText.length()!=10){
            phoneNumber.setError("Please input phone num");
            state=false;
        }
        if (carTypeText.isEmpty()){
            carType.setError("Please input data");
            state=false;
        }
        if (carColorText.isEmpty()){
            carColor.setError("Please input data");
            state=false;
        }
        if (carModelText.isEmpty()){
            carModel.setError("Please input data");
            state=false;
        }
        return state;
    }

    private boolean helperValidation(){

        boolean state=true;
        boolean emailState= Patterns.EMAIL_ADDRESS.matcher(emailText).matches();
        boolean passWordState=isValidPassWord(passWordText);

        if (fullNameText.isEmpty()){
            fullName.setError("Please input name");
            state=false;
        }
        if (emailText.isEmpty() || !emailState){
            email.setError("Please input correct email");
            state=false;
        }
        if (passWordText.isEmpty() || !passWordState){
            passWord.setError("Please input correct password");
            Toast.makeText(getApplicationContext(),"Password must contain letter and character & at least 8 digits", Toast.LENGTH_LONG).show();
            state=false;
        }
        if (phoneNumberText.length()!=10){
            phoneNumber.setError("Please input phone num");
            state=false;
        }
        if (typeOfExperienceText.isEmpty()){
            carType.setError("Please input data");
            state=false;
        }
        if (levelOfExperienceText.isEmpty()){
            carColor.setError("Please input data");
            state=false;
        }
        if (locationText.isEmpty()){
            carModel.setError("Please input data");
            state=false;
        }
        return state;
    }

    private boolean isValidPassWord(String passwords){
        Pattern PASSWORD_PATTERN= Pattern.compile("^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#%^&+=!*])(?=\\S+$).{8,16}$");
        return PASSWORD_PATTERN.matcher(passwords).matches();
    }


    private void saveUserData(final User user) {

        FirebaseDatabase database=FirebaseDatabase.getInstance();
        DatabaseReference userRef=database.getReference("Users");

        loginPref=getSharedPreferences("login", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor=loginPref.edit();

        userRef.child(user.getEmail()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(getApplicationContext(),"SignUP Successfully",Toast.LENGTH_LONG).show();
                editor.putString("email",user.getEmail());
                editor.putString("password",user.getPassword());
                editor.putString("type","Users");
                editor.apply();
                signUp.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);

                Intent intentUser=new Intent(SignUP.this,UserHome.class);
                intentUser.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intentUser.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intentUser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intentUser.putExtra("EXIT",true);
                startActivity(intentUser);

            }
        }).addOnCanceledListener(new OnCanceledListener() {
            @Override
            public void onCanceled() {
                Toast.makeText(getApplicationContext(),"Sorry Failed while Sign Up",Toast.LENGTH_LONG).show();
                signUp.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void saveHelperData(final Helper helper){

        FirebaseDatabase database=FirebaseDatabase.getInstance();
        DatabaseReference userRef=database.getReference("Helpers");

        loginPref=getSharedPreferences("login", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor=loginPref.edit();

        userRef.child(helper.getEmail()).setValue(helper).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(getApplicationContext(),"SignUP Successfully",Toast.LENGTH_LONG).show();
                editor.putString("email",helper.getEmail());
                editor.putString("password",helper.getPassword());
                editor.putString("type","Helpers");
                editor.apply();
                signUp.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);

                Intent intent=new Intent(SignUP.this,HelperHome.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("EXIT",true);
                startActivity(intent);

            }
        }).addOnCanceledListener(new OnCanceledListener() {
            @Override
            public void onCanceled() {
                Toast.makeText(getApplicationContext(),"Sorry Failed while Sign Up",Toast.LENGTH_LONG).show();
                signUp.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void checkUserAccountExist(final String email,final User user){

        FirebaseDatabase database=FirebaseDatabase.getInstance();
        DatabaseReference reference=database.getReference();
        reference.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.child(email).exists()){
                    saveUserData(user);
                }
                else{
                    Toast.makeText(getApplicationContext(),"You already Have account",Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    signUp.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),"Error Occurred",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkHelperAccountExist(final String email, final Helper helper){
        FirebaseDatabase database=FirebaseDatabase.getInstance();
        DatabaseReference reference=database.getReference();
        reference.child("Helpers").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.child(email).exists()){
                    saveHelperData(helper);
                }
                else{
                    Toast.makeText(getApplicationContext(),"You already Have account",Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    signUp.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),"Error Occurred",Toast.LENGTH_SHORT).show();
            }
        });
    }


}
