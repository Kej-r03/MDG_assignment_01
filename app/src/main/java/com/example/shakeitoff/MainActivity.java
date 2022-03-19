package com.example.shakeitoff;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements SensorEventListener
{

    private TextView colorbox,colorbox2,msg;
    private SensorManager sensorManager;
    private Sensor accelerometerSensor;
    private double currentX, currentY, currentZ, lastX, lastY, lastZ, xDifference,yDifference, zDifference, delA;
    private double shakeThreshold=5f;
    private static int c=0,flag=0;
    private Vibrator vib;
    private CameraManager cm;
    private ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        colorbox=findViewById(R.id.colorbox);
        colorbox2=findViewById(R.id.colorbox2);
        img=findViewById(R.id.imageView);
        msg=findViewById(R.id.message);

    sensorManager=(SensorManager) getSystemService(SENSOR_SERVICE);
    accelerometerSensor=sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    vib=(Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    cm=(CameraManager) getSystemService(Context.CAMERA_SERVICE);
}
@Override
public void onSensorChanged(SensorEvent sensorEvent) {

        if (flag <=3)
        flag ++;
    else {
        lastX = currentX;
        lastY = currentY;
        lastZ = currentZ;

        currentX = sensorEvent.values[0];
        currentY = sensorEvent.values[1];
        currentZ = sensorEvent.values[2];

        xDifference = currentX - lastX;
        yDifference = currentY - lastY;
        zDifference = currentZ - lastZ;

        delA = Math.sqrt(xDifference * xDifference + yDifference * yDifference + zDifference * zDifference);

        if (delA > shakeThreshold) {
            c++;
            int b = 525, l = RelativeLayout.LayoutParams.MATCH_PARENT;
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(b, l);
            if (c % 2 == 1) {
                lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            } else {
                lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            }
            colorbox.setLayoutParams(lp);

            RelativeLayout.LayoutParams lp2 = new RelativeLayout.LayoutParams(b, l);
            if (c % 2 == 0) {
                lp2.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            } else {
                lp2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            }
            colorbox2.setLayoutParams(lp2);

            MediaPlayer music = MediaPlayer.create(MainActivity.this, R.raw.beep);
            music.start();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vib.vibrate(VibrationEffect.createOneShot(250, VibrationEffect.DEFAULT_AMPLITUDE));
            }
            img.setImageResource(R.drawable.skull);
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    cm.setTorchMode(cm.getCameraIdList()[0], true);
                }
            }
            catch(CameraAccessException e)
            {
                e.printStackTrace();
            }
        }
        else {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    cm.setTorchMode(cm.getCameraIdList()[0], false);
                }
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
            img.setImageDrawable(null);
        }
        }
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
    @Override
    protected void onResume() {
        super.onResume();
sensorManager.registerListener(this, accelerometerSensor,SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }
}