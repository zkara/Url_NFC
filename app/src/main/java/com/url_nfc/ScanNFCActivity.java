package com.url_nfc;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.widget.Toast;

import java.io.IOException;

public class ScanNFCActivity extends AppCompatActivity {

    private NfcAdapter nfcAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_nfc);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if(nfcAdapter !=null  && nfcAdapter.isEnabled()){
            Toast.makeText(this, "NFC AVAILABLE", Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(this,"NFC NOT AVAILABLE(:",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);
        Toast.makeText(this, "NFC AVAILABLE", Toast.LENGTH_LONG).show();
        if(NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction()))
        {
            Toast.makeText(this, "NFC if", Toast.LENGTH_LONG).show();
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            Ndef ndef = Ndef.get(tag);
            try {
                ndef.connect();
                NdefMessage message = ndef.getNdefMessage();
                System.out.println("size = "+message.getRecords().length);
                Uri uri = message.getRecords()[0].toUri();
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(browserIntent);
                ndef.close();

            } catch (IOException | FormatException e) {
                e.printStackTrace();
            }

        }
    }
}