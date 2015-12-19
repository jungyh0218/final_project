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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class SearchResultActivity extends AppCompatActivity {

    Context mContext = this;

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
       /* ListView listView = (ListView)findViewById(R.id.listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                QuestionContent item = questionList.get(position);

            }
        });*/
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
                if(str.equals("no data\n")){
                    Toast toast = Toast.makeText(SearchResultActivity.this, "검색결과가 존재하지 않습니다.", Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }
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

            ImageView imageView = (ImageView) findViewById(R.id.imageView);
            ImageView imageViewMark = (ImageView) findViewById(R.id.imageViewMark);
            TextView textView = (TextView) findViewById(R.id.textViewInfo);

        //ListView 출력
            ListView listView = (ListView) findViewById(R.id.listView);
            QuestionListAdapter adapter = new QuestionListAdapter(mContext,0,questionList);
            listView.setAdapter(adapter);

            listView.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.INVISIBLE);
            imageViewMark.setVisibility(View.INVISIBLE);
            textView.setVisibility(View.INVISIBLE);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    QuestionContent item = questionList.get(position);
                    Intent intent = new Intent(SearchResultActivity.this, QuestionViewActivity.class);
                    intent.putExtra("title", item.getTitle());
                    intent.putExtra("content", item.getContent());
                    intent.putExtra("questioner", item.getQuestioner());
                    intent.putExtra("date", item.getDate());
                    intent.putExtra("question_id", item.getQuestionId());
                    intent.putExtra("vote", item.getVote());
                    startActivity(intent);
                }
            });
        }
    }

    private class QuestionListAdapter extends ArrayAdapter<QuestionContent> {

        private ArrayList<QuestionContent> mQuestionData;

        public QuestionListAdapter(Context context, int resource, ArrayList<QuestionContent> questionData) {
            super(context, resource, questionData);

            mQuestionData = questionData;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
            View rowView= inflater.inflate(R.layout.item, null, true);

            TextView txtTitle = (TextView) rowView.findViewById(R.id.textViewTitle);
            TextView txtContent = (TextView) rowView.findViewById(R.id.textViewContent);
            TextView txtOption = (TextView) rowView.findViewById(R.id.textViewOption);

            String title = mQuestionData.get(position).getTitle().replaceAll("\n"," ");
            String preview = mQuestionData.get(position).getContent().replaceAll("\n"," ");
            String date = mQuestionData.get(position).getDate();
            String id = mQuestionData.get(position).getQuestioner();
            int vote = mQuestionData.get(position).getVote();

            if(title.length() > 50)
                txtTitle.setText(title.substring(0, 50)+"...");
            else
                txtTitle.setText(title);

            if(preview.length() > 100)
                txtContent.setText(preview.substring(0, 100)+"...");
            else
                txtContent.setText(preview);

            date = calDate(date);
            txtOption.setText(date + "  |  "+ id +"  |  추천 "+vote);

            return rowView;
        }
    }

    public String calDate(String thatday){
        String output = "";
        int todayS, thatdayS, sub;

        // 현재 날짜 구하기
        Calendar calendar = new GregorianCalendar(Locale.KOREA);
        SimpleDateFormat fm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String today = fm.format(new Date());

        //같은 날
        if (today.substring(0, 10).equals(thatday.substring(0, 10))){
            thatdayS = Integer.parseInt(thatday.substring(11, 13))*3600
                    + Integer.parseInt(thatday.substring(14, 16))*60 + Integer.parseInt(thatday.substring(17, 19));
            todayS = Integer.parseInt(today.substring(11, 13))*3600
                    + Integer.parseInt(today.substring(14, 16))*60 + Integer.parseInt(today.substring(17, 19));

            sub = todayS - thatdayS;

            if(sub < 60) output = Integer.toString(sub)+"초 전";
            else if(sub < 3600) output = Integer.toString(sub/60)+"분 전";
            else output = Integer.toString(sub/3600)+"시간 전";
        }

        //1일 이상 전
        else {
            if(today.substring(0, 7).equals(thatday.substring(0, 7))) {
                sub = Integer.parseInt(today.substring(8, 10)) - Integer.parseInt(thatday.substring(8, 10));
                output = Integer.toString(sub) + "일 전";
            }
            else if(today.substring(0, 4).equals(thatday.substring(0, 4))){
                sub = Integer.parseInt(today.substring(5, 7)) - Integer.parseInt(thatday.substring(5, 7));
                output = Integer.toString(sub) + "달 전";
            }
            else
                output = thatday.substring(0, 10);
        }
        return output;
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
