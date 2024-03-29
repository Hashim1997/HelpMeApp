package com.example.helpme;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

//an activity to edit user or helper profile data
public class Profile extends AppCompatActivity {

    //define view and variable
    private EditText fullName,emailX,passWord,carTypeOfExperience,carColorExpLevel,carModelOrLocation,phoneNumber;
    private String fullNameText,emailText,passWordText,carTypeOfExperienceText,carColorExpLevelText,carModelOrLocationText,phoneNumberText,savedType;
    private SharedPreferences profilePref;
    private ProgressBar progressProfile;
    private Button editProfileBtn;
    private ImageView backImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //bind view to id
        editProfileBtn=findViewById(R.id.editProfileBtn);
        fullName=findViewById(R.id.FullNameProfile);
        emailX=findViewById(R.id.profileEmail);
        passWord=findViewById(R.id.profilePassword);
        carTypeOfExperience=findViewById(R.id.profileCarType);
        carModelOrLocation=findViewById(R.id.profileCarModel);
        carColorExpLevel=findViewById(R.id.profileCarColor);
        phoneNumber=findViewById(R.id.profilePhoneNumber);
        progressProfile=findViewById(R.id.progressProfile);
        backImage=findViewById(R.id.imageBackBtnProfile);

        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        //retrieve email and password
        profilePref=getSharedPreferences("login",MODE_PRIVATE);
        String savedEmail=profilePref.getString("email","empty");
        String savedPassword=profilePref.getString("password","empty");
        savedType=profilePref.getString("type","empty");

        Toast.makeText(getApplicationContext(),savedEmail+" "+savedPassword+" "+savedType,Toast.LENGTH_LONG).show();
        //if user retrieve ir else retrieve helper data
        if (savedType.equals("Users")){
            retrieveUserInfo(savedEmail,savedPassword);
        }
        else if (savedType.equals("Helpers")){
            retrieveHelperInfo(savedEmail,savedPassword);
        }

