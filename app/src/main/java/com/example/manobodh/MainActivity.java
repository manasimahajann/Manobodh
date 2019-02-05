package com.example.manobodh;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.media.Image;
import android.media.MediaPlayer;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    ListView listView;
    List<String> list;
    List<String> list2;

    int [] resIDList = new int[210];
    Toast toastC;
    Toast toastIfNumberInvalid;
    ListAdapter adapter;
    MediaPlayer mediaPlayer = null;
    FloatingActionButton fabPause;

    FloatingActionButton fabstopThreadButton;
    final Field[] fields = R.raw.class.getFields();

    AlertDialog alertDialogForContinuousPlay;
    EditText editTextCont;
    SecondThread thread;

    TextView playPause;
    ImageButton exitButton;

    boolean threadStatus = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//settool bar, in this make changes to manifest and styles --- NoActionBar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        toolbar.setBackgroundColor(Integer.parseInt("@color/colorPrimary"));
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        setSupportActionBar(toolbar);



        listView = (ListView) findViewById(R.id.listView);

        fabPause = (FloatingActionButton) findViewById(R.id.floatingActionButtonPause);
        fabstopThreadButton = (FloatingActionButton) findViewById(R.id.stopThreadButton);
        playPause = (TextView)findViewById(R.id.textViewPause);

        exitButton = (ImageButton) findViewById(R.id.exitButton);

        // creating this variable for easy read on the list [files in raw folder reads a001_ganadheesh_jo whereas when you see on the listview on the phone you see 1_ganadheesh_jo]
        list = new ArrayList<>();
        list2 = new ArrayList<>();

        //if user enters any special values and clicks submit in the pop-up which comes up when one clicks on "Play!"
        toastC = Toast.makeText(getApplicationContext(), "Please enter integer value between 1 to 205 to start from a specific Shlok", Toast.LENGTH_LONG);
        toastIfNumberInvalid = Toast.makeText(getApplicationContext(), "The verses are from 1 to 205, choose any number between 1 to 205", Toast.LENGTH_LONG);

        //exit the app

        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer != null) {

                    //Log.i("running", String.valueOf(position));
                    mediaPlayer.release();
                    mediaPlayer = null;
                    playPause.setText("Play?");
                }
                moveTaskToBack(true);
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(1);

            }
        });



        for (int i = 0; i < fields.length; i++) {
            //list2 to play correct song acc to the name on the file
            list2.add(String.valueOf(fields[i].getName()));
                String stringtoModify;

                stringtoModify = String.valueOf(i) + fields[i].getName().substring(4);
                //modified name of the file for user to understand easily
                list.add(stringtoModify);

                //          Log.i("running", String.valueOf(fields[i].getName()));

        }


        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                if (mediaPlayer != null) {

                    //Log.i("running", String.valueOf(position));
                    mediaPlayer.release();
                    mediaPlayer = null;
                    playPause.setText("Play?");
                }

                playPause.setText("Pause?");
                int resID = getResources().getIdentifier(list2.get(position), "raw", getPackageName());

                mediaPlayer = MediaPlayer.create(MainActivity.this, resID);
                mediaPlayer.start();

                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        if(thread != null && thread.isAlive() && mediaPlayer!=null) {
                            thread.interrupt();
                            Log.d(TAG, "mediaplayer1 inside list view" + mediaPlayer);
                            mediaPlayer.stop();
                         //   mediaPlayer.release();
                            mediaPlayer = null;
                        }
                        if(mediaPlayer!=null)
                        {
                            mediaPlayer.stop();
                            mediaPlayer = null;
                        }
                    }
                });
            }
