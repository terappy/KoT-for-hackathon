package com.camera.kot_for_one;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.mlkcca.client.DataElement;
import com.mlkcca.client.DataElementValue;
import com.mlkcca.client.DataStore;
import com.mlkcca.client.DataStoreEventListener;
import com.mlkcca.client.MilkCocoa;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.IOException;
import java.net.URI;

public class MjpegActivity extends Activity implements DataStoreEventListener {
    private static final boolean DEBUG = false;
    private static final String TAG = "MJPEG";

    private MjpegView mv = null;
    String URL;

    // for settings (network and resolution)
    private static final int REQUEST_SETTINGS = 0;

    private int width = 320;
    private int height = 240;

    private int ip_ad1 = 192;
    private int ip_ad2 = 168;
    private int ip_ad3 = 0;
    private int ip_ad4 = 80;
    private int ip_port = 8080;
    private String ip_command = "?action=stream";

    private boolean suspending = false;

    final Handler handler = new Handler();

    TextView stateText;
    ImageView onlinestate;
    ToggleButton onoff;
    boolean toggleflag;



    //////////////////////////////////////////////
    private MilkCocoa milkcocoa;
    private DataStore messagesDataStore;

    //////////////////////////////////////////////

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences preferences = getSharedPreferences("SAVED_VALUES", MODE_PRIVATE);
        width = preferences.getInt("width", width);
        height = preferences.getInt("height", height);
        ip_ad1 = preferences.getInt("ip_ad1", ip_ad1);
        ip_ad2 = preferences.getInt("ip_ad2", ip_ad2);
        ip_ad3 = preferences.getInt("ip_ad3", ip_ad3);
        ip_ad4 = preferences.getInt("ip_ad4", ip_ad4);
        ip_port = preferences.getInt("ip_port", ip_port);
        ip_command = preferences.getString("ip_command", ip_command);

        StringBuilder sb = new StringBuilder();
        String s_http = "http://";
        String s_dot = ".";
        String s_colon = ":";
        String s_slash = "/";
        sb.append(s_http);
        sb.append(ip_ad1);
        sb.append(s_dot);
        sb.append(ip_ad2);
        sb.append(s_dot);
        sb.append(ip_ad3);
        sb.append(s_dot);
        sb.append(ip_ad4);
        sb.append(s_colon);
        sb.append(ip_port);
        sb.append(s_slash);
        sb.append(ip_command);
        URL = new String(sb);

        setContentView(R.layout.main);
        mv = (MjpegView) findViewById(R.id.mv);
        if (mv != null) {
            mv.setResolution(width, height);
        }

        stateText = (TextView)findViewById(R.id.onlinetext);
        onlinestate = (ImageView)findViewById(R.id.onlinestate);


        Button rollleft = (Button)findViewById(R.id.rollleft);
        Button rollright = (Button)findViewById(R.id.rollright);
        Button callBtn = (Button)findViewById(R.id.callBtn);
        Button shcallBtn = (Button)findViewById(R.id.shcallBtn);
        Button goodBtn = (Button)findViewById(R.id.goodBtn);
        Button byebyeBtn = (Button)findViewById(R.id.byebyeBtn);
        ImageButton prize = (ImageButton) findViewById(R.id.prize);

        onoff = (ToggleButton)findViewById(R.id.toggleButton);
        toggleflag = false;

//        rollleft.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                DataElementValue params = new DataElementValue();
//                params.put("operation", "left");
//                messagesDataStore.push(params);
//                Toast.makeText(getApplicationContext(), "Left!", Toast.LENGTH_SHORT).show();
//            }
//        });

        rollleft.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    DataElementValue params = new DataElementValue();
                    params.put("operation", "leftdown");
                    messagesDataStore.push(params);
//                    Toast.makeText(getApplicationContext(), "LeftDown!", Toast.LENGTH_SHORT).show();
                }else if(event.getAction() == MotionEvent.ACTION_UP){
                    DataElementValue params = new DataElementValue();
                    params.put("operation", "leftup");
                    messagesDataStore.push(params);
//                    Toast.makeText(getApplicationContext(), "LeftUp!", Toast.LENGTH_SHORT).show();
                }

                return false;
            }
        });

//        rollright.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                DataElementValue params = new DataElementValue();
//                params.put("operation", "right");
//                messagesDataStore.push(params);
//                Toast.makeText(getApplicationContext(), "Right!", Toast.LENGTH_SHORT).show();
//            }
//        });

        rollright.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    DataElementValue params = new DataElementValue();
                    params.put("operation", "rightdown");
                    messagesDataStore.push(params);
//                    Toast.makeText(getApplicationContext(), "RightDown!", Toast.LENGTH_SHORT).show();
                }else if(event.getAction() == MotionEvent.ACTION_UP){
                    DataElementValue params = new DataElementValue();
                    params.put("operation", "rightup");
                    messagesDataStore.push(params);
//                    Toast.makeText(getApplicationContext(), "RightUp!", Toast.LENGTH_SHORT).show();
                }

                return false;
            }
        });


        callBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataElementValue params = new DataElementValue();
                params.put("operation", "call");
                messagesDataStore.push(params);
