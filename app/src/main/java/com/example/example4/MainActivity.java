package com.example.example4;

import android.content.Intent;
import android.support.annotation.Nullable;

import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;



public class MainActivity extends Activity {

    private Button start;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        start=(Button) findViewById(R.id.start_button);

        start.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent =new Intent(MainActivity.this, fragment.class);


                startActivity(intent);
            }
        });

    }
}