package com.example.helpme;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        Button englishBtn=findViewById(R.id.englishBtn);
        Button arabicBtn=findViewById(R.id.arabicBtn);
        final TextView howItWork = findViewById(R.id.text_how_work);
        final TextView aboutApp=findViewById(R.id.about_text);


        Bundle bundle=getIntent().getExtras();
        final String type= Objects.requireNonNull(bundle).getString("type");
        assert type != null;
        if (type.equals("user")){
            howItWork.setText(R.string.how_to_use_user);
        }

        englishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aboutApp.setText(R.string.about_app);
                if (type.equals("user"))
                    howItWork.setText(R.string.how_to_use_user);
                else
                    howItWork.setText(R.string.how_to_use_helper);
            }
        });

        arabicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aboutApp.setText(R.string.about_app_arabic);
                if (type.equals("user"))
                    howItWork.setText(R.string.how_to_use_user_arabic);
                else
                    howItWork.setText(R.string.how_to_use_helper_arabic);
            }
        });
    }
}
