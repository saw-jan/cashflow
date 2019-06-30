package me.kinsae.service;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.CancellationSignal;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

@TargetApi(Build.VERSION_CODES.M)
public class FingerprintHandler extends FingerprintManager.AuthenticationCallback{
    private CancellationSignal cancellationSignal;
    private Context context;
    //FingerprintHandler fh = new FingerprintHandler(FingerPrint.this);

    public FingerprintHandler(Context mContext){
        context = mContext;
        this.context = mContext;
    }

    //fingerprint authentication
    public void startAuth(FingerprintManager manage, FingerprintManager.CryptoObject cryptoObject){
        cancellationSignal = new CancellationSignal();
        if(ActivityCompat.checkSelfPermission(context, Manifest.permission.USE_FINGERPRINT)!= PackageManager.PERMISSION_GRANTED){
            return;
        }
        manage.authenticate(cryptoObject,cancellationSignal,0,this,null);
    }
    @Override
    public void onAuthenticationError(int errMsgId, CharSequence errString){
        //Toast.makeText(context,"Authentication Error\n"+errString,Toast.LENGTH_LONG).show();
    }
    //method for not matching to any fingerprints registered
    @Override
    public void onAuthenticationFailed(){
        //Toast.makeText(context,"Authentication Failed\n",Toast.LENGTH_LONG).show();
    }
    @Override
    public void onAuthenticationHelp(int helpMasId, CharSequence helpString){
        Toast.makeText(context,helpString,Toast.LENGTH_LONG).show();
    }
    //success fingerprint authentication
    @Override
    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result){
        Toast.makeText(context,"Success!",Toast.LENGTH_LONG).show();
        Intent main = new Intent(context,MainActivity.class);
        context.startActivity(main);
    }
}
