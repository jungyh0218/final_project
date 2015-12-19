package knu.mobile.lookatmyenglish;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class SearchResultActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        final EditText keywordText = (EditText)findViewById(R.id.editTextSearch);
        ImageButton searchButton = (ImageButton)findViewById(R.id.imageButtonSearch);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String keyword = keywordText.getText().toString();
                DBContentProvider dbContentProvider = new DBContentProvider(SearchResultActivity.this);
                //ArrayList<QuestionContent> result
                        //= (ArrayList<QuestionContent>)dbContentProvider.searchQuestion(keyword);
               searchQuestion(keyword);

            }
        });
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

    private class PHPDown extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... args) {
            StringBuilder jsonHtml = new StringBuilder();
            String link = args[1];
            link += "?keyword="+args[2];
            try {
                URL url = new URL(link);
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.connect();

                if(conn != null){
                    conn.setConnectTimeout(10000);
                    // conn.setUseCaches(false);
                    // 연결되었음 코드가 리턴되면.
                    if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){
                        InputStream is = conn.getInputStream();
                        BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                        for(;;){
                            // 웹상에 보여지는 텍스트를 라인단위로 읽어 저장.
                            String line = br.readLine();
                            if(line == null) break;
                            // 저장된 텍스트 라인을 jsonHtml에 붙여넣음
                            jsonHtml.append(line + "\n");
                        }
                        br.close();
                    }
                    conn.disconnect();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.i("Async", "Returned");
            return jsonHtml.toString();
        }

        @Override
        protected void onPostExecute(String str) {
            super.onPostExecute(str);

            String id;
            try {
                JSONArray jsonArray = new JSONArray(str);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject job = jsonArray.getJSONObject(i);
                    QuestionContent content = new QuestionContent(
                            Integer.parseInt(job.get("question_id").toString()),
                            job.get("title").toString(),
                            job.get("content").toString(),
                            job.get("questioner").toString(),
                            job.get("upload_date").toString(),
                            Integer.parseInt(job.get("vote").toString())
                    );
                    questionList.add(content);
                }
            }
            catch(JSONException e){
                e.printStackTrace();
            }
//이 부분에서 ArrayList questionList 에 필요한 정보가 들어있어요
        //ArrayAdapter 사용해서 ListView 안에 출력되게


        //    finish();
        }

    }

    ArrayList<QuestionContent> questionList;
    PHPDown task2;
    public void searchQuestion(String keyword){
        questionList = new ArrayList<QuestionContent>();
        String link = "http://knucsewiki.ivyro.net/select_question.php";
        task2 = new PHPDown();
        task2.execute("Query", link, keyword);
    }
}
