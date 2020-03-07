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
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import app.fingerpay.android.R;
import app.fingerpay.android.dialogs.TransactionDialog;
import app.fingerpay.android.system.JSONUtil;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity {

    public static TextView data;
    private Button payBtn;

    private FingerprintManager fingerprintManager;
    private KeyguardManager keyguardManager;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        data = findViewById(R.id.balanceValue);
        payBtn = findViewById(R.id.payBtn);

        payBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONUtil jsonUtil = new JSONUtil();
                jsonUtil.execute();
            }
        });

    }

    private void FingerPrint() {

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
            TransactionDialog transactionDialog = new TransactionDialog(this, fingerprintManager);
            transactionDialog.transact();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
