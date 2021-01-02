package com.url_nfc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private NfcAdapter nfcAdapter;
    private EditText uRIInput;
    private Button addURIButton;
    private Button scanURIButton;
    private String uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // nfc
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        /* if(nfcAdapter !=null  && nfcAdapter.isEnabled()){
            Toast.makeText(this, "NFC AVAILABLE", Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(this,"NFC NOT AVAILABLE(:",Toast.LENGTH_LONG).show();
        }

        // référencer les éléments
        uRIInput = findViewById(R.id.activity_main_add_link);
        addURIButton = findViewById(R.id.activity_main_add_botton);
        scanURIButton = findViewById(R.id.activity_main_scan_botton);

        addURIButton.setEnabled(false);
        uRIInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                addURIButton.setEnabled(s.toString().length() != 0);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        scanURIButton.setOnClickListener(new View.OnClickListener() {
            @Override

            // User clicked the button
            public void onClick(View v) {
                Intent scanRFDActivityIntent = new Intent(MainActivity.this, ScanNFCActivity.class);
                startActivity(scanRFDActivityIntent);
            }
        });

        addURIButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addURI();
            }
        });*/
    }

    @Override
    protected void onNewIntent(Intent intent) {

        super.onNewIntent(intent);
        if (intent.hasExtra(NfcAdapter.EXTRA_TAG))
        {
            Toast.makeText(this, "NfcIntent!", Toast.LENGTH_SHORT).show();

            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            NdefMessage ndefMessage = createNdefMessage("My string content");

            wirteNedfMessage(tag, ndefMessage);
        }

        /*Toast.makeText(this, this.uri, Toast.LENGTH_LONG).show();
        if(NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction()))
        {
            Toast.makeText(this, "Intent dans if !", Toast.LENGTH_LONG).show();
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            Ndef ndef = Ndef.get(tag);
            try {
                ndef.connect();
                NdefRecord[] records = ndef.getNdefMessage().getRecords();
                records[0] = NdefRecord.createUri(this.uri);
                NdefMessage msg = new NdefMessage(records);
                ndef.writeNdefMessage(msg);
                NdefMessage message = ndef.getNdefMessage();
                Uri uri = message.getRecords()[0].toUri();
                System.out.println(uri);
                ndef.close();

            } catch (IOException | FormatException e) {
                e.printStackTrace();
            }

        }*/
    }

    @Override
    protected void onResume() {
        super.onResume();
        enableForegroundDispatchSystem();
    }

    @Override
    protected void onPause() {
        super.onPause();
        disableForegroundDispatchSystem();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    public void addURI()
    {
        this.uri = this.uRIInput.getText().toString();
    }

    private void enableForegroundDispatchSystem() {
        Intent intent = new Intent(this, MainActivity.class).addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        IntentFilter[] intentFilters = new IntentFilter[] {};
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFilters, null);

    }

    private void disableForegroundDispatchSystem() {
        nfcAdapter.disableForegroundDispatch(this);
    }

    private void formatTag(Tag tag, NdefMessage ndefMessage) {
        try {
            NdefFormatable ndefFormatable = NdefFormatable.get(tag);

            if (ndefFormatable == null) {
                Toast.makeText(this, "Tag is not ndef formatable", Toast.LENGTH_SHORT).show();
            }

            ndefFormatable.connect();
            ndefFormatable.format(ndefMessage);
            ndefFormatable.close();


        } catch (Exception e) {
            Log.e("formatTag", e.getMessage());
        }
    }

    private void wirteNedfMessage(Tag tag, NdefMessage ndefMessage) {
        try {
            if (tag == null) {
                Toast.makeText(this, "Tag object cannot be null", Toast.LENGTH_SHORT).show();
                return;
            }

            Ndef ndef = Ndef.get(tag);

            if (ndef == null) {

                // format tag with the ndef format and write the message
                formatTag(tag, ndefMessage);
            } else {
                ndef.connect();

                if (!ndef.isWritable()) {
                    Toast.makeText(this, "Tag is not writable !", Toast.LENGTH_SHORT).show();
                    ndef.close();
                    return;
                }

                ndef.writeNdefMessage(ndefMessage);
                ndef.close();
                Toast.makeText(this, "Tag writen!", Toast.LENGTH_SHORT).show();

            }
        } catch (Exception e) {
            Log.e("writeNdefMessage", e.getMessage());
        }
    }

    private NdefRecord createTextRecord(String content) {
        try {
            byte[] language;

            language = Locale.getDefault().getLanguage().getBytes("UTF-8");

            final byte[] text = content.getBytes("UTF-8");
            final int languageSize = language.length;
            final int textLength = text.length;
            final ByteArrayOutputStream payload = new ByteArrayOutputStream(1 + languageSize + textLength);

            payload.write((byte) (languageSize & 0x1f));
            payload.write(language, 0, textLength);
            payload.write(text, 0, textLength);

            return new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], payload.toByteArray());

        } catch (UnsupportedEncodingException e) {
            Log.e("createTextRecord", e.getMessage());
        }

        return null;
    }

    private NdefMessage createNdefMessage(String content) {
        NdefRecord ndefRecord = createTextRecord(content);
        NdefMessage ndefMessage = new NdefMessage(new NdefRecord[]{ndefRecord});

        return ndefMessage;
    }


}