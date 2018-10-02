package br.edu.catolica_to.templatecg;

import android.opengl.GLSurfaceView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

//ESTA CLASSE IMPLEMENTA OS METODOS NECESSARIOS PARA
//UTILIZAR A BIBLIOTECA OPENGL NO DESENHO GRAFICO
//QUE SERA APRESENTADO NA TELA PELA SUPERFICIE DE DESENHO
class Renderizador implements GLSurfaceView.Renderer, View.OnTouchListener {


    //vetor de coordenadas
    FloatBuffer buffer1;

    //OBJETO DO OPENGL PASSADO PARA OS OBJETO GEOMETRICOS
    GL10 gl;

    float largura = 0;
    float altura = 0;

    //TAMANHOS PEQUENOS (ESTATICOS)
    int tamanhoEstatico = 100;

    //TRANSLACAO
    float posX = 0; //VARIAVEL USADA PARA FAZER A TRANSLACAO NO EIXO X
    float direcaoHorizontal = 1; //VARIAVEL USADA PARA FAZER A TRANSLACAO NO EIXO X

    float posY = 0; //VARIAVEL USADA PARA FAZER A TRANSLACAO NO EIXO Y
    float direcaoVertical = 1; //VARIAVEL USADA PARA FAZER A TRANSLACAO NO EIXO Y

    //ROTACAO
    float angulo = 0;

    //LISTA USADA PARA CRIAR OBJETOS DINAMINCAMENTE COM UM CLICK
    List<Geometria> objetosGeometricos = new ArrayList<>();
    List<Geometria> objetosGeometricosStatic = new ArrayList<>();



    //------------------------------------- INICIO METODOS OPENGL --------------------------------------------------------------


