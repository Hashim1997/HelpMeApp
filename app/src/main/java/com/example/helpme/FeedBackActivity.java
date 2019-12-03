package com.example.helpme;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class FeedBackActivity extends AppCompatActivity {

    private String feedBackText;
    private String userID;
    private ProgressBar progressFeed;
    private  Button submitFeed;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_back);

        final EditText feedBack=findViewById(R.id.feedBackText);
        submitFeed=findViewById(R.id.submit_feed);
        progressFeed=findViewById(R.id.progressFeed);

        SharedPreferences preferences=getSharedPreferences("login",MODE_PRIVATE);
        userID=preferences.getString("email","empty");

        submitFeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                feedBackText=feedBack.getText().toString().trim();
                if (feedBackText.isEmpty())
                    Toast.makeText(getApplicationContext(),"This cannot be empty",Toast.LENGTH_LONG).show();

                else
                    submitFeedBack(feedBackText);
            }
        });
    }

    private void submitFeedBack(String feedBackT) {

        progressFeed.setVisibility(View.VISIBLE);
        submitFeed.setVisibility(View.GONE);

        Bundle bundle=getIntent().getExtras();
        String type= Objects.requireNonNull(bundle).getString("type");

        String format="dd-MM-yyyy";
        Date c= Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat=new SimpleDateFormat(format, Locale.US);
        String feedDate=dateFormat.format(c);

        FirebaseDatabase databaseFeed=FirebaseDatabase.getInstance();
        DatabaseReference referenceFeed=databaseFeed.getReference("FeedBack");
        assert type != null;
        referenceFeed.child(type).child(userID).child(feedDate).setValue(feedBackT).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getApplicationContext(),"Thank you for your feedback",Toast.LENGTH_SHORT).show();
                progressFeed.setVisibility(View.GONE);
                submitFeed.setVisibility(View.VISIBLE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_LONG).show();
                progressFeed.setVisibility(View.GONE);
                progressFeed.setVisibility(View.VISIBLE);
            }
        });
    }
}
