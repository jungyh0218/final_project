package knu.mobile.lookatmyenglish;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class QuestionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);
    }

    public void askQuestion(View v){
        String titleText = ((EditText)findViewById(R.id.editText_title)).getText().toString();
        String contentText = ((EditText)findViewById(R.id.editText_content)).getText().toString();
        DBContentProvider dbContentProvider = new DBContentProvider();
        dbContentProvider.insert(titleText, contentText.toString(), SignInActivity.memberIdx);
    }
}