    //CHAMADO UMA VEZ QUANDO A SUPERFICIE DE DESENHO FOR CRIADA
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        //CONFIGURA A COR DE LIMPEZA DA TELA (RGBA)
        gl.glClearColor(0.61f, 0.4f, 0.122f, 1); //vermelha
    }


    //CHAMADO QUANDO A SUPERFICIE DE DESENHO FOR ALTERADA (QUANDO CRIA E QUANDO ROTACIONA A TELA)
    @Override
    public void onSurfaceChanged(GL10 gl, int largura, int altura) {

        this.largura = largura;
        this.altura = altura;
        this.gl = gl;

        //IMPRIME A LARGURA E ALTURA NO CONSOLE DO ANDROID STUDIO
        Log.i("INFO", largura + " " + altura);

        //CONFIGURANDO A AREA DE COORDENADAS DO PLANO CARTESIANO
        gl.glMatrixMode(gl.GL_PROJECTION); //APONTANDO O OPENGL PARA A MATRIZ DE PRODUCAO (usada para definir area de rendereizacao)
        gl.glLoadIdentity(); //SETANDO MATRIZ IDENTIDADE NA MATRIZ DE PRODUCAO
        gl.glOrthof(0f, largura,
                0f, altura,
                -1f, 1f); //DEFININDO O VOLUME DE RENDERIZACAO

        gl.glMatrixMode(GL10.GL_MODELVIEW); //APONTANDO O OPENGL PARA A MATRIZ DE TRANSFORMACOES GEOMETRICAS (tranzacao, rotacao e escala)
        gl.glLoadIdentity(); //para limpar o lixo de memoria e setar a matriz inicial

//        //DESENHANDO...................................................................................
//
        //CONFIGURA A AREA DE VISUALIZAÇÃO NA TELA
        gl.glViewport(0, 0, largura, altura);

//        //DEFINE OS VETORES DE COORDENADAS JAVA (QUADRADO)
//        float[] vetroJava1 = {-largura / 4, -altura / 4,
//                -largura / 4, altura / 4,
//                largura / 4, -altura / 4,
//                largura / 4, altura / 4};
//
//        //CONVERTE PARA O VETOR DO OPENGL
//        buffer1 = criaBuffer(vetroJava1);
//
//        //SOLICITA AO OPENGL PERMISSAO PARA USAR O VETOR DE VERTICES
//        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
//
//        //REGISTRA O VETOR DE COORDENADAS NA OPENGL
//        gl.glVertexPointer(2, GL10.GL_FLOAT, 0, buffer1);
//        //FIM DESENHO.........................................


    }

    private boolean jaListei = false;

    //CHAMADO N VEZES POR SEGUNDO PARA DESENHAR NA TELA
    //QUANTIDADE DE VEZES CHAMADO, DETERMINA O FPS
    //QUANTO MAIS COISAS HOUVER AQUI DENTRO MENOR A TAXA DE FPS
    @Override
    public void onDrawFrame(GL10 gl) {

        //CARREGA A MATRIZ IDENTIDADE
        gl.glLoadIdentity();

//        //AUMENTA A ROTACAO A CADA CICLO
//        this.angulo += 2;
//
//        //AUMENTA A TRANSLACAO A CADA CICLO
//        this.posX += direcaoHorizontal * 3.0;
//        this.posY += direcaoVertical * 3.0;
//
//        //COMO SEI QUE O MEU QUADRADO BATEU NA BORDA VERTICAL (TRANSLACAO)
//        if (posX + tamanhoEstatico >= largura || posX <= 0) {
//            direcaoHorizontal *= -1;
//        }
//        //COMO SEI QUE O MEU QUADRADO BATEU NA BORDA HORIZONTAL (TRANSLACAO)
//        if (posY + tamanhoEstatico >= altura || posY <= 0) {
//            direcaoVertical *= -1;
//        }
//        //REGISTRO O MOVIMENTO DE TRANSLACAO
//        gl.glTranslatef(posX, posY, 0);
//        //REALIZA O MOVIMENTO DE ROTACAO
//        gl.glRotatef(angulo, 0, 0, 1); //rotacao é a multiplicao do angulo por cada eixo
//

        //APLICA A COR DE LIMPEZA DE TELA A TODOS O BITS DO BUFER DE COR
        gl.glClear(gl.GL_COLOR_BUFFER_BIT);

//
////
////
//        //DESENHO A COR DO DESENHO
//        gl.glColor4f(1, 1, 0, 1); //cor yellow
//        //DESENHAR INTERPRETANDO OS VERTICES COMO TRIANGULOS (3 VERTICES) INICIANDO NA POSICAO 0 ATE 3 POSICAO
//        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4); //a partir da posicao 0 desenha 4 vertices

        synchronized (this) {
            for (Geometria objGeometrico :
                    objetosGeometricos) {
                objGeometrico.desenha();
            }
        }

    }


    //------------------------------------- FIM METODOS OPENGL --------------------------------------------------------------


    //conversor de array de coordenadas java -> array de coordenadas openGL
    FloatBuffer criaBuffer(float[] coordenadas) {
        //ALOCA A QTD DE BYTES
        ByteBuffer vetBytes =
                ByteBuffer.allocateDirect(coordenadas.length * 4);

        vetBytes.order(ByteOrder.nativeOrder());

        //CRIA O FLOAT BUFFER A PARTIR DO BYTEBUF
        FloatBuffer buffer = vetBytes.asFloatBuffer();
        //limpa eventual lixo na memoria
        buffer.clear();
        //encaspula o array java no objeto float buffer
        buffer.put(coordenadas);
        //retira sobras da memoria
        buffer.flip();
        //retorna o objeto de coordenadas
        return buffer;
    }


    //OUVINDO AS INTERACOES COM O TOUCHSCREEN
    @Override
    public boolean onTouch(View v, MotionEvent event) {

        synchronized (this) {
            if (event.getAction() == event.ACTION_MOVE) {

//                for (int i = 0; i < this.objetosGeometricos.size(); i++) {
//
//                    if (event.getX() < objetosGeometricos.get(i).getPosX() + tamanhoEstatico &&
//                            event.getX() > objetosGeometricos.get(i).getPosX()) {
//                        //SETO AS POSICOES DE X e Y NO DESENHO
//                        posX = event.getX();
//                        posY = altura - event.getY();
//
//                    }
//
//                }
//
//





            } else if (event.getAction() == event.ACTION_UP) {
                //HOUVE UM TOQUE NA TELA
                this.posX = event.getX();
                this.posY = altura - event.getY();

                Quadrado q = new Quadrado(gl, this.posX, this.posY, tamanhoEstatico, tamanhoEstatico );
                q.setCor((float) Math.random(), (float) Math.random(), (float) Math.random(), (float) Math.random());
                this.objetosGeometricos.add(q);

            } else if (event.getAction() == event.ACTION_DOWN) {

                //HOUVE UM TERMINO DE TOQUE

            }
            return true; //retorno true para que o arrastar possa ser chamado mais de uma vez
        }
    }
}

public class TelaPrincipal extends AppCompatActivity {

    //CRIA REFERENCIA PARA OBJETO DE DESENHO
    GLSurfaceView superficieDesenho = null;
    Renderizador render = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //VALIDA A VAR DE REFERENCIA PARA A SUPERFICIE
        superficieDesenho = new GLSurfaceView(this);

        //VALIDA A VAR DE REFERENCIA PARA O RENDERIZADOR
        render = new Renderizador();

        //ASSOCIA A SUPERFICIE DE DESENHO AO REDENDERIZADOR
        superficieDesenho.setRenderer(render);
        superficieDesenho.setOnTouchListener(render); //setar o ouvidor do touchcreen

        //PUBLICA A SUPERFICIE DE DESENHO NA TELA DO APP
        setContentView(superficieDesenho);
    }
}
