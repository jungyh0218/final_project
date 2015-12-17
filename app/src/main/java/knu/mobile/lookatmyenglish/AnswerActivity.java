package knu.mobile.lookatmyenglish;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class AnswerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageButtonClose: //X버튼
                finish();
                break;

            case R.id.imageButtonCheck: //V버튼
                finish();
                break;
        }
    }
}
