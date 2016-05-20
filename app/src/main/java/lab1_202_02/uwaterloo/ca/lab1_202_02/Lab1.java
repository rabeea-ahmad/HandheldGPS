package lab1_202_02.uwaterloo.ca.lab1_202_02;

import android.support.v7.app.AppCompatActivity;

import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Arrays;

public class Lab1 extends AppCompatActivity
{
    LineGraphView graph;
    TextView lightR;
    TextView accelR;
    TextView magneticR;
    TextView rotatR;
    TextView highAccel;
    TextView highMagnetic;
    TextView highRotat;
    Button reset;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lab1);
        LinearLayout lin = (LinearLayout) findViewById(R.id.linear);
        lin.setOrientation(LinearLayout.VERTICAL);
        lightR = new TextView(getApplicationContext());
        accelR=new TextView(getApplicationContext());
        magneticR =new TextView(getApplicationContext());
        rotatR =new TextView(getApplicationContext());
        highAccel = new TextView (getApplicationContext());
        highMagnetic = new TextView(getApplicationContext());
        highRotat = new TextView(getApplicationContext());
        reset = (Button) findViewById(R.id.button);
        accelR.setTextColor(Color.parseColor("#000000"));
        lightR.setTextColor(Color.parseColor("#000000"));
        magneticR.setTextColor(Color.parseColor("#000000"));
        rotatR.setTextColor(Color.parseColor("#000000"));
        highAccel.setTextColor(Color.parseColor("#000000"));
        highMagnetic.setTextColor(Color.parseColor("#000000"));
        highRotat.setTextColor(Color.parseColor("#000000"));

        SensorManager sensorManager = (SensorManager) getSystemService (SENSOR_SERVICE);
        Sensor lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        Sensor accelSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Sensor magneticSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        Sensor rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

        highAccel.setText("0,0,0");
        highMagnetic.setText("0,0,0");
        highRotat.setText("0,0,0");

        graph = new LineGraphView(getApplicationContext(),
                100,
                Arrays.asList("x", "y", "z"));
        lin.addView(graph);
        graph.setVisibility(View.VISIBLE);

        SensorEventListener l = new LightSensorEventListener(lightR);
        AccelerometerSensorEventListener a = new AccelerometerSensorEventListener(accelR, highAccel, graph);
        MagneticFieldSensorEventListener m = new MagneticFieldSensorEventListener (magneticR, highMagnetic);
        RotationVectorSensorEventListener r = new RotationVectorSensorEventListener(rotatR, highRotat);

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int x = 0; x < 3; x++) {
                    AccelerometerSensorEventListener.val[x] = "0";
                    MagneticFieldSensorEventListener.val[x] = "0";
                    RotationVectorSensorEventListener.val[x] = "0";
                }
            }
        });

        sensorManager.registerListener (l, lightSensor,SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(a, accelSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(m, magneticSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(r, rotationSensor, SensorManager.SENSOR_DELAY_NORMAL);

        lin.addView(lightR);
        lin.addView(accelR);
        lin.addView(magneticR);
        lin.addView (rotatR);
        lin.addView(highAccel);
        lin.addView(highMagnetic);
        lin.addView(highRotat);



    }
}

class LightSensorEventListener implements SensorEventListener
{
    TextView output;

    public LightSensorEventListener (TextView outputView)
    {
        output = outputView;
    }
    public void onAccuracyChanged (Sensor s, int i) {}
    public void onSensorChanged (SensorEvent se)
    {
        if (se.sensor.getType() == Sensor.TYPE_LIGHT)
        {
            output.setText("Light Sensor: " + Float.toString(se.values[0]));
        }
    }
}

class AccelerometerSensorEventListener implements SensorEventListener
{
    TextView output;
    TextView highest;
    LineGraphView line;
    static String [] val = new String [3];

    public AccelerometerSensorEventListener (TextView outputView, TextView highView, LineGraphView graph)
    {
        output = outputView;
        val=highView.getText().toString().split(",",3);
        highest=highView;
        line = graph;
    }
    public void onAccuracyChanged (Sensor s, int i) {}
    public void onSensorChanged (SensorEvent se)
    {
        if (se.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
        {
            String s1 = "Accelerometer: " + String.format("%f,%f,%f",se.values[0],se.values[1],se.values[2]);
            output.setText(s1);
            for (int x=0 ; x<3; x++)
            {
                if (Float.parseFloat(val[x]) < Math.abs(se.values[x]))
                    val[x] = Float.toString(Math.abs(se.values[x]));
            }
            String s2 = String.format("%s,%s,%s",val[0],val[1],val[2]);
            highest.setText(s2);
            line.addPoint(se.values);
        }
    }
}
class MagneticFieldSensorEventListener implements SensorEventListener
{
    TextView output;
    TextView highest;
    static String [] val = new String [3];

    public MagneticFieldSensorEventListener (TextView outputView, TextView highView)
    {
        output=outputView;
        val=highView.getText().toString().split(",",3);
        highest=highView;
    }
    public void onAccuracyChanged (Sensor s, int i) {}
    public void onSensorChanged (SensorEvent se)
    {
        if (se.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
        {
            String s = "Magnetic Field: " + String.format("%f,%f,%f",se.values[0],se.values[1],se.values[2]);
            output.setText(s);
            for (int x=0 ; x<3; x++)
            {
                if (Float.parseFloat(val[x]) < Math.abs(se.values[x]))
                    val[x] = Float.toString(Math.abs(se.values[x]));
            }
            String s2 = String.format("%s,%s,%s",val[0],val[1],val[2]);
            highest.setText(s2);
        }

    }
}
class RotationVectorSensorEventListener implements SensorEventListener
{
    TextView output;
    TextView highest;
    static String [] val = new String [3];

    public RotationVectorSensorEventListener (TextView outputView, TextView highView)
    {
        output=outputView;
        val=highView.getText().toString().split(",",3);
        highest=highView;
    }
    public void onAccuracyChanged (Sensor s, int i) {}
    public void onSensorChanged (SensorEvent se)
    {
        if (se.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR)
        {
            String s = "Rotation Vector: " + String.format("%f,%f,%f",se.values[0],se.values[1],se.values[2]);
            output.setText(s);

            for (int x=0 ; x<3; x++)
            {
                if (Float.parseFloat(val[x]) < Math.abs(se.values[x]))
                    val[x] = Float.toString(Math.abs(se.values[x]));
            }
            String s2 = String.format("%s,%s,%s",val[0],val[1],val[2]);
            highest.setText(s2);
        }
    }
}

