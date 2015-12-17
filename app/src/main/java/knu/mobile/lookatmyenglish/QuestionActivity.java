package knu.mobile.lookatmyenglish;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class QuestionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);
    }

    public void askQuestion(View v){
        if(SignInActivity.memberIdx == -1){
            Toast toast = Toast.makeText(this, "먼저 로그인 해주세요.", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
        String titleText = ((EditText)findViewById(R.id.editTextTitle)).getText().toString();
        String contentText = ((EditText)findViewById(R.id.editTextContent)).getText().toString();
        DBContentProvider dbContentProvider = new DBContentProvider();
        dbContentProvider.insert(titleText, contentText.toString(), SignInActivity.memberIdx);
        Toast toast = Toast.makeText(this, "질문을 등록했습니다.", Toast.LENGTH_SHORT);
        toast.show();
        finish();
    }

    public void cancelActivity(View v){
        finish();
    }
}