/*
            private void playFromhere( int position) {
                try{
                    for (int count = position; count < fields.length; count++) {
                        mediaPlayer = MediaPlayer.create(MainActivity.this, getResources().getIdentifier(list.get(count), "raw", getPackageName()));
                        Log.i("Duration", String.valueOf(mediaPlayer.getDuration()));
                        mediaPlayer.start();
                        Thread.sleep(mediaPlayer.getDuration());
                    }

                }catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
*/

        });


        fabPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(mediaPlayer!=null) {
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                        playPause.setText("Play?");

                    } else {
                        mediaPlayer.start();
                        playPause.setText("Pause?");
                    }
                }
                else
                {
                    Toast toast = Toast.makeText(getApplicationContext(), "Please select some entity from the list",Toast.LENGTH_LONG);
                    toast.show();
                }


            }
        });

        // pop-up alert box when user clicks on "Play!"

        AlertDialog.Builder builderCont = new AlertDialog.Builder(MainActivity.this);
        builderCont.setTitle("Integer value only");
        builderCont.setIcon(R.drawable.ic_launcher_foreground);

        builderCont.setMessage("Please enter the verse you want to play from");
        editTextCont = new EditText(MainActivity.this);
        editTextCont.setRawInputType(Configuration.KEYBOARD_12KEY);
        builderCont.setView(editTextCont);

        builderCont.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String inputValue = editTextCont.getText().toString();
                Log.d(TAG, "onClick: Here " + inputValue);

                try {
                        playPause.setText("Pause?");
                        // so that when user puts in info again it does not overlap with earlier thread
                    if (mediaPlayer != null) {
                        mediaPlayer.stop();
                        mediaPlayer.release();
                        mediaPlayer = null;
                        if(threadStatus == true){
                            threadStatus = false;
                          //  thread.interrupt();
                          //  mediaPlayer.stop();
                        }
                        Log.d(TAG, "Thread" + "Cancelled " + Integer.parseInt(inputValue));
                    }

                    if((Integer.parseInt(inputValue) > -1) && (Integer.parseInt(inputValue)<206)) {
                            threadStatus = true;
                        Log.d(TAG, "mediaplayer1 inside for" + mediaPlayer);
                            if(threadStatus == true) {
                                if(thread != null && thread.isAlive() && mediaPlayer!=null) {
                                    thread.interrupt();
                                    Log.d(TAG, "mediaplayer1 inside thread!=null" + mediaPlayer);
                                    mediaPlayer.stop();
                                        mediaPlayer = null;
                                }
                                thread = new SecondThread(editTextCont.getText().toString());
                                Log.d(TAG, "onClick: Thread" + inputValue);
                                thread.start();
                            }
                        }
                        else
                        {
                            toastIfNumberInvalid.show();
                        }
                    }
                catch (NumberFormatException e)
                    {

                        toastC.show();

                    }

            }
        });
        builderCont.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertDialogForContinuousPlay = builderCont.create();

        fabstopThreadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alertDialogForContinuousPlay.show();
            }
        });
    }


//New class
    class SecondThread extends Thread {

        String valuefromAlert;
        //constructor
        SecondThread( String val) {
            this.valuefromAlert = val;
        }

        @Override
        public void run() {
            mediaPlayer = null;
            int startFromValue = Integer.parseInt(valuefromAlert);

            Log.d(TAG, "run: " + thread.getState());
            //release the verse which was first selected and start from the value entered


            //Start from tbe value entered till the end
            for(int count = startFromValue; count < fields.length; count++)
            {
                try
                {
                    resIDList[count] = fields[count].getInt(fields[count]);
                }
                catch (IllegalAccessException e)
                {
                    e.printStackTrace();
                }

                mediaPlayer = MediaPlayer.create(MainActivity.this, resIDList[count]);
              //  Log.i("Duration", String.valueOf(mediaPlayer.getDuration()));
                mediaPlayer.start();
                try {
                    Thread.sleep(mediaPlayer.getDuration());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }


        }
    }
}
/*
        Log.i("adfad", String.valueOf(resIDList[count]));
        MediaPlayer m = MediaPlayer.create(MainActivity.this, tracks[0]);
        m.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mcompleted++;
                mp.reset();
                if(mcompleted<tracks.length) {
                    {
                        try{
                            AssetFileDescriptor afd = getResources().openRawResourceFd(tracks[mcompleted]);
                            if(afd != null)
                            {
                                mp.setDataSource(afd.getFileDescriptor(),afd.getStartOffset(),afd.getLength());
                                afd.close();
                                mp.prepare();
                                mp.start();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                else if(mcompleted >= tracks.length)
                {
                    mcompleted=0;
                    try{
                        AssetFileDescriptor afd = getResources().openRawResourceFd(tracks[mcompleted]);
                        if(afd != null)
                        {
                            mp.setDataSource(afd.getFileDescriptor(),afd.getStartOffset(),afd.getLength());
                            afd.close();
                            mp.prepare();
                            mp.start();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else
                {
                    mcompleted = 0;
                    mp.release();
                    mp = null;
                }

            }
        });
*/
