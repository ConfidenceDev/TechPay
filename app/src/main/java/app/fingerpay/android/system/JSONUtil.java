package app.fingerpay.android.system;

import android.os.AsyncTask;
import android.util.Base64;

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

import app.fingerpay.android.activities.MainActivity;

public class JSONUtil extends AsyncTask<Void, Void, Void> {
    String data = "";
    String dataParsed = "";
    String stringParsed = "";

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            URL url = new URL("");
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line = "";
            while (line != null) {
                line = bufferedReader.readLine();
                data += line;
            }

            JSONArray JA = new JSONArray(data);
            for (int i = 0; i < JA.length(); i++){
                JSONObject JO = (JSONObject) JA.get(i);
                stringParsed =  "Name:" + JO.get("name") + "\n" +
                                "Bank:" + JO.get("bankName") + "\n";

                dataParsed += stringParsed;
            }

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        MainActivity.data.setText(this.dataParsed);
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
