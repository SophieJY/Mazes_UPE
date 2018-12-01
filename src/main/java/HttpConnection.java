import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class HttpConnection {
    private static final String POST_URL= "http://ec2-34-216-8-43.us-west-2.compute.amazonaws.com/session";
    private static final String UID = "904818173";
    private String token;
    private static final String GET_STATE_URL = "http://ec2-34-216-8-43.us-west-2.compute.amazonaws.com/game?token=";
    private static final String POST_STATE_URL = "http://ec2-34-216-8-43.us-west-2.compute.amazonaws.com/game?token=";
    private static HttpClient httpClient;

    public void initToken() {
        httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(POST_URL);

        List<NameValuePair> params = new ArrayList<NameValuePair>(1);
        params.add(new BasicNameValuePair("uid", UID));
        httpPost.setEntity(new UrlEncodedFormEntity(params, StandardCharsets.UTF_8));

        HttpResponse response = null;
        try {
            response = httpClient.execute(httpPost);
        } catch (IOException e) {
            e.printStackTrace();
        }
        HttpEntity entity = response.getEntity();

        if(entity != null) {
            String responseString = null;
            try {
                responseString = EntityUtils.toString(entity, StandardCharsets.UTF_8);
            } catch (IOException e) {
                e.printStackTrace();
            }
            JSONObject jsonObject = new JSONObject(responseString);

            token = jsonObject.get("token").toString();
            System.out.print("token: " + token + "\n");
        }
    }

    //get the Maze state, return the status of the game state
    public JSONObject getMazeState() {
        JSONObject jsonObject = null;
        HttpGet httpGet = new HttpGet(GET_STATE_URL + token);
        HttpResponse response = null;
        try {
            response = httpClient.execute(httpGet);
        } catch (IOException e) {
            e.printStackTrace();
        }

        HttpEntity entity= response.getEntity();
        if(entity != null){
            String responseString = null;
            try {
                responseString = EntityUtils.toString(entity, StandardCharsets.UTF_8);
            } catch (IOException e) {
                e.printStackTrace();
            }
            jsonObject = new JSONObject(responseString);

            System.out.print(responseString + "\n");
        }
        return jsonObject;
    }

    public String tryMove(String direction) {
        HttpPost request = new HttpPost(POST_STATE_URL+token);

        List<NameValuePair> params = new ArrayList<NameValuePair>(1);
        params.add(new BasicNameValuePair("action", direction));
        request.setEntity(new UrlEncodedFormEntity(params, StandardCharsets.UTF_8));

        String result = "";

        HttpResponse response = null;
        try {
            response = httpClient.execute(request);
        } catch (IOException e) {
            e.printStackTrace();
        }
        HttpEntity entity = response.getEntity();

        if(entity != null) {
            String responseString = null;
            try {
                responseString = EntityUtils.toString(entity, StandardCharsets.UTF_8);
            } catch (IOException e) {
                e.printStackTrace();
            }
            JSONObject jsonObject = new JSONObject(responseString);

            result = jsonObject.get("result").toString();
        }
        return result;
    }

}
