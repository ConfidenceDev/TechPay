package app.fingerpay.android.system;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Base64;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.HttpsURLConnection;

import app.fingerpay.android.R;
import app.fingerpay.android.activities.MainActivity;
import app.fingerpay.android.utils.CommaCounter;

public class JSONUtil extends AsyncTask<Void, Void, Void> {

    private String data = "";
    private String stringParsed = "", dataParsed = "";
    private String id;
    private AppCompatActivity activity;
    private CommaCounter commaCounter;

    public JSONUtil(AppCompatActivity activity, String id) {
        this.activity = activity;
        this.id = id;
        commaCounter = new CommaCounter();
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            URL url = new URL("https://api.myjson.com/bins/1d66da");
            HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();
            InputStream inputStream = httpsURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line = "";
            while (line != null) {
                line = bufferedReader.readLine();
                data += line;
            }

            JSONObject JO = new JSONObject(data);
            stringParsed = JO.getString("BVN") + "," +
                    JO.getString("BVNFingerprint") + "," +
                    JO.get("AccountBalance");

            dataParsed += stringParsed;
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        try {
            String[] arr = this.dataParsed.split(",");

            String bvn = arr[0];
            String fingerPrint = arr[1];
            String bal = arr[2];

            //Toast.makeText(activity, fingerPrint, Toast.LENGTH_LONG).show();

            MainActivity.bvn.setText("BVN : " + bvn);
            MainActivity.balance.setText(commaCounter.getFormattedValue(Integer.parseInt(bal)));
            ((MainActivity)activity).fingerPrint(fingerPrint);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String decrypt(String data) throws Exception {
        SecretKeySpec key = generateKey(data);
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decodeVal = Base64.decode(data, Base64.DEFAULT);
        byte[] decryptVal = cipher.doFinal(decodeVal);
        return new String(decryptVal);
    }

    private String encrypt(String data) throws Exception {
        SecretKeySpec key = generateKey(data);
        Cipher c = Cipher.getInstance("AES");
        c.init(Cipher.ENCRYPT_MODE, key);
        byte[] encVal = c.doFinal(data.getBytes());
        return Base64.encodeToString(encVal, Base64.DEFAULT);
    }

    private SecretKeySpec generateKey(String data) throws Exception {
        SecretKeySpec keySpec = null;
        try {
            final MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = data.getBytes("UTF-8");
            digest.update(bytes, 0, bytes.length);
            byte[] key = digest.digest();
            keySpec = new SecretKeySpec(key, "AES");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return keySpec;
    }
}
