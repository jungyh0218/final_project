package knu.mobile.lookatmyenglish;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class AnswerActivity extends AppCompatActivity {

    private int question_id;
    private int answerer = SignInActivity.memberIdx;
    private String question_title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer);
        QuestionContent content;
        question_id = getIntent().getIntExtra("question_id", 0);
        question_title =  getIntent().getStringExtra("title"); ///////////////★
        EditText editText = (EditText)findViewById(R.id.editTextTitle2);///////////////★
        editText.setText(question_title+ " 번역/해석");///////////////★

    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageButtonClose: //X버튼
                finish();
                break;

            case R.id.imageButtonCheck: //V버튼
                String title = ((EditText)findViewById(R.id.editTextTitle2)).getText().toString();
                String content = ((EditText)findViewById(R.id.editTextContent2)).getText().toString();
                insert(title, content);
                finish();
                break;
        }
    }


    PHPUp task;
    public boolean insert(String title, String content){
        String url = "http://knucsewiki.ivyro.net/insert_answer.php";
        task = new PHPUp();
        task.execute("INSERT", url, title, content, Integer.toString(question_id), Integer.toString(answerer));
        return true;
    }

    private class PHPUp extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... args) {
            StringBuilder jsonHtml = new StringBuilder();
            String link = args[1];
            JSONObject job = new JSONObject();

            try {
                job.put("title", args[2]);
                job.put("content", args[3]);
                job.put("question_id", args[4]);
                job.put("answerer", args[5]);
            }catch (JSONException e) {
                e.printStackTrace();
            }


            try {
                URL url = new URL(link);
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.connect();
                OutputStream os = conn.getOutputStream();
                os.write(job.toString().getBytes());
                os.flush();
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
        protected void onPostExecute(String s) {

            super.onPostExecute(s);
            Toast toast = Toast.makeText(AnswerActivity.this, "답변 등록 완료", Toast.LENGTH_SHORT);
            toast.show();
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }
}
