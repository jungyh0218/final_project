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
import android.widget.Button;
import android.widget.ImageButton;
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
    AnswerListAdapter adapName;
    int question_id;
    String question_title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_view);

        Intent intent = getIntent();
        TextView titleTextView = (TextView)findViewById(R.id.textViewTitle);
        TextView contentTextView = (TextView)findViewById(R.id.textViewContent);
        TextView idTextView = (TextView)findViewById(R.id.textViewId);
        TextView timeTextView = (TextView)findViewById(R.id.textViewTime);
        Button buttonVote = (Button)findViewById(R.id.buttonVote);
        titleTextView.setText(intent.getStringExtra("title"));
        contentTextView.setText(intent.getStringExtra("content"));
        idTextView.setText(intent.getStringExtra("questioner") + " | ");
        timeTextView.setText(intent.getStringExtra("date"));
        buttonVote.setText(intent.getIntExtra("vote", 0) + " 추천");
        question_id = intent.getIntExtra("question_id", 0);
        question_title = intent.getStringExtra("title");
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
                i.putExtra("title", question_title); ///////////////★
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
        //answerList = new ArrayList<AnswerContent>();
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
            adapName = new AnswerListAdapter(
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
            Button voteButton = (Button)rowView.findViewById(R.id.buttonVoteAnswer);
            voteButton.setText(mAnswerList.get(position).getVote() + " 추천");

            Button likeButton = (Button)rowView.findViewById(R.id.buttonLikeAnswer);
            Button unLikeButton = (Button)rowView.findViewById(R.id.buttonUnlikeAnswer);
            final int pos = position;
            likeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!SignInActivity.isLoggedIn) {
                        Toast toast = Toast.makeText(QuestionViewActivity.this, "먼저 로그인하세요.", Toast.LENGTH_SHORT);
                        toast.show();
                        Intent i = new Intent(QuestionViewActivity.this, SignInActivity.class);
                        startActivity(i);
                    } else {
                        PHPUp task = new PHPUp();
                        task.execute("VoteUpAnswer", Integer.toString(answerList.get(pos).getAnswerId()), Integer.toString(pos));
                    }
                }
            });
            unLikeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!SignInActivity.isLoggedIn) {
                        Toast toast = Toast.makeText(QuestionViewActivity.this, "먼저 로그인하세요.", Toast.LENGTH_SHORT);
                        toast.show();
                        Intent i = new Intent(QuestionViewActivity.this, SignInActivity.class);
                        startActivity(i);
                    } else {
                        PHPUp task = new PHPUp();
                        task.execute("VoteDownAnswer", Integer.toString(answerList.get(pos).getAnswerId()), Integer.toString(pos));
                    }
                }
            });

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
            case R.id.buttonLike:
                task.execute("VoteUp");
                break;
            case R.id.buttonUnlike:
                task.execute("VoteDown");
                break;
        }
    }

    private class PHPUp extends AsyncTask<String, Integer, String>{
            String command = "";
            int pos;
        @Override
        protected String doInBackground(String... args) {
            StringBuilder jsonHtml = new StringBuilder();
            String link = "";
            command = args[0];
            if(args[0].equals("VoteUp")) {
                link = "http://knucsewiki.ivyro.net/voteup.php?question_id=" + question_id + "&memberIdx="+SignInActivity.memberIdx;
            }else if(args[0].equals("VoteDown")){
                link = "http://knucsewiki.ivyro.net/votedown.php?question_id=" + question_id + "&memberIdx="+SignInActivity.memberIdx;
            }else if(args[0].equals("VoteUpAnswer")){
                pos = Integer.parseInt(args[2]);
                link = "http://knucsewiki.ivyro.net/voteup_answer.php?answer_id=" + args[1] + "&memberIdx="+SignInActivity.memberIdx;
            }else{
                pos = Integer.parseInt(args[2]);
                link = "http://knucsewiki.ivyro.net/votedown_answer.php?answer_id=" + args[1] + "&memberIdx="+SignInActivity.memberIdx;
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

                    if(command.equals("VoteUp")){
                        Button voteButton = (Button)findViewById(R.id.buttonVote);
                        String str2 = voteButton.getText().toString();
                        str2 = str2.split(" ")[0];
                        int num = Integer.parseInt(str2);
                        voteButton.setText((num + 1) + " 추천");
                        Button voteup = (Button)findViewById(R.id.buttonLike);
                        voteup.setBackgroundResource(R.drawable.checklike);
                    }else if(command.equals("VoteDown")){
                        Button voteButton = (Button)findViewById(R.id.buttonVote);
                        String str2 = voteButton.getText().toString();
                        str2 = str2.split(" ")[0];
                        int num = Integer.parseInt(str2);
                        voteButton.setText((num-1) + " 추천");
                        Button votedown = (Button)findViewById(R.id.buttonUnlike);
                        votedown.setBackgroundResource(R.drawable.checkunlike);
                    }else if(command.equals("VoteUpAnswer")){
                        ListView listView = (ListView)findViewById(R.id.listViewAnswer);
                        View rowView = (View)listView.getChildAt(pos);
                        Button voteButton = (Button)rowView.findViewById(R.id.buttonVoteAnswer);
                        String str2 = voteButton.getText().toString();
                        str2 = str2.split(" ")[0];
                        int num = Integer.parseInt(str2);
                        voteButton.setText((num+1) + " 추천");
                        Button voteup = (Button)rowView.findViewById(R.id.buttonLikeAnswer);
                        voteup.setBackgroundResource(R.drawable.checklike);
                    }else{
                        ListView listView = (ListView)findViewById(R.id.listViewAnswer);
                        View rowView = (View)listView.getChildAt(pos);
                        Button voteButton = (Button)rowView.findViewById(R.id.buttonVoteAnswer);
                        String str2 = voteButton.getText().toString();
                        str2 = str2.split(" ")[0];
                        int num = Integer.parseInt(str2);
                        voteButton.setText((num-1) + " 추천");
                        Button votedown = (Button)rowView.findViewById(R.id.buttonLikeAnswer);
                        votedown.setBackgroundResource(R.drawable.checklike);
                    }
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
