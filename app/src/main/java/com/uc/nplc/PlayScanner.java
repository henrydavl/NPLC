package com.uc.nplc;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class PlayScanner extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private ZXingScannerView mScannerView;
    RequestQueue requestQueue;
    String userid = "", posQR = "", idpos = "", idtrans = "", namapos = "";
    ActionBar bar;
    SharedPreferences userPref;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_scanner);

        pd = new ProgressDialog(PlayScanner.this);
        mScannerView = new ZXingScannerView(PlayScanner.this);
        setContentView(mScannerView);
        bar = getSupportActionBar();
        bar.setTitle("Scan QR Code POS");
        userPref = getSharedPreferences("user", MODE_PRIVATE);
        userid = userPref.getString("id","-");

    }

    @Override
    public void handleResult(Result result) {
        posQR = result.getText();
        String qr = posQR.substring(4,6);
        new AlertDialog.Builder(PlayScanner.this)
                .setTitle("Konfirmasi")
                .setMessage("Bermain di POS "+qr+"?")
                .setCancelable(false)
                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        pd.setCancelable(false);
                        pd.setTitle("Dalam proses");
                        pd.setMessage("Sedang masuk dalam thread, mohon tunggu...");
                        pd.show();
                        Runnable r = new Runnable() {
                            @Override
                            public void run() {
                                pd.cancel();
                            }
                        };
                        Handler cancel = new Handler();
                        cancel.postDelayed(r, 2000);

                        dialogInterface.cancel();

                    }
                })
                .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        onResume();
                    }
                })
                .create()
                .show();
        Toast.makeText(getApplicationContext(), posQR, Toast.LENGTH_LONG).show();
    }


    @Override
    public void onBackPressed() {
        mScannerView.stopCamera();
        Intent intent = new Intent(PlayScanner.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}