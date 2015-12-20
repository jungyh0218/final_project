package knu.mobile.lookatmyenglish;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

        if(titleText.equals("")){
            Toast toast = Toast.makeText(this, "제목을 적어주세요.", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }

        insert(titleText, contentText.toString(), SignInActivity.memberIdx);
        Toast toast = Toast.makeText(this, "질문을 등록했습니다.", Toast.LENGTH_SHORT);
        toast.show();
        finish();
    }

    public void cancelActivity(View v){
        finish();
    }

    PHPUp task;
    public boolean insert(String title, String content, int memberIdx){
        String url = "http://knucsewiki.ivyro.net/insert_question.php";
        task = new PHPUp();
        task.execute("INSERT", url, title, content, Integer.toString(memberIdx));
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
                job.put("questioner", args[4]);
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

        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }

}
