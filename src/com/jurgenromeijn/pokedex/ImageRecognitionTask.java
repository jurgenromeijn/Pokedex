package com.jurgenromeijn.pokedex;

import android.os.AsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

/**
 * Send image data to the Kooaba webservice and make the resultPresenter present the result.
 */
public class ImageRecognitionTask extends AsyncTask<byte[], Void, String> {
    private IImageRecognitionResultPresenter _resultPresenter;
    private String _secretKey;
    public final String API_ADRESS = "https://query-api.kooaba.com/v4/query";
    public final String BOUNDARY = "51768e7298680";

    /**
     * Setup required paramters.
     *
     * @param resultPresenter
     * @param secretKey
     */
    public ImageRecognitionTask(IImageRecognitionResultPresenter resultPresenter, String secretKey) {
        this._resultPresenter = resultPresenter;
        this._secretKey = secretKey;
    }

    /**
     * Find the entity on another thread.
     *
     * @param bytes
     * @return
     */
    @Override
    protected String doInBackground(byte[]... bytes) {
        return findEntityInImage(bytes[0]);
    }

    /**
     * Show the found entity
     *
     * @param result
     */
    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        _resultPresenter.showImageRecognitionResult(result);
    }

    /**
     * find the name of the result and return it.
     *
     * @param data
     * @return
     */
    private String findEntityInImage(byte[] data)
    {
        HttpResponse response = doQueryRequest(data);
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
            String json = reader.readLine();
            JSONTokener tokener = new JSONTokener(json);
            JSONObject responseObject = new JSONObject(tokener);
            return responseObject.getJSONArray("results").getJSONObject(0).getString("reference_id");
        } catch (Exception e) {
            // TODO: handle exception
        }
        return "";
    }

    /**
     * Send the image data to the kooaba webservice and return the result.
     *
     * @param data
     * @return
     */
    private HttpResponse doQueryRequest(byte[] data)
    {
        try {
            // Create content
            MultipartEntity content = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE, BOUNDARY, Charset.defaultCharset());
            content.addPart("image", new ByteArrayBody(data, "photo.jpg"));

            // Build request
            HttpClient client = new DefaultHttpClient();
            HttpPost request = new HttpPost(API_ADRESS);
            request.setEntity(content);
            request.addHeader("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);
            request.addHeader("Authorization", "Token " + _secretKey);

            // Send request
            HttpResponse response = client.execute(request);
            return response;
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}