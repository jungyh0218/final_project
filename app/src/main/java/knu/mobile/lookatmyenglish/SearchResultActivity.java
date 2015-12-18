package knu.mobile.lookatmyenglish;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class SearchResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonHome: //홈
                Intent i = new Intent(this, MainActivity.class);
                startActivity(i);
                break;

            case R.id.buttonQuestion: //질문
                i = new Intent(this, QuestionActivity.class);
                if(!SignInActivity.isLoggedIn)
                    i = new Intent(this, SignInActivity.class);
                startActivity(i);
                break;
        }
    }
}
