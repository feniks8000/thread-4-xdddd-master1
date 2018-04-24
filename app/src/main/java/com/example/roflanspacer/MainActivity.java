package com.example.roflanspacer;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.Hashtable;

public class MainActivity extends Activity implements View.OnTouchListener {
      public static   TextView textView;
    private static final Hashtable<String, Typeface> cache = new Hashtable<String, Typeface>();
    private static final int PERMISSION_REQUEST_CODE = 123;
    Thread thread;
    Thread getThread;
    Handler handler;

   public static ImageView imageView;

    private SurfaceHolder surfaceHolder;

    public static boolean isLeftPressed = false;
    public static boolean isRightPressed = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SurfaceView surfaceView = new GameView(this);
        LinearLayout gameLayout = (LinearLayout) findViewById(R.id.gameLayout);
        gameLayout.addView(surfaceView);
        imageView=(ImageView)findViewById(R.id.gg);
        Button leftButton = (Button) findViewById(R.id.leftButton);
        Button rightButton = (Button) findViewById(R.id.rightButton);
        leftButton.setOnTouchListener(this);
        rightButton.setOnTouchListener(this);
        textView = (TextView)findViewById(R.id.moneys);
        //startService(new Intent(this, Music.class));
        if (hasPermissions()){
            // our app has permissions.
            makeFolder();

        }
        else {
            //our app doesn't have permissions, So i m requesting permissions.
            requestPermissionWithRationale();


    }}

        public static Typeface get(Context c, String name) {
            synchronized (cache) {
                if (!cache.containsKey(name)) {
                    String path = "fonts/" + name;
                    try {
                        Typeface t = Typeface.createFromAsset(c.getAssets(), path);
                        cache.put(name, t);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return cache.get(name);
            }
        }


    @Override
    protected void onStop() {

        super.onStop();
    }


    public boolean onTouch(View button, MotionEvent motion) {

        switch(button.getId()) {
            case R.id.leftButton:
                switch (motion.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        isLeftPressed = true;
                        break;
                    case MotionEvent.ACTION_UP:
                        isLeftPressed = false;
                        break;
                }
                break;
            case R.id.rightButton:
                switch (motion.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        isRightPressed = true;
                        break;
                    case MotionEvent.ACTION_UP:
                        isRightPressed = false;
                        break;
                }
                break;
        }
        return true;
    }

    private void makeFolder(){
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),"fandroid");
//
       // if (!file.exists()){
       //     Boolean ff = file.mkdir();
       //     if (ff){
       //         Toast.makeText(MainActivity.this, "Folder created successfully", Toast.LENGTH_SHORT).show();
       //     }
       //     else {
       //         Toast.makeText(MainActivity.this, "Failed to create folder", Toast.LENGTH_SHORT).show();
       //     }
//
       // }
       // else {
       //     Toast.makeText(MainActivity.this, "Folder already exist", Toast.LENGTH_SHORT).show();
       // }
    }

    @SuppressLint("WrongConstant")
    private boolean hasPermissions(){
       int res = 0;
        //string array of permissions,
        String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        for (String perms : permissions){
            res = checkCallingOrSelfPermission(perms);
            if (!(res == PackageManager.PERMISSION_GRANTED)){
                return false;
            }
        }
        return true;
    }

    private void requestPerms(){
        String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            requestPermissions(permissions,PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int  requestCode, @NonNull String[] permissions,@NonNull int[] grantResults) {
        boolean allowed = true;

        switch (requestCode){
            case PERMISSION_REQUEST_CODE:

                for (int res : grantResults){
                    // if user granted all permissions.
                    allowed = allowed && (res == PackageManager.PERMISSION_GRANTED);
                }

                break;
            default:
                // if user not granted permissions.
                allowed = false;
                break;
        }

        if (allowed){
            //user granted all permissions we can perform our task.
            makeFolder();
        }
        else {
            // we will give warning to user that they haven't granted permissions.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                   // Toast.makeText(this, "Storage Permissions denied.", Toast.LENGTH_SHORT).show();

                } else {
                    showNoStoragePermissionSnackbar();
                }
            }
        }

    }



    public void showNoStoragePermissionSnackbar() {
        Snackbar.make(MainActivity.this.findViewById(R.id.activity_main), "Storage permission isn't granted" , Snackbar.LENGTH_LONG)
                .setAction("SETTINGS", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openApplicationSettings();

                        Toast.makeText(getApplicationContext(),
                                "Open Permissions and grant the Storage permission",
                                Toast.LENGTH_SHORT)
                                .show();
                    }
                })
                .show();
    }

    public void openApplicationSettings() {
        Intent appSettingsIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:" + getPackageName()));
        startActivityForResult(appSettingsIntent, PERMISSION_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            makeFolder();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    public void requestPermissionWithRationale() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)) {
            final String message = "Storage permission is needed to show files count";
            Snackbar.make(MainActivity.this.findViewById(R.id.activity_main), message, Snackbar.LENGTH_LONG)
                    .setAction("GRANT", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            requestPerms();
                        }
                    })
                    .show();
        } else {
            requestPerms();
        }
    }

    @Override
    protected void onDestroy() {
        stopService(new Intent(this, Music.class));
        super.onDestroy();
    }
    public void onShopClick(View view) {
        if (GameView.count > 15) {

            GameView.count -= 15;
            GameView.life += 20;
        }
    }

    public void onggClick(View view) {
        Intent i = new Intent( this , this.getClass() );
        finish();
        this.startActivity(i);
        GameView.firstTime = true;
        GameView.gameRunning = true;
        GameView.count = 0;
        GameView.life = 100;

    }
}