        //in button edit change user or helper data
        editProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if button edit make all text clickable
                if (editProfileBtn.getText().equals("EDIT")){
                    editProfileBtn.setText("SAVE");
                    fullName.setEnabled(true);
                    passWord.setEnabled(true);
                    carColorExpLevel.setEnabled(true);
                    carModelOrLocation.setEnabled(true);
                    carTypeOfExperience.setEnabled(true);
                    phoneNumber.setEnabled(true);
                }
                //if save and validate save all info to firebase
                else if (editProfileBtn.getText().equals("SAVE")){
                    progressProfile.setVisibility(View.VISIBLE);
                    editProfileBtn.setVisibility(View.GONE);
                    fullNameText=fullName.getText().toString().trim();
                    emailText=emailX.getText().toString().trim();
                    passWordText=passWord.getText().toString().trim();
                    carTypeOfExperienceText=carTypeOfExperience.getText().toString().trim();
                    carColorExpLevelText=carColorExpLevel.getText().toString().trim();
                    carModelOrLocationText=carModelOrLocation.getText().toString().trim();
                    phoneNumberText=phoneNumber.getText().toString().trim();

                    if (savedType.equals("Users") && validation()){
                        editProfileUserData(fullNameText,emailText,passWordText,phoneNumberText,carColorExpLevelText,carModelOrLocationText,carTypeOfExperienceText);
                    }
                    else if (savedType.equals("Helpers") && validation()){
                        editProfileHelperData(fullNameText,emailText,passWordText,phoneNumberText,carColorExpLevelText,carModelOrLocationText,carTypeOfExperienceText);
                    }
                    else{
                        Toast.makeText(getApplicationContext(),"Please Input Correct Data",Toast.LENGTH_LONG).show();
                        progressProfile.setVisibility(View.GONE);
                        editProfileBtn.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }

    //method for user database section to retrieve data
    private void retrieveUserInfo(final String email, final String password){
        FirebaseDatabase database=FirebaseDatabase.getInstance();
        DatabaseReference reference=database.getReference();

        reference.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(email).exists()){
                    User user=dataSnapshot.child(email).getValue(User.class);
                    if (Objects.requireNonNull(user).getPassword().equals(password)){
                        fullName.setText(user.getFullName());
                        emailX.setText(user.getEmail());
                        passWord.setText(user.getPassword());
                        carTypeOfExperience.setText(user.getCarType());
                        carColorExpLevel.setText(user.getCarColor());
                        carModelOrLocation.setText(user.getCarModel());
                        phoneNumber.setText(user.getPhoneNum());
                    }
                    else
                        Toast.makeText(getApplicationContext(),"The password is inCorrect",Toast.LENGTH_LONG).show();
                }
                else
                    Toast.makeText(getApplicationContext(),"The email is inCorrect",Toast.LENGTH_LONG).show();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),"Sorry Error While Loading",Toast.LENGTH_LONG).show();
            }
        });
    }

    //validate user input else show error
    private boolean validation(){
        boolean state=true;
        boolean passWordState=isValidPassWord(passWordText);

        if (fullNameText.isEmpty()){
            fullName.setError("Please input name");
            state=false;
        }
        //if password not empty and consist of symbol capital and small letter and number and 8 digits
        if (passWordText.isEmpty() || !passWordState){
            passWord.setError("Please input correct password");
            Toast.makeText(getApplicationContext(),"Password must contain letter and character & at least 8 digits", Toast.LENGTH_LONG).show();
            state=false;
        }
        if (phoneNumberText.length()!=10){
            phoneNumber.setError("Please input phone num");
            state=false;
        }
        if (carTypeOfExperienceText.isEmpty()){
            carTypeOfExperience.setError("Please input data");
            state=false;
        }
        if (carColorExpLevelText.isEmpty()){
            carColorExpLevel.setError("Please input data");
            state=false;
        }
        if (carModelOrLocationText.isEmpty()){
            carModelOrLocation.setError("Please input data");
            state=false;
        }
        return state;
    }

    //if password not empty and consist of symbol capital and small letter and number and 8 digits
    private boolean isValidPassWord(String passwords){
        Pattern PASSWORD_PATTERN= Pattern.compile("^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#%^&+=!*])(?=\\S+$).{8,16}$");
        return PASSWORD_PATTERN.matcher(passwords).matches();
    }

    //retrieve helper data from firebase helper section (document or root)
    private void retrieveHelperInfo(final String email, final String password){
        FirebaseDatabase database=FirebaseDatabase.getInstance();
        DatabaseReference reference=database.getReference();

        reference.child("Helpers").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(email).exists()){
                    Helper helper=dataSnapshot.child(email).getValue(Helper.class);
                    if (Objects.requireNonNull(helper).getPassword().equals(password)){
                        fullName.setText(helper.getFullName());
                        emailX.setText(helper.getEmail());
                        passWord.setText(helper.getPassword());
                        carTypeOfExperience.setText(helper.getTypeOfExperience());
                        carColorExpLevel.setText(helper.getLevelOfExperience());
                        carModelOrLocation.setText(helper.getLocation());
                        phoneNumber.setText(helper.getPhoneNum());
                    }
                    else
                        Toast.makeText(getApplicationContext(),"The password is inCorrect",Toast.LENGTH_LONG).show();
                }
                else
                    Toast.makeText(getApplicationContext(),"The email is inCorrect",Toast.LENGTH_LONG).show();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),"Sorry Error While Loading",Toast.LENGTH_LONG).show();
            }
        });
    }

    //save user new data in place of old data method
    private void editProfileUserData(String fullName, String email, String password, String phoneNumber, String carColor, String carModel, String carType){

        //user object
        final User user=new User();
        user.setFullName(fullName);
        user.setEmail(email);
        user.setPhoneNum(phoneNumber);
        user.setPassword(password);
        user.setCarColor(carColor);
        user.setCarModel(carModel);
        user.setCarType(carType);

        //edit in offline storage resource by key
        final SharedPreferences.Editor editor=profilePref.edit();

        FirebaseDatabase userDatabase=FirebaseDatabase.getInstance();
        DatabaseReference reference=userDatabase.getReference("Users");
        reference.child(user.getEmail()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(getApplicationContext(),"Edited Successfully",Toast.LENGTH_LONG).show();
                editor.putString("password",user.getPassword());
                editor.apply();
                progressProfile.setVisibility(View.GONE);
                editProfileBtn.setVisibility(View.VISIBLE);
            }
        }).addOnCanceledListener(new OnCanceledListener() {
            @Override
            public void onCanceled() {
                Toast.makeText(getApplicationContext(),"Error While Saving",Toast.LENGTH_LONG).show();
            }
        });
    }

    //save helper new data in place of old data method
    private void editProfileHelperData(String fullName, String email, String password, String phoneNumber, String experienceLevel, String location, String typeOfExperience){

        final Helper helper=new Helper();
        helper.setFullName(fullName);
        helper.setEmail(email);
        helper.setPassword(password);
        helper.setPhoneNum(phoneNumber);
        helper.setLevelOfExperience(experienceLevel);
        helper.setLocation(location);
        helper.setTypeOfExperience(typeOfExperience);

        //edit in offline storage resource
        final SharedPreferences.Editor editor=profilePref.edit();

        FirebaseDatabase userDatabase=FirebaseDatabase.getInstance();
        DatabaseReference reference=userDatabase.getReference("Helpers");
        reference.child(helper.getEmail()).setValue(helper).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(getApplicationContext(),"Edited Successfully",Toast.LENGTH_LONG).show();
                editor.putString("password",helper.getPassword());
                editor.apply();
                progressProfile.setVisibility(View.GONE);
                editProfileBtn.setVisibility(View.VISIBLE);
            }
        }).addOnCanceledListener(new OnCanceledListener() {
            @Override
            public void onCanceled() {
                Toast.makeText(getApplicationContext(),"Error While Saving",Toast.LENGTH_LONG).show();
            }
        });
    }
}
