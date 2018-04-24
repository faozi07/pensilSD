package id.suksesit.pensil.helper;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ResetStatus extends AsyncTask<String, Void, String> {
    private Context context;
    private java.lang.String link = Http.url;

    public ResetStatus(Context context) {
        this.context = context;
    }

    protected void onPreExecute() {
    }

    @Override
    protected java.lang.String doInBackground(java.lang.String... arg0) {

        BufferedReader bufferedReader;
        java.lang.String result;
        try {

            link = link + "penso/reset_status.php";

            URL url = new URL(link);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
            result = bufferedReader.readLine();
            return result;
        } catch (Exception e) {
            return new java.lang.String("Exception: " + e.getMessage());
        }
    }

    @Override
    protected void onPostExecute(java.lang.String result) {
        java.lang.String jsonStr = result;
//        Toast.makeText(context, link, Toast.LENGTH_SHORT).show();
        if (jsonStr != null) {
            try {
                JSONObject jsonObj = new JSONObject(jsonStr);
                java.lang.String query_result = jsonObj.getString("query_result");
                if (query_result.equals("SUCCESS")) {
//                    Toast.makeText(context, "Berhasil Reset Status Data", Toast.LENGTH_SHORT).show();
                } else if (query_result.equals("FAILED")) {
                    Toast.makeText(context, "Data could not be updated.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Couldn't connect to remote database.", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(context, "Error parsing JSON data.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(context, "Couldn't get any JSON data.", Toast.LENGTH_SHORT).show();
        }
    }
}