//                Toast.makeText(getApplicationContext(), "Call!", Toast.LENGTH_SHORT).show();
            }
        });
        shcallBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataElementValue params = new DataElementValue();
                params.put("operation", "shcall");
                messagesDataStore.push(params);
//                Toast.makeText(getApplicationContext(), "Shoot!", Toast.LENGTH_SHORT).show();
            }
        });
        goodBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataElementValue params = new DataElementValue();
                params.put("operation", "good");
                messagesDataStore.push(params);
//                Toast.makeText(getApplicationContext(), "Good!", Toast.LENGTH_SHORT).show();
            }
        });
        byebyeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataElementValue params = new DataElementValue();
                params.put("operation", "byebye");
                messagesDataStore.push(params);
//                Toast.makeText(getApplicationContext(), "Bye!", Toast.LENGTH_SHORT).show();
            }
        });
        prize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataElementValue params = new DataElementValue();
                params.put("operation", "prize");
                messagesDataStore.push(params);
//                Toast.makeText(getApplicationContext(), "Prize!", Toast.LENGTH_SHORT).show();
            }
        });

        onoff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(toggleflag) {
                    toggleflag = false;
                }else {
                    if (isChecked) {
                        DataElementValue params = new DataElementValue();
                        params.put("operation", "playon");
                        messagesDataStore.push(params);
//                    Toast.makeText(getApplicationContext(), "PlayON!", Toast.LENGTH_SHORT).show();
                    } else {
                        DataElementValue params = new DataElementValue();
                        params.put("operation", "playoff");
                        messagesDataStore.push(params);
//                    Toast.makeText(getApplicationContext(), "PlayOFF!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

//        KoTNotification.notify(this,"test",1);

        setTitle(R.string.title_connecting);
        new DoRead().execute(URL);
    }


    public void onResume() {
        if (DEBUG) Log.d(TAG, "onResume()");
        super.onResume();
        if (mv != null) {
            if (suspending) {
                new DoRead().execute(URL);
                suspending = false;
            }
        }

    }

    public void onStart() {
        if (DEBUG) Log.d(TAG, "onStart()");
        super.onStart();

        // Milkcocoa process
        this.milkcocoa = new MilkCocoa("vuej9n3jub4.mlkcca.com");
        this.messagesDataStore = this.milkcocoa.dataStore("KoT");

        this.messagesDataStore.addDataStoreEventListener(this);
        this.messagesDataStore.on("send");
        this.messagesDataStore.on("push");

    }

    public void onPause() {
        if (DEBUG) Log.d(TAG, "onPause()");
        super.onPause();
        if (mv != null) {
            if (mv.isStreaming()) {
                mv.stopPlayback();
                suspending = true;
            }
        }
    }

    public void onStop() {
        if (DEBUG) Log.d(TAG, "onStop()");
        super.onStop();
    }

    public void onDestroy() {
        if (DEBUG) Log.d(TAG, "onDestroy()");

        if (mv != null) {
            mv.freeCameraMemory();
        }

        super.onDestroy();
    }

    @SuppressLint("ResourceType")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.layout.option_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                Intent settings_intent = new Intent(MjpegActivity.this, SettingsActivity.class);
                settings_intent.putExtra("width", width);
                settings_intent.putExtra("height", height);
                settings_intent.putExtra("ip_ad1", ip_ad1);
                settings_intent.putExtra("ip_ad2", ip_ad2);
                settings_intent.putExtra("ip_ad3", ip_ad3);
                settings_intent.putExtra("ip_ad4", ip_ad4);
                settings_intent.putExtra("ip_port", ip_port);
                settings_intent.putExtra("ip_command", ip_command);
                startActivityForResult(settings_intent, REQUEST_SETTINGS);
                return true;
            case R.id.audiosettings:
                Intent audiosettings_intent = new Intent(MjpegActivity.this, AudioActivity.class);
                startActivity(audiosettings_intent);
                return true;
        }
        return false;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_SETTINGS:
                if (resultCode == Activity.RESULT_OK) {
                    width = data.getIntExtra("width", width);
                    height = data.getIntExtra("height", height);
                    ip_ad1 = data.getIntExtra("ip_ad1", ip_ad1);
                    ip_ad2 = data.getIntExtra("ip_ad2", ip_ad2);
                    ip_ad3 = data.getIntExtra("ip_ad3", ip_ad3);
                    ip_ad4 = data.getIntExtra("ip_ad4", ip_ad4);
                    ip_port = data.getIntExtra("ip_port", ip_port);
                    ip_command = data.getStringExtra("ip_command");

                    if (mv != null) {
                        mv.setResolution(width, height);
                    }
                    SharedPreferences preferences = getSharedPreferences("SAVED_VALUES", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putInt("width", width);
                    editor.putInt("height", height);
                    editor.putInt("ip_ad1", ip_ad1);
                    editor.putInt("ip_ad2", ip_ad2);
                    editor.putInt("ip_ad3", ip_ad3);
                    editor.putInt("ip_ad4", ip_ad4);
                    editor.putInt("ip_port", ip_port);
                    editor.putString("ip_command", ip_command);

                    editor.commit();

                    new RestartApp().execute();
                }
                break;
        }
    }

    public void setImageError() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                setTitle(R.string.title_imageerror);
                return;
            }
        });
    }

    @Override
    public void onPushed(DataElement dataElement) {
        final DataElement sended = dataElement;
        new Thread(new Runnable() {
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        // ここにオンラインメッセージが来た時のみ実行する処理を書く
                        String mes = sended.getValue("onlinestate");
                        if(mes != null){
//                            Toast.makeText(getApplicationContext(), mes, Toast.LENGTH_SHORT).show();
                            if(mes.contains("online") ){
                                stateText.setText("online");
                                onlinestate.setImageResource(R.drawable.kotonline);
//                                Toast.makeText(getApplicationContext(), "in", Toast.LENGTH_SHORT).show();
                                KoTNotification.notify(getApplicationContext(),"online",1);

                            }else{
                                stateText.setText("offline");
                                onlinestate.setImageResource(R.drawable.kotoffline);
//                                Toast.makeText(getApplicationContext(), "out", Toast.LENGTH_SHORT).show();

                            }
                        }
                        String mes2 = sended.getValue("music");
                        if(mes2 != null){
//                            Toast.makeText(getApplicationContext(), mes, Toast.LENGTH_SHORT).show();
                            if(mes2.contains("finish") ){
                                toggleflag = true;
                                onoff.setChecked(false);
                                Toast.makeText(getApplicationContext(), "music off", Toast.LENGTH_SHORT).show();
                            }
                        }

                    }
                });
            }
        }).start();
    }

    @Override
    public void onSetted(DataElement dataElement) {

    }

    @Override
    public void onSended(DataElement dataElement) {
        final DataElement sended = dataElement;
        new Thread(new Runnable() {
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        // ここにオンラインメッセージが来た時のみ実行する処理を書く
                        String mes = sended.getValue("onlinestate");
                        if(mes != null){
//                            Toast.makeText(getApplicationContext(), mes, Toast.LENGTH_SHORT).show();
                            if(mes.contains("online") ){
                                stateText.setText("online");
                                onlinestate.setImageResource(R.drawable.kotonline);
//                                Toast.makeText(getApplicationContext(), "in", Toast.LENGTH_SHORT).show();
                                KoTNotification.notify(getApplicationContext(),"online",1);

                            }else{
                                stateText.setText("offline");
                                onlinestate.setImageResource(R.drawable.kotoffline);
//                                Toast.makeText(getApplicationContext(), "out", Toast.LENGTH_SHORT).show();

                            }
                        }
                        String mes2 = sended.getValue("music");
                        if(mes2 != null){
//                            Toast.makeText(getApplicationContext(), mes, Toast.LENGTH_SHORT).show();
                            if(mes2.contains("finish") ){
                                toggleflag = true;
                                onoff.setChecked(false);
                                Toast.makeText(getApplicationContext(), "music off", Toast.LENGTH_SHORT).show();
                            }
                        }

                    }
                });
            }
        }).start();
    }

    @Override
    public void onRemoved(DataElement dataElement) {

    }

    public class DoRead extends AsyncTask<String, Void, MjpegInputStream> {
        protected MjpegInputStream doInBackground(String... url) {
            //TODO: if camera has authentication deal with it and don't just not work
            HttpResponse res = null;
            DefaultHttpClient httpclient = new DefaultHttpClient();
            HttpParams httpParams = httpclient.getParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, 5 * 1000);
            HttpConnectionParams.setSoTimeout(httpParams, 5 * 1000);
            if (DEBUG) Log.d(TAG, "1. Sending http request");
            try {
                res = httpclient.execute(new HttpGet(URI.create(url[0])));
                if (DEBUG)
                    Log.d(TAG, "2. Request finished, status = " + res.getStatusLine().getStatusCode());
                if (res.getStatusLine().getStatusCode() == 401) {
                    //You must turn off camera User Access Control before this will work
                    return null;
                }
                return new MjpegInputStream(res.getEntity().getContent());
            } catch (ClientProtocolException e) {
                if (DEBUG) {
                    e.printStackTrace();
                    Log.d(TAG, "Request failed-ClientProtocolException", e);
                }
                //Error connecting to camera
            } catch (IOException e) {
                if (DEBUG) {
                    e.printStackTrace();
                    Log.d(TAG, "Request failed-IOException", e);
                }
                //Error connecting to camera
            }
            return null;
        }

        protected void onPostExecute(MjpegInputStream result) {
            mv.setSource(result);
            if (result != null) {
                result.setSkip(1);
                setTitle(R.string.app_name);
            } else {
                setTitle(R.string.title_disconnected);
            }
            mv.setDisplayMode(MjpegView.SIZE_BEST_FIT);
            mv.showFps(false);
        }
    }

    public class RestartApp extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... v) {
            MjpegActivity.this.finish();
            return null;
        }

        protected void onPostExecute(Void v) {
            startActivity((new Intent(MjpegActivity.this, MjpegActivity.class)));
        }
    }

}
