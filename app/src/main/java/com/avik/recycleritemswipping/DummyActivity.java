package com.avik.recycleritemswipping;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import org.w3c.dom.Text;

public class DummyActivity extends AppCompatActivity {

    private String text;
    private TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dummy);

        textView = findViewById(R.id.tvDetails);
        if(getIntent().getExtras()!=null){
            textView.setText("This is " + getIntent().getExtras().getString(getResources().getString(R.string.intent_data), "null"));
        }
    }
}
