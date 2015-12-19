package knu.mobile.lookatmyenglish;

import android.app.LauncherActivity;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
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
    PHPUp phpUp;
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
            if (str.equals("no data\n")) {
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
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }

    }

    public void login(View v){
        String username = idField.getText().toString();
        String password = pwField.getText().toString();
        phpDown = new PHPDown();
        phpDown.execute(username, password);
    }

    public void join(View v) {
        showLoginDialog();
    }

    //회원가입 다이얼로그 표시
    public void showLoginDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("회원가입");
        builder.setCancelable(false);

        /**join.xml 읽어들이기*/
        //Layout 리소스를 로드할 수 있는 객체
        LayoutInflater inflater = getLayoutInflater();

        //"/res/layout/join.xml" 파일을 로드하기
        //--> "OK"버튼이 눌러지면, 이 객체에 접근해서 포함된 EditText객체를 취득
        final View joinView = inflater.inflate(R.layout.join, null);

        //Dialog에 Message 대신, XML 레이아웃을 포함시킨다.
        builder.setView(joinView);

        /**취소버튼 처리*/
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), "취소되었습니다.", Toast.LENGTH_SHORT).show();
            }
        });

        /**확인버튼 처리*/
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                EditText et1 = (EditText) joinView.findViewById(R.id.editTextId);
                EditText et2 = (EditText) joinView.findViewById(R.id.editTextPw);

                String user_id = et1.getText().toString();
                String user_pw = et2.getText().toString();
                phpUp = new PHPUp();
                phpUp.execute(user_id, user_pw);
                Toast.makeText(getApplicationContext(),
                        "아이디:" + user_id + "/비밀번호:" + user_pw, Toast.LENGTH_SHORT).show();
            }
        });

        builder.create();
        builder.show();
    }

    private class PHPUp extends AsyncTask<String, Integer, String>{
        @Override
        protected String doInBackground(String... args) {
            StringBuilder jsonHtml = new StringBuilder();
            String username = args[0];
            String password = args[1];
            String link = "http://knucsewiki.ivyro.net/join.php?id="+username+"&password="+password;
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
    }
}
