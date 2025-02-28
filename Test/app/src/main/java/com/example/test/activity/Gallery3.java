package com.example.test.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.test.R;
import com.example.test.adapter.GridItem;
import com.example.test.adapter.GridViewAdapter;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class Gallery3 extends AppCompatActivity {

    private static final String TAG = Gallery3.class.getSimpleName();
    private GridView mGridView;
    private GridViewAdapter mGridAdapter;
    private ArrayList<GridItem> mGridData;
    private String FEED_URL = "assets/gallery.json";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LayoutInflater inflater = LayoutInflater.from(this);
        View view01 = inflater.inflate(R.layout.grid_item_layout, null);
        mGridView = (GridView) view01.findViewById(R.id.gridView);
        mGridData = new ArrayList<>();
        mGridAdapter = new GridViewAdapter(this, R.layout.grid_item_layout, mGridData);
        mGridView.setAdapter(mGridAdapter);
        new AsyncHttpTask().execute(FEED_URL);
    }

    //Downloading data asynchronously
    public class AsyncHttpTask extends AsyncTask<String, Void, Integer> {

        @Override
        protected Integer doInBackground(String... params) {
            Integer result = 0;
            try {
                // Create Apache HttpClient
                HttpClient httpclient = new DefaultHttpClient();
                HttpResponse httpResponse = httpclient.execute(new HttpGet(params[0]));
                int statusCode = httpResponse.getStatusLine().getStatusCode();

                // 200 represents HTTP OK
                if (statusCode == 200) {
                    String response = streamToString(httpResponse.getEntity().getContent());
                    parseResult(response);
                    result = 1; // Successful
                } else {
                    result = 0; //"Failed
                }
            } catch (Exception e) {
                Log.d(TAG, e.getLocalizedMessage());
            }
            return result;
        }
        String streamToString(InputStream stream) throws IOException {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));
            String line;
            String result = "";
            while ((line = bufferedReader.readLine()) != null) {
                result += line;
            }

            // Close stream
            if (null != stream) {
                stream.close();
            }
            return result;
        }
        @Override
        protected void onPostExecute(Integer result) {
            // Download complete. Let us update UI
            if (result == 1) {
                mGridAdapter.setGridData(mGridData);
            } else {
                Toast.makeText(Gallery3.this, "Failed to fetch data!", Toast.LENGTH_SHORT).show();
            }

        }
    }

    private void parseResult(String result) {

        try {
            JSONObject response = new JSONObject(result);
            JSONArray array = response.getJSONArray("data");
            GridItem item;
            for (int i = 0; i < array.length(); i++) {
                JSONObject imageobject = array.getJSONObject(i);
                String image_url = imageobject.getString("thumbnail");
                item = new GridItem();
                item.setImage(image_url);
                mGridData.add(item);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}