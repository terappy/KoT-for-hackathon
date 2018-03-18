package com.camera.kot_for_one;

import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;


import java.io.File;
import java.io.IOException;

public class AudioActivity extends AppCompatActivity implements OnCompletionListener{

    //再生
    private MediaPlayer mp;
//    private Boolean nowPlaying = false;

    //録音
    private MediaRecorder mediarecorder; //録音用のメディアレコーダークラス
    static final String directoryPath = Environment.getExternalStorageDirectory()+ "/userVoice/"; //録音用のディレクトリパス
    static String filePath = null; //録音用のファイルパス

//    private Boolean nowRecording = false;

    Button rec;
    Button stoprec;
    Button playsound;
    Button stopsound;

    String voiceName = "";              //保存ファイル名
    String extension = ".wav";          //拡張子の設定


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);

        final Intent intent = new Intent();

        RadioGroup selectVoiceList = (RadioGroup) findViewById(R.id.selectvoice);

        Button back = (Button)findViewById(R.id.audioback);
        rec = (Button)findViewById(R.id.recordvoice);
        stoprec = (Button)findViewById(R.id.stoprecord);
        playsound = (Button)findViewById(R.id.playsound);
        stopsound = (Button)findViewById(R.id.stopsound);

        Button send = (Button)findViewById(R.id.hogehoge);

        rec.setEnabled(true);
        stoprec.setEnabled(false);
        playsound.setEnabled(true);
        stopsound.setEnabled(false);

        voiceName = "/yobikake" + extension;

        selectVoiceList.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.yobikake:
                        voiceName = "/yobikake" + extension;
                        break;
                    case R.id.kakegoe:
                        voiceName = "/kakegoe" + extension;
                        break;
                    case R.id.homeru:
                        voiceName = "/homeru" + extension;
                        break;
                    case R.id.sayonara:
                        voiceName = "/sayonara" + extension;
                        break;
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        rec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startMediaRecord();
            }
        });
        stoprec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopRecord();
            }
        });
        playsound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPlay();
            }
        });
        stopsound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopPlay();
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Sent!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void startPlay() {
//        if(!nowPlaying) {
            File dir = new File(directoryPath);
            if (!dir.exists()) {
                Toast.makeText(AudioActivity.this, "directory not exist", Toast.LENGTH_SHORT).show();
                return;
            }
            filePath = dir.getAbsolutePath() + voiceName;
            File mediafile = new File(filePath);
            if (!mediafile.exists()) {
                Toast.makeText(AudioActivity.this, "voice data not exist", Toast.LENGTH_SHORT).show();
                return;
            }

            mp = new MediaPlayer();
            try {
                mp.setDataSource(filePath);
                mp.prepare();
                mp.start();
                mp.setOnCompletionListener(this);
                Toast.makeText(AudioActivity.this, "再生開始", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
//                nowPlaying = true;
                rec.setEnabled(false);
                stoprec.setEnabled(false);
                playsound.setEnabled(false);
                stopsound.setEnabled(true);
            }
//        }
    }

    // 停止
    private void stopPlay() {
//        if(mp != null && nowPlaying){
            try {
                mp.stop();
                mp.prepare();
                mp.release();
                Toast.makeText(AudioActivity.this, "停止", Toast.LENGTH_SHORT).show();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
//                nowPlaying = false;
                rec.setEnabled(true);
                stoprec.setEnabled(false);
                playsound.setEnabled(true);
                stopsound.setEnabled(false);
            }
//        }
    }
    @Override
    public void onCompletion(MediaPlayer arg0) {
        Toast.makeText(AudioActivity.this, "停止", Toast.LENGTH_SHORT).show();
        rec.setEnabled(true);
        stoprec.setEnabled(false);
        playsound.setEnabled(true);
        stopsound.setEnabled(false);
    }


    private void startMediaRecord(){
//        if(!nowRecording) {
            try {
                File dir = new File(directoryPath);
                if (!dir.exists()) {
                    dir.mkdir();
                }
                filePath = dir.getAbsolutePath() + voiceName;
                File mediafile = new File(filePath);
                if (mediafile.exists()) {
                    mediafile.delete();
                    Toast.makeText(AudioActivity.this, "delete", Toast.LENGTH_SHORT).show();
                }

                mediarecorder = new MediaRecorder();
                //マイクからの音声を録音する
                mediarecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                //ファイルへの出力フォーマット DEFAULTにするとwavが扱えるはず
                mediarecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
                //音声のエンコーダーも合わせてdefaultにする
                mediarecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
                //ファイルの保存先を指定
                mediarecorder.setOutputFile(filePath);
                //録音の準備をする
                mediarecorder.prepare();
                //録音開始
                mediarecorder.start();
                Toast.makeText(AudioActivity.this, "録音開始しました", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
//                nowRecording = true;
                rec.setEnabled(false);
                stoprec.setEnabled(true);
                playsound.setEnabled(false);
                stopsound.setEnabled(false);
            }
//        }
    }

    //停止
    private void stopRecord(){
        if(mediarecorder == null){
            Toast.makeText(AudioActivity.this, "mediarecorder = null", Toast.LENGTH_SHORT).show();
        }else{
            try{
                //録音停止
                mediarecorder.stop();
                mediarecorder.reset();
                mediarecorder.release();
                Toast.makeText(AudioActivity.this, "録音停止", Toast.LENGTH_SHORT).show();
            }catch (Exception e){
                e.printStackTrace();
            }finally {
//                nowRecording = false;
                rec.setEnabled(true);
                stoprec.setEnabled(false);
                playsound.setEnabled(true);
                stopsound.setEnabled(false);
            }
        }
    }
}
