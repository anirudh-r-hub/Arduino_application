package com.example.arduino_application;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.text.format.DateUtils;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private TextToSpeech myTTS;
    private SpeechRecognizer mySpeechrecognizer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //################################

        /*this.editIp = (EditText) findViewById(R.id.ip);
        this.btnOn = (Button) findViewById(R.id.bon);
        this.btnOff = (Button) findViewById(R.id.boff);
        this.textInfo1 = (TextView) findViewById(R.id.info1);
        this.textInfo2 = (TextView) findViewById(R.id.info2);
        this.btnOn.setOnClickListener(this.btnOnOffClickListener);
        this.btnOff.setOnClickListener(btnOnOffClickListener);*/
//###################################################

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS,1);
                mySpeechrecognizer.startListening(intent);
            }
        });
        initialise_text_to_speech();
        initialise_speech_recognizer();
    }

    private void initialise_text_to_speech()
    {
        myTTS=new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(myTTS.getEngines().size()==0)
                {
                    Toast.makeText(MainActivity.this,"there is no speech engine",Toast.LENGTH_LONG).show();
                    finish();
                }
                else
                {
                    myTTS.setLanguage(Locale.US);
                    //speak("Hello! I am ready");
                    //speak("how are you?");
                    //speak("kya hai be lund fakir");
                }
            }
        });
    }

    private void initialise_speech_recognizer()
    {
        if(SpeechRecognizer.isRecognitionAvailable(this))
        {
            mySpeechrecognizer=SpeechRecognizer.createSpeechRecognizer(this);
            mySpeechrecognizer.setRecognitionListener(new RecognitionListener() {
                @Override
                public void onReadyForSpeech(Bundle params) {

                }

                @Override
                public void onBeginningOfSpeech() {

                }

                @Override
                public void onRmsChanged(float rmsdB) {

                }

                @Override
                public void onBufferReceived(byte[] buffer) {

                }

                @Override
                public void onEndOfSpeech() {

                }

                @Override
                public void onError(int error) {

                }

                @Override
                public void onResults(Bundle results) {
                    List<String> speech_results=results.getStringArrayList(
                      SpeechRecognizer.RESULTS_RECOGNITION
                    );
                    procesResult(speech_results.get(0));

                }

                @Override
                public void onPartialResults(Bundle partialResults) {

                }

                @Override
                public void onEvent(int eventType, Bundle params) {

                }
            });
        }
    }

    private void procesResult(String command)
    {
        command=command.toLowerCase();

        //what is your name

        if(command.indexOf("led")!=-1)
        {
            if(command.indexOf("on")!=-1)
            {
                speak("Turning on the lights");
                new TaskEsp("192.168.1.198/on").execute(new Void[0]);
            }

            if(command.indexOf("off")!=-1)
            {
                Date date=new Date();
                String time= DateUtils.formatDateTime(this,date.getTime(),
                        DateUtils.FORMAT_SHOW_TIME);
                //speak("The time is "+time);
                speak("Turning off the lights,sir");
                //speak(" bandh kartoy saaheb");
                new TaskEsp("192.168.1.198/off").execute(new Void[0]);
            }

        }
        else
        {
            //speak("seedha bol na chutiya. Kaam ki bhat kar.");
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private void speak(String message)
    {
        if(Build.VERSION.SDK_INT>=21)
            myTTS.speak(message,TextToSpeech.QUEUE_ADD,null,null);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onPause()
    {
        super.onPause();
        myTTS.shutdown();
    }


    //*************************************************

    Button btnOff;
    Button btnOn;
    EditText editIp;
    TextView textInfo1;
    TextView textInfo2;







    View.OnClickListener btnOnOffClickListener = new View.OnClickListener() {

        public void onClick(View v) {
            String onoff;
            if (v == MainActivity.this.btnOn) {
                onoff = "/on";
            } else {
                onoff = "/off";
            }
            MainActivity.this.btnOn.setEnabled(false);
            MainActivity.this.btnOff.setEnabled(false);
            new TaskEsp(MainActivity.this.editIp.getText().toString() + onoff).execute(new Void[0]);
        }
    };





    private class TaskEsp extends AsyncTask<Void, Void, String> {
        String server;

        TaskEsp(String server2) {
            this.server = server2;
        }

        /* access modifiers changed from: protected */
        public String doInBackground(Void... params) {
            final String p = "http://" + this.server;
            MainActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    //MainActivity.this.textInfo1.setText(p);
                }
            });
            String serverResponse = "";
            try {
                HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(p).openConnection();
                if (httpURLConnection.getResponseCode() != 200) {
                    return serverResponse;
                }
                InputStream inputStream = httpURLConnection.getInputStream();
                String serverResponse2 = new BufferedReader(new InputStreamReader(inputStream)).readLine();
                inputStream.close();
                return serverResponse2;
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return e.getMessage();
            } catch (IOException e2) {
                e2.printStackTrace();
                return e2.getMessage();
            }
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(String s) {
            //MainActivity.this.textInfo2.setText(s);
            //MainActivity.this.btnOn.setEnabled(true);
            //MainActivity.this.btnOff.setEnabled(true);
        }
    }


}
