package com.example.hiyoriaya.feb11;

import android.app.Activity;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.os.Parcelable;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.nio.charset.Charset;

/**
 * 写経です
 */

public class MainActivity extends Activity implements NfcAdapter.CreateNdefMessageCallback{

    NfcAdapter mNfcAdapter;
    EditText e1;
    EditText e2;
    EditText e3;
    EditText e4;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editssend);
        findViews();

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if(mNfcAdapter == null){
            Toast.makeText(this,"NFC is not available",Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        mNfcAdapter.setNdefPushMessageCallback(this, this);
    }

    public void findViews(){
        e1 = (EditText)findViewById(R.id.e1);
        e2 = (EditText)findViewById(R.id.e2);
        e3 = (EditText)findViewById(R.id.e3);
        e4 = (EditText)findViewById(R.id.e4);
    }

    //NDEFの作成
    @Override
    public NdefMessage createNdefMessage(NfcEvent event) {
        //String text = String.valueOf(e1.getText())+"\n"+
         //       String.valueOf(e2.getText())+"\n"+
           //     String.valueOf(e3.getText())+"\n"+
             //   String.valueOf(e4.getText())+"\n";
        String text = ("Beam me up, Android!\n\n" +
                "Beam Time: " + System.currentTimeMillis());
        NdefMessage msg = new NdefMessage(
                new NdefRecord[] { createMimeRecord(
                        "application/com.example.android.beam", text.getBytes())
                });

        return msg;
    }

    @Override
    public void onNewIntent(Intent intent) {
        // インテントを処理するために、この後でonResumeが呼ばれる
        setIntent(intent);
    }

    @Override
    public void onResume(){
        super.onResume();
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
            processIntent(getIntent());
        }
    }
    /**
     * インテントからのNDEFメッセージのパースとTextViewへの表示
     */
    void processIntent(Intent intent) {
        textView = (TextView) findViewById(R.id.textView);
        Parcelable[] rawMsgs = intent.getParcelableArrayExtra(
                NfcAdapter.EXTRA_NDEF_MESSAGES);
        // ビームの送信中は一つだけのメッセージ
        NdefMessage msg = (NdefMessage) rawMsgs[0];
        // 現在は、レコード0はMIMEタイプを含む、レコード1はAARを含む
        textView.setText(new String(msg.getRecords()[0].getPayload()));
    }

    /**
     * NDEFレコード内にカスタムMIMEタイプをカプセル化して生成する
     */
    public NdefRecord createMimeRecord(String mimeType, byte[] payload) {
        byte[] mimeBytes = mimeType.getBytes(Charset.forName("US-ASCII"));
        NdefRecord mimeRecord = new NdefRecord(
                NdefRecord.TNF_MIME_MEDIA, mimeBytes, new byte[0], payload);
        return mimeRecord;
    }

}
