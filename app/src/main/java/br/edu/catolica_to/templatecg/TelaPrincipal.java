package br.edu.catolica_to.templatecg;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
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
    private int posX = 0;
    private int posY = 0;
    private static int larguraX;
    private static int alturaY;

    private Buffer buffer;

    boolean aumentandoAngulo = false;

    //variaveis usadas para usar textura
    Activity vrActivity = null;
    private int codTextura;

    public Renderizador(Activity vrActivity) {
        this.vrActivity = vrActivity;
    }

    //objetos usados para realizar o arrasta e solta
    private Geometria objMove = null;
    private ArrayList<Geometria> vetorGeo = new ArrayList();
    private float angulo = 0;

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        gl.glClearColor(0.1f, 0, 0, 1);
//        gl.glClearColor(0.181f,0.165f,0.66f, 1);
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

        //HABILITA POSSIBILIDADE PARA ARRAY DE TEXTURA (TEXTURA)
        gl.glEnable(GL10.GL_TEXTURE_2D);
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

        //HABILITA POSSIBILIDADE PARA ARRAY DE VERTICES (POLIGNO)
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);

        gl.glEnable(GL10.GL_BLEND);
        gl.glEnable(GL10.GL_ALPHA_TEST);
        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_DST_ALPHA);

        //OPENGL aponta para nova matriz (De transformações geométricas)
        //Translação, Rotação e Escala
        //Matriz de câmera
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();
        gl.glViewport(0, 0, largura, altura);

        //FIM --------------------------------------------------------------------------------------

        // ----- CRIA TEXTURA ----------------------------------------------------------------
        float[] vetCoordText = {
                1f, 1f,
                1, 0,
                0f, 1f,
                0f, 0
        };
        FloatBuffer coordenadasTextura = criaNIOBuffer(vetCoordText);
        //REGISTRA AS COORDENADAS DA TEXTURA NA MAQUINA
        gl.glTexCoordPointer(2,
                GL10.GL_FLOAT,
                0,
                coordenadasTextura);
        codTextura = carregaTextura(gl, R.mipmap.pantera);

        // ----- CRIA POLIGNO ----------------------------------------------------------------
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
        buffer = criaNIOBuffer(co);
    }

    //METODO UTILIZADO PARA CARREGAR UMA TEXTURA
    int carregaTextura(GL10 openGl, int codTextura) {

        //CARREGA A IMAGEM NA MEMORIA RAM
        Bitmap imagem = BitmapFactory
                .decodeResource(vrActivity.getResources(), codTextura);

        //DEFINE UM ARRAY PARA ARMAZ. DOS IDS DE TEXTURA
        int[] idTextura = new int[1];

        //GERA AS AREAS NA GPU E CRIA UM ID PARA CADA UMA
        //***QUANTOS AREAS DE TEXTURAS, VETOR DA TEXTURA, DE QUAL POSICAO O ARRAY COMECA A SER LIDO***
        openGl.glGenTextures(1, idTextura, 0);

        //APONTA A MAQUINA OPENGL PARA UMAS DAS AREAS DE MEMORIAS CRIADAS NA GPU
        openGl.glBindTexture(GL10.GL_TEXTURE_2D, idTextura[0]);

        //COPIAR A IMAGEM DA RAM PARA A VRAM
        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, imagem, 0);

        //CONFIGURANDO A MUDANÇA DE PROPORÇÃO DA IMAGEM ( MINIMIZAR )
        openGl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);

        //CONFIGURANDO A MUDANÇA DE PROPORÇÃO DA IMAGEM ( MAXIMIZAR )
        openGl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_NEAREST);

        //APONTA A VRAM OPENGL PARA O NADA
        openGl.glBindTexture(GL10.GL_TEXTURE_2D, 0);

        //DELETA A IMAGEM CARREGADA NA RAM
        imagem.recycle();

        return idTextura[0];
    }

    //METODO USADO PARA ME DEVOLVER UMA DAS COORDENADAS DA TEXTURA
    void mudarIndiceDaTextura(int indice, boolean inverte) {

        float[] indice0 = null;
        float[] indice1 = null;
        float[] indice2 = null;
        float[] indice3 = null;
        float[] indice4 = null;
        float[] indice5 = null;
        float[] indice6 = null;
        float[] indice7 = null;

        if (!inverte){
            indice0 = new float[]{
                    0f, 0.25f,
                    0, 0,
                    0.5f, 0.25f,
                    0.5f, 0
            };
            indice1 = new float[]{
                    0.5f, 0.25f,
                    0.5f, 0,
                    1, 0.25f,
                    1, 0
            };
            indice2 = new float[]{
                    0f, 0.5f,
                    0, 0.25f,
                    0.5f, 0.5f,
                    0.5f, 0.25f
            };
            indice3 = new float[]{
                    0.5f, 0.5f,
                    0.5f, 0.25f,
                    1, 0.5f,
                    1, 0.25f
            };
            indice4 = new float[]{
                    0f, 0.75f,
                    0, 0.5f,
                    0.5f, 0.75f,
                    0.5f, 0.5f
            };
            indice5 = new float[]{
                    0.5f, 0.75f,
                    0.5f, 0.5f,
                    1, 0.75f,
                    1, 0.5f
            };
            indice6 = new float[]{
                    0, 1,
                    0, 0.75f,
                    0.5f, 1,
                    0.5f, 0.75f
            };
            indice7 = new float[]{
                    0.5f, 1,
                    0.5f, 0.75f,
                    1, 1,
                    1, 0.75f
            };
        }
        else{
            indice0 = new float[]{
                    1f, 0.25f,
                    1, 0,
                    0.5f, 0.25f,
                    0.5f, 0
            };
            indice1 = new float[]{
                    0.5f, 0.25f,
                    0.5f, 0,
                    0, 0.25f,
                    0, 0
            };
            indice2 = new float[]{
                    1f, 0.5f,
                    1, 0.25f,
                    0.5f, 0.5f,
                    0.5f, 0.25f
            };
            indice3 = new float[]{
                    0.5f, 0.5f,
                    0.5f, 0.25f,
                    0, 0.5f,
                    0, 0.25f
            };
            indice4 = new float[]{
                    1f, 0.75f,
                    1, 0.5f,
                    0.5f, 0.75f,
                    0.5f, 0.5f
            };
            indice5 = new float[]{
                    0.5f, 0.75f,
                    0.5f, 0.5f,
                    0, 0.75f,
                    1, 0.5f
            };
            indice6 = new float[]{
                    1, 1,
                    1, 0.75f,
                    0.5f, 1,
                    0.5f, 0.75f
            };
            indice7 = new float[]{
                    0.5f, 1,
                    0.5f, 0.75f,
                    0, 1,
                    0, 0.75f
            };
        }


        List<FloatBuffer> buffers = new ArrayList<>();

        buffers.add(criaNIOBuffer(indice0));
        buffers.add(criaNIOBuffer(indice1));
        buffers.add(criaNIOBuffer(indice2));
        buffers.add(criaNIOBuffer(indice3));
        buffers.add(criaNIOBuffer(indice4));
        buffers.add(criaNIOBuffer(indice5));
        buffers.add(criaNIOBuffer(indice6));
        buffers.add(criaNIOBuffer(indice7));

        switch (indice) {
            case 0:
                //REGISTRA AS COORDENADAS DA TEXTURA NA MAQUINA
                gl.glTexCoordPointer(2,
                        GL10.GL_FLOAT,
                        0,
                        buffers.get(0));
                break;
            case 1:
                //REGISTRA AS COORDENADAS DA TEXTURA NA MAQUINA
                gl.glTexCoordPointer(2,
                        GL10.GL_FLOAT,
                        0,
                        buffers.get(1));
                break;
            case 2:
                //REGISTRA AS COORDENADAS DA TEXTURA NA MAQUINA
                gl.glTexCoordPointer(2,
                        GL10.GL_FLOAT,
                        0,
                        buffers.get(2));
                break;
            case 3:
                //REGISTRA AS COORDENADAS DA TEXTURA NA MAQUINA
                gl.glTexCoordPointer(2,
                        GL10.GL_FLOAT,
                        0,
                        buffers.get(3));
                break;
            case 4:
                //REGISTRA AS COORDENADAS DA TEXTURA NA MAQUINA
                gl.glTexCoordPointer(2,
                        GL10.GL_FLOAT,
                        0,
                        buffers.get(4));
                break;
            case 5:
                //REGISTRA AS COORDENADAS DA TEXTURA NA MAQUINA
                gl.glTexCoordPointer(2,
                        GL10.GL_FLOAT,
                        0,
                        buffers.get(5));
                break;
            case 6:
                //REGISTRA AS COORDENADAS DA TEXTURA NA MAQUINA
                gl.glTexCoordPointer(2,
                        GL10.GL_FLOAT,
                        0,
                        buffers.get(6));
                break;
            case 7:
                //REGISTRA AS COORDENADAS DA TEXTURA NA MAQUINA
                gl.glTexCoordPointer(2,
                        GL10.GL_FLOAT,
                        0,
                        buffers.get(7));
                break;
            default:
                break;
        }

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

    long inicio = System.currentTimeMillis();
    int indiceDaTextura = 0;
    int direcao = 1;

    boolean inverte = false;

    @Override
    public void onDrawFrame(GL10 gl) {

        //limpa o vetor de cores
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
        //carrega a matriz identidade
        gl.glLoadIdentity();

        //----- DESENHANDO QUADRADO ---------
        //TRANSLACAO
        gl.glTranslatef(posX, posY, 0);
        //ROTACAO
        gl.glRotatef(angulo, 0, 0, 1);
        //TEXTURA
        gl.glBindTexture(GL10.GL_TEXTURE_2D, codTextura);
        //CORDENADAS
        gl.glVertexPointer(2, GL10.GL_FLOAT, 0, buffer);
        //DESENHA
        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);

        if (System.currentTimeMillis() - inicio > 100) {
            if (indiceDaTextura == 7) {
                indiceDaTextura = 0;
            }

            mudarIndiceDaTextura(indiceDaTextura, inverte);
            indiceDaTextura++;

            inicio = System.currentTimeMillis();
        }

        //AUMENTA A TRANSLACAO
        posX += direcao * 5;

        //MUDA A DIRACAO DA TRANSLACAO X
        if (posX + 100 >= larguraX ){
            direcao *= -1;
            inverte = true;
        }
        //MUDA A DIRACAO DA TRANSLACAO X
        if (posX <= 100){
            direcao *= -1;
            inverte = false;
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