package me.kinsae.service;

import android.Manifest;
import android.app.KeyguardManager;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class FingerPrint extends AppCompatActivity {

    //declarations
    private static final String KEY_NAME = "UKey";
    private Cipher cipher;
    private KeyStore keyStore;
    private KeyGenerator keyGenerator;
    private FingerprintManager.CryptoObject cryptoObject;
    private FingerprintManager fingerprintManager;
    private KeyguardManager keyguardManager;
    private TextView txtbox;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finger_print);
        //check the build version of device
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
            fingerprintManager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);

            txtbox = findViewById(R.id.progress);

            //check device for fingerprint sensor
            if(!fingerprintManager.isHardwareDetected()){
                txtbox.setText("Fingerprint sensor not detected");
            }
            //check for user permission
            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT)!= PackageManager.PERMISSION_GRANTED){
                txtbox.setText("Fingerprint not enabled");
            }
            //check for finger registration
            if(!fingerprintManager.hasEnrolledFingerprints()){
                txtbox.setText("No Fingerprint detected.");
            }
            if(!keyguardManager.isKeyguardSecure()){
                txtbox.setText("First set lockscreen security");
            }
            else {
                try{
                generateKey();
                }catch (FingerprintException e){
                    e.printStackTrace();
                }
                if(initCipher()){
                    cryptoObject = new FingerprintManager.CryptoObject(cipher);

                    //custom class
                    FingerprintHandler helper = new FingerprintHandler(this);
                    helper.startAuth(fingerprintManager,cryptoObject);
                }
            }
        }
    }
    //method
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void generateKey() throws FingerprintException{
        try{
            keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES,"AndroidKeyStore");
            keyStore.load(null);
            keyGenerator.init(new
                    KeyGenParameterSpec.Builder(KEY_NAME,KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
            .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
            .setUserAuthenticationRequired(true)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
            .build());

            keyGenerator.generateKey();
        }catch (KeyStoreException
                |NoSuchAlgorithmException
                |NoSuchProviderException
                |InvalidAlgorithmParameterException
                |CertificateException
                |IOException exc){
            exc.printStackTrace();
            throw new FingerprintException(exc);
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    public boolean initCipher(){
        try{
            cipher = Cipher.getInstance(
                    KeyProperties.KEY_ALGORITHM_AES +"/"
                    +KeyProperties.BLOCK_MODE_CBC+"/"
                    +KeyProperties.ENCRYPTION_PADDING_PKCS7);
        }catch (NoSuchAlgorithmException
                |NoSuchPaddingException e){
            throw new RuntimeException("Failed"+e);
        }

        try{
            keyStore.load(null);
            SecretKey key = (SecretKey) keyStore.getKey(KEY_NAME,null);
            cipher.init(Cipher.ENCRYPT_MODE,key);
            return true;
        }catch (KeyPermanentlyInvalidatedException e){
            return false;
        }catch (KeyStoreException | CertificateException
                |UnrecoverableKeyException | IOException
                |NoSuchAlgorithmException | InvalidKeyException e){
            throw new RuntimeException("Failed",e);
        }
    }
    private class FingerprintException extends Exception{
        public FingerprintException(Exception e){
            super(e);
        }
    }
}
