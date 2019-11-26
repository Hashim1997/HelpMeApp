package com.example.helpme;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.text.HtmlCompat;

public class AccountChooseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_choose);

        TextView quoteText = findViewById(R.id.qouteText);
        Button userSelect = findViewById(R.id.userBtn);
        Button helperSelect = findViewById(R.id.helperBtn);

        String car = "<font color=\'#000000\'>CAR</font>";
        String hands = "<font color=\'#000000\'>HANDS</font>";

        String wholeTitle = "YOUR " + car + " IN THE RIGHT " + hands;
        quoteText.setText(HtmlCompat.fromHtml(wholeTitle,HtmlCompat.FROM_HTML_MODE_LEGACY));

        userSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent userIntent=new Intent(AccountChooseActivity.this,SignIn.class);
                userIntent.putExtra("type","user");
                userIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(userIntent);
            }
        });

        helperSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent helperIntent =new Intent(AccountChooseActivity.this,SignIn.class);
                helperIntent.putExtra("type","helper");
                helperIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(helperIntent);
            }
        });
    }
}
