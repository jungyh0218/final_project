package knu.mobile.lookatmyenglish;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView listView = (ListView) findViewById(R.id.listView);
        ArrayList<String> arrName = new ArrayList<String>();
        arrName.add("android");
        arrName.add("Apple");
        arrName.add("Microsoft");
        ArrayAdapter<String> adapName = new ArrayAdapter<String>(
                this,
                R.layout.item, // android.R.layout.simple_list_item_1
                R.id.textViewTitle,
                arrName
        );
        listView.setAdapter(adapName);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, QuestionViewActivity.class);
                startActivity(intent);
            }
        });
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonSearch: //검색
                Intent i = new Intent(this, SearchResultActivity.class);
                startActivity(i);
                break;

            case R.id.buttonQuestion: //질문
                i = new Intent(this, QuestionActivity.class);
                if(!SignInActivity.isLoggedIn)
                    i = new Intent(this, SignInActivity.class);
                startActivity(i);
                break;

            case R.id.buttonProfile: //프로필
                i = new Intent(this, QuestionViewActivity.class);
                if(!SignInActivity.isLoggedIn)
                    i = new Intent(this, SignInActivity.class);
                startActivity(i);
                break;
        }
    }
}
