package com.example.testmicro;

import static android.speech.RecognizerIntent.ACTION_RECOGNIZE_SPEECH;
import static android.speech.RecognizerIntent.EXTRA_LANGUAGE_MODEL;
import static android.speech.RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements RecognitionListener {

    private int permission = 100;
    private TextView tvTexte, tvMsg;
    private ToggleButton toggleButton;
    private SpeechRecognizer speech;
    private final Intent recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

    private void initUI(){
        tvTexte = findViewById(R.id.tvTexte);
        tvMsg = findViewById(R.id.tvMsg);
        toggleButton = findViewById(R.id.toggleButton);
    }

    private void initRecognitionListener() {
    }

    private void onCheckedChangeToggleButton() {
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.RECORD_AUDIO},
                            permission);

                } else {
                    speech.stopListening();
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == permission && grantResults.length > 0 ){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                speech.startListening(recognizerIntent);
            }
            else
                Toast.makeText(this,"Permission Denied",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onReadyForSpeech(Bundle params) {
        toggleButton.setBackgroundResource(R.drawable.ic_baseline_mic_24);
        tvMsg.setText("ReadyForSpeech");
    }

    @Override
    public void onBeginningOfSpeech() {
        tvMsg.setText("BeginningOfSpeech");
    }

    @Override
    public void onRmsChanged(float rmsdB) {

    }

    @Override
    public void onBufferReceived(byte[] buffer) {

    }

    @Override
    public void onEndOfSpeech() {
        tvMsg.setText("EndOfSpeech");
        toggleButton.setChecked(false);
    }

    private String getErrorText(int error) {
        String message = "";
        switch (error) {
            case SpeechRecognizer.ERROR_AUDIO : message = "Audio recording error"; break;
            case SpeechRecognizer.ERROR_CLIENT : message = "Client side error";
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS : message = "Insufficient permissions";
            case SpeechRecognizer.ERROR_NETWORK : message = "Network error";
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT : message = "Network timeout";
            case SpeechRecognizer.ERROR_NO_MATCH : message = "No match";
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY : message = "RecognitionService busy";
            case SpeechRecognizer.ERROR_SERVER : message = "error from server";
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT : message = "No speech input";
            default: message = "Didn't understand, please try again.";
        }
        return message;
    }
    @Override
    public void onError(int error) {
        String msg = getErrorText(error);
        tvMsg.setText("Error : " + msg);
        toggleButton.setChecked(false);
    }

    @Override
    public void onResults(Bundle results) {
        toggleButton.setBackgroundResource(R.drawable.ic_baseline_mic_off_24);
        ArrayList<String> data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        tvMsg.setText("RESULT");
        tvTexte.setText("");
        if (data != null){
            for (String s:data){
                tvTexte.setText(tvTexte.getText() + " " + s.trim());
            }
        }
//        val matches = results!!.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
//        var text = ""
//        if (matches != null) {
//            for (result in matches) text = """
//          $result
//          """.trimIndent()
//        }
//        returnedText.text = ext

    }

    @Override
    public void onPartialResults(Bundle partialResults) {

    }

    @Override
    public void onEvent(int eventType, Bundle params) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();
        speech = SpeechRecognizer.createSpeechRecognizer(this);
        speech.setRecognitionListener(this);
        recognizerIntent.putExtra(EXTRA_LANGUAGE_PREFERENCE, "FR-fr");
        recognizerIntent.putExtra(EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);
        onCheckedChangeToggleButton();
    }


}