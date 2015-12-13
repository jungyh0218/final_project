package knu.mobile.lookatmyenglish;

import android.app.LauncherActivity;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
public class SignInActivity extends AppCompatActivity {
    public static boolean isLoggedIn = false;
    public static int memberIdx = -1;
    EditText idField;
    EditText pwField;
    PHPDown phpDown;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        idField = (EditText)findViewById(R.id.editText_id);
        pwField = (EditText)findViewById(R.id.editText_password);
        phpDown = new PHPDown();

    }

    private class PHPDown extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... args) {
            StringBuilder jsonHtml = new StringBuilder();
            String username = args[0];
            String password = args[1];
            String link = "http://knucsewiki.ivyro.net/login.php?id="+username+"&password="+password;
            try {
                URL url = new URL(link);
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                conn.setDoInput(true);
                conn.connect();

                if(conn != null){
                    conn.setConnectTimeout(10000);
                   // conn.setUseCaches(false);
                    // 연결되었음 코드가 리턴되면.
                    if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){
                        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
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
            if (str.equals("no data")) {
                Toast toast = Toast.makeText(SignInActivity.this, "로그인 실패!", Toast.LENGTH_SHORT);
                toast.show();
            } else {
                try {
                    JSONObject root = new JSONObject(str);
                    String result = root.getString("status");
                    if (result.equals("OK")) {
                        isLoggedIn = true;
                        memberIdx = Integer.parseInt(root.getString("idx"));
                        Toast toast = Toast.makeText(SignInActivity.this, "로그인 성공!", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            finish();

        }

    }

    public void login(View v){
        String username = idField.getText().toString();
        String password = pwField.getText().toString();
        phpDown.execute(username, password);
    }
}
