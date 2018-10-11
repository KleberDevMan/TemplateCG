package br.edu.catolica_to.templatecg;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends Activity implements SensorEventListener {

    GLSurfaceView superficieDesenho = null;
    Renderizador render = null;

    //ACELEROMETRO
    Sensor acelerometro;
    SensorManager sensorManager;

    //INCLINACAO
    public static float INCLINACAO_X;
    public static float INCLINACAO_Y;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //valida a var de referencia para a superficie
        superficieDesenho = new GLSurfaceView(this);

        //Valida a var de referencia para o renderizador
        render = new Renderizador(this);

        //Associa o renderizador a superficie de desenho da tela.
        superficieDesenho.setRenderer(render);

        superficieDesenho.setOnTouchListener(render);

        //Publica a sup de desenho na tela do app
        setContentView(superficieDesenho);

        //FAZENDO REFERENCIA AOS SERVICOS DE SESSORES
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        acelerometro = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }


    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, acelerometro, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this, acelerometro);
    }

    //TODA VEZ QUE OCORRE UMA MUDANÇA NO ACELEROMETRO
    @Override
    public void onSensorChanged(SensorEvent event) {

//        float sensorX = event.values[0];
//        float sensorY = event.values[1];
//        float sensorZ = event.values[2];

        this.INCLINACAO_X = event.values[0];
        this.INCLINACAO_Y = event.values[1];
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
