package app.fingerpay.android.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import app.fingerpay.android.R;
import app.fingerpay.android.dialogs.TransactionDialog;
import app.fingerpay.android.system.JSONUtil;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity {

    private Button payBtn;

    private FingerprintManager fingerprintManager;
    private KeyguardManager keyguardManager;

    public static TextView balance, bvn;
    private EditText bankName, accName, accNum, amt, pin;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.activity_main);

        bvn = findViewById(R.id.bvnNumber);
        balance = findViewById(R.id.balanceValue);
        bankName = findViewById(R.id.bankName);
        accName = findViewById(R.id.accName);
        accNum = findViewById(R.id.accNum);
        amt = findViewById(R.id.amt);
        pin = findViewById(R.id.accPin);
        payBtn = findViewById(R.id.payBtn);

        //Test BVN: 12345678901
        payBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String id = pin.getText().toString().trim();
                final String bName = bankName.getText().toString().trim();
                final String acNum = accNum.getText().toString().trim();
                final String acName = accName.getText().toString().trim();
                final String amount = amt.getText().toString().trim();

                if (!TextUtils.isEmpty(bName) && !TextUtils.isEmpty(acNum) &&
                        !TextUtils.isEmpty(acName) && !TextUtils.isEmpty(id) && !TextUtils.isEmpty(amount)) {
                    JSONUtil jsonUtil = new JSONUtil(MainActivity.this, id);
                    jsonUtil.execute();
                }else {
                    Toast.makeText(MainActivity.this, "All fields needs to be field!", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    public void fingerPrint(String fingerPrint) {

        fingerprintManager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);
        keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);

        if (!fingerprintManager.isHardwareDetected()) {
            Toast.makeText(this, "Fingerprint scanner not detected in device!", Toast.LENGTH_LONG).show();

        } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Enable fingerprint permission to continue!", Toast.LENGTH_LONG).show();
            ActivityCompat.requestPermissions(MainActivity.this, new String[]
                    {Manifest.permission.USE_FINGERPRINT}, 1);

        } else if (!keyguardManager.isKeyguardSecure()) {
            Toast.makeText(this, "Add lock to your phone in settings!", Toast.LENGTH_LONG).show();

        } else if (!fingerprintManager.hasEnrolledFingerprints()) {
            Toast.makeText(this, "You should at least add 1 fingerprint to use this feature!", Toast.LENGTH_LONG).show();

        } else {
            TransactionDialog transactionDialog = new TransactionDialog(this, fingerprintManager, fingerPrint);
            transactionDialog.transact();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
