package br.edu.catolica_to.templatecg;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static java.lang.Math.random;

//ESTA CLASSE IMPLEMENTA OS METODOS NECESSARIOS PARA
//UTILIZAR A BIBLIOTECA OPENGL NO DESENHO GRAFICO
//QUE SERA APRESENTADO NA TELA PELA SUPERFICIE DE DESENHO
class Renderizador implements GLSurfaceView.Renderer, View.OnTouchListener {


    private GL10 gl;
    private Triangulo tri;
    private Quadrado qua;
    private Paralelogramo para;
    private float esquerda = 0;
    private float direita = 0;
    private int posX = 0;
    private int posY = 0;
    private static int larguraX;
    private static int alturaY;

    private Buffer buffer;

    private float direcaoX = 1;
    private float direcaoY = 1;

    //objetos usados para realizar o arrasta e solta
    private Geometria objMove = null;
    private ArrayList<Geometria> vetorGeo = new ArrayList();
    private float angulo = 0;

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        gl.glClearColor(0.1f, 0, 0, 1);
    }

    //É chamado quando a superfície de desenho for alterada.
    @Override
    public void onSurfaceChanged(GL10 gl, int largura, int altura) {
        this.gl = gl;
        this.alturaY = altura;
        this.larguraX = largura;

        //CONFIGURACAOES DA VIEW PORT --------------------------------------------------------------

        //Configurando a área de cordenadas do plano cartesiano
        //MAtriz de projeção
        gl.glMatrixMode(gl.GL_PROJECTION);
        gl.glLoadIdentity();
        //Define o espaço de trabalho.
        //volume (CUBO 3D) de renderização - Tudo que for configurado dentro desse volume aparece na tela.
        //Definindo X - Y - Z , LARGURA - ALTURA - PROFUNDIDADE
        gl.glOrthof(0.0f, largura, 0.0f, altura, -1.0f, 1.0f);
        //OPENGL aponta para nova matriz (De transformações geométricas)
        //Translação, Rotação e Escala
        //Matriz de câmera
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();
        gl.glViewport(0, 0, largura, altura);

        //FIM --------------------------------------------------------------------------------------

        //CRIA UM QUADRADO
        qua = new Quadrado(gl);
        qua.setCor((float) random(), (float) random(), (float) random(), 1f);
        qua.setPosX(posX);
        qua.setPosY(posY);

        float[] co = new float[]{
                -100, -100,
                -100, 100,
                100, -100,
                100, 100
        };
//

        buffer = criaNIOBuffer(co);

    }

    public static FloatBuffer criaNIOBuffer(float[] coordenadas) {
        //Aloca a qtd de bytes necessárias para armazenar os dados dos vertices
        ByteBuffer vetBytes = ByteBuffer.allocateDirect(coordenadas.length * 4);

        //Usa o sistema de endereçamento de memória
        //nativo no processador em questão
        vetBytes.order(ByteOrder.nativeOrder());

        //cria o FloatBuffer a partir do byteBuffer
        FloatBuffer buffer = vetBytes.asFloatBuffer();

        //Limpa um eventual lixo de memória
        buffer.clear();

        //Encapsula o array java no objeto Float Buffer
        buffer.put(coordenadas);

        //Retira as sobras de memória
        buffer.flip();

        //Retorna o objeto de coordenadas
        return buffer;
    }

    @Override
    public void onDrawFrame(GL10 gl) {

        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);

        //limpa o vetor de cores
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
        //carrega a matriz identidade
        gl.glLoadIdentity();
        //realiza o movimento de translação em todos os vertices desenhados
        gl.glTranslatef(posX, posY, 0);
        //rotacao é a multiplicao do angulo por cada eixo
        gl.glRotatef(angulo, 0, 0, 1);

        //almento o angulo de rotacao
        angulo += 8;

        gl.glColor4f(1, 0, 0.2f, 1);
        gl.glVertexPointer(2, GL10.GL_FLOAT, 0, buffer);
        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);

        if (MainActivity.INCLINACAO_X > 2) {
            Log.i("RENDER", "esquerda : " + MainActivity.INCLINACAO_X);
            if (posX >= 100) {
                posX -= 6;
            }
        }
        if (MainActivity.INCLINACAO_X < -2) {
            Log.i("RENDER", "direita : " + MainActivity.INCLINACAO_X);
            if (posX + 100 <= larguraX) {
                posX += 6;
            }
        }
        if (MainActivity.INCLINACAO_Y < 0) {
            Log.i("RENDER", "ponta baixa : " + MainActivity.INCLINACAO_Y);
            if (posY + 100 <= alturaY) {
                posY += 6;
            }
        }
        if (MainActivity.INCLINACAO_Y > 3) {
            Log.i("RENDER", "ponta pra cima : " + MainActivity.INCLINACAO_Y);
            if (posY >= 100) {
                posY -= 6;
            }
        }

    }

    //PEGA O OBJ DE LOCALIZADO EM UM LUGAR NA TELA
    public Geometria getObjeto(int posX, int posY) {
        int pontoBase = 0;
        for (int i = 0; i < vetorGeo.size(); i++) {
            if (posX > vetorGeo.get(i).getPosX() - 100 / 2 && posY > vetorGeo.get(i).getPosY() - 100 / 2) {
                return vetorGeo.get(i);
            }
        }
        return null;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        //AO ARRASTAR
        if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
            //altera a translacao
            posX = (int) motionEvent.getX();
            posY = alturaY - (int) motionEvent.getY();
        }
        //AO CLICAR
        if (objMove == null) {
            if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                posX = (int) motionEvent.getX();
                posY = alturaY - (int) motionEvent.getY();
                Quadrado qua = new Quadrado(gl);
                qua.setPosX(posX);
                qua.setPosY(posY);
                qua.setCor((float) random(), (float) random(), (float) random(), 1f);
                //ADICIONA UM QUADRADO AO LIST DE GEOMETRICOS QUE VAI SER DESENHADO NA TELA
                vetorGeo.add(qua);

            }
        }
        //AO SOLTAR CLICK
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            posX = (int) motionEvent.getX();
            posY = alturaY - (int) motionEvent.getY();

            objMove = this.getObjeto(posX, posY);
        }
        return true;
    }
}