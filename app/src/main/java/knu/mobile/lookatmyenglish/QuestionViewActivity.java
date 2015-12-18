package knu.mobile.lookatmyenglish;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class QuestionViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_view);

        ListView listView = (ListView) findViewById(R.id.listView);
        ArrayList<String> arrName = new ArrayList<String>();
        arrName.add("안녕?");
        arrName.add("안녕하세요.");
        ArrayAdapter<String> adapName = new ArrayAdapter<String>(
                this,
                R.layout.answer_item, // android.R.layout.simple_list_item_1
                R.id.textViewTitle,
                arrName
        );
        listView.setAdapter(adapName);
    }

/*    //스크롤뷰 안에서 리스트뷰 사용!! 인데 안됩니다!!!ㅠㅠ
    private ListView listView;
    private ScrollView scrollView;

    private void iNit(){
        scrollView = (ScrollView)findViewById(R.id.scrollView);
        listView = (ListView)findViewById(R.id.listView);

        listView.setOnTouchListener(new onTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event){
                scrollView.requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
    }
*/

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageButtonBack: //←버튼
                finish();
                break;

            case R.id.buttonAnswer: //←버튼
                Intent i = new Intent(this, AnswerActivity.class);
                if(!SignInActivity.isLoggedIn)
                    i = new Intent(this, SignInActivity.class);
                startActivity(i);
                break;
        }
    }
}
