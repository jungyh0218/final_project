package knu.mobile.lookatmyenglish;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClick(View v){
        Intent intent;
        switch(v.getId()){
            case R.id.button:
                intent = new Intent(MainActivity.this, SignInActivity.class);
                startActivity(intent);
                break;
            case R.id.button2:
                intent = new Intent(MainActivity.this, QuestionActivity.class);
                startActivity(intent);
                break;
        }
    }
}
