package knu.mobile.lookatmyenglish;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class QuestionViewActivity extends AppCompatActivity {

    ArrayList<AnswerContent> answerList;
    int question_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_view);

        Intent intent = getIntent();
        TextView titleTextView = (TextView)findViewById(R.id.textViewTitle);
        TextView contentTextView = (TextView)findViewById(R.id.textViewContent);
        TextView idTextView = (TextView)findViewById(R.id.textViewId);
        TextView timeTextView = (TextView)findViewById(R.id.textViewTime);
        titleTextView.setText(intent.getStringExtra("title"));
        contentTextView.setText(intent.getStringExtra("content"));
        idTextView.setText(intent.getStringExtra("questioner") + " | ");
        timeTextView.setText(intent.getStringExtra("date"));
        question_id = intent.getIntExtra("question_id", 0);
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
                i.putExtra("question_id", question_id);
                if(!SignInActivity.isLoggedIn)
                    i = new Intent(this, SignInActivity.class);
                startActivity(i);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        PHPDown task = new PHPDown();
        task.execute(Integer.toString(question_id));

    }

    public class PHPDown extends AsyncTask<String, Integer, String>{

        @Override
        protected String doInBackground(String... args) {
            StringBuilder jsonHtml = new StringBuilder();
            String id = args[0];
            String link = "http://knucsewiki.ivyro.net/select_answer.php?question_id="+id;
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
                return;
            } else {
                try {
                    answerList = new ArrayList<AnswerContent>();
                    JSONArray jsonArray = new JSONArray(str);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject job = jsonArray.getJSONObject(i);
                        AnswerContent content = new AnswerContent(
                                job.getInt("answer_id"),
                                job.getInt("question_id"),
                                job.getString("title"),
                                job.getString("content"),
                                job.getString("answerer"),
                                job.getString("upload_date"),
                                job.getInt("vote")
                        );
                       answerList.add(content);
                    }
                }catch(JSONException e){
                    e.printStackTrace();
                }
            }
            ListView listView = (ListView) findViewById(R.id.listViewAnswer);
            AnswerListAdapter adapName = new AnswerListAdapter(
                    QuestionViewActivity.this, 0, answerList
            );
            listView.setAdapter(adapName);
        }
    }

    private class AnswerListAdapter extends ArrayAdapter<AnswerContent>{
        private ArrayList<AnswerContent> mAnswerList;
        public AnswerListAdapter(Context context, int resource, ArrayList<AnswerContent> objects) {
            super(context, resource, objects);
            mAnswerList = objects;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater)QuestionViewActivity.this.getSystemService(LAYOUT_INFLATER_SERVICE);
            View rowView= inflater.inflate(R.layout.answer_item, null, true);

            TextView txtTitle = (TextView) rowView.findViewById(R.id.textViewTitleAnswer);
            TextView txtContent = (TextView) rowView.findViewById(R.id.textViewContentAnswer);
            TextView txtDate = (TextView) rowView.findViewById(R.id.textViewTimeAnswer);
            TextView txtId = (TextView) rowView.findViewById(R.id.textViewIdAnswer);
            txtTitle.setText(mAnswerList.get(position).getTitle());
            txtContent.setText(mAnswerList.get(position).getContent());
            txtDate.setText(mAnswerList.get(position).getDate());
            txtId.setText(mAnswerList.get(position).getAnswerer() + " | ");

            return rowView;
        }

    }

    public void vote(View v){
        if(SignInActivity.memberIdx == -1){
            Toast toast = Toast.makeText(this, "먼저 로그인하세요.", Toast.LENGTH_SHORT);
            toast.show();
            Intent intent = new Intent(this, SignInActivity.class);
            startActivity(intent);
            return;
        }
        PHPUp task = new PHPUp();
        switch(v.getId()){
            case R.id.imageButtonLike:
                task.execute("VoteUp");
                break;
            case R.id.imageButtonUnlike:
                task.execute("VoteDown");
                break;
        }
    }

    private class PHPUp extends AsyncTask<String, Integer, String>{

        @Override
        protected String doInBackground(String... args) {
            StringBuilder jsonHtml = new StringBuilder();
            String link = "";
            if(args[0].equals("VoteUp")) {
                link = "http://knucsewiki.ivyro.net/voteup.php?question_id=" + question_id + "&memberIdx="+SignInActivity.memberIdx;
            }else{
                link = "http://knucsewiki.ivyro.net/votedown.php?question_id=" + question_id + "&memberIdx="+SignInActivity.memberIdx;
            }
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

            try {
                JSONObject job = new JSONObject(str);
                String result = job.getString("status");

                if(result.equals("OK")){
                    Toast toast = Toast.makeText(QuestionViewActivity.this, "Vote 완료", Toast.LENGTH_SHORT);
                    toast.show();
                }else if(result.equals("Duplicated")){
                    Toast toast = Toast.makeText(QuestionViewActivity.this, "이미 투표했습니다.", Toast.LENGTH_SHORT);
                    toast.show();
                }else{
                    Toast toast = Toast.makeText(QuestionViewActivity.this, "Vote 실패", Toast.LENGTH_SHORT);
                    toast.show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
