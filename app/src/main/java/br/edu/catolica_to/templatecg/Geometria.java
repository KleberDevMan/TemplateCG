package br.edu.catolica_to.templatecg;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

public abstract class Geometria {

    private GL10 gl;

    private float red;
    private float greem;
    private float blue;
    private float opacity;
    private float coordenadas[];

    //rotacao
    private float anguloRotacao;
    //translacao
    private float posX;
    private float posY;
    //tamanho
    private float largura;
    private float altura;

    public Geometria(GL10 gl, float posX, float posY, float largura, float altura) {
        this.gl = gl;
        this.posX = posX;
        this.posY = posY;
        this.largura = largura;
        this.altura = altura;
    }
    public Geometria(GL10 gl){
        this.gl = gl;
    }

    public void setCoordenadas(float[] coordenadas) {
        this.coordenadas = coordenadas;
    }

    public void registraBuffer(float coordenadas[]) {
        //CONVERTE PARA O VETOR DO OPENGL
        FloatBuffer buffer1 = criaBuffer(coordenadas);

        //SOLICITA AO OPENGL PERMISSAO PARA USAR O VETOR DE VERTICES
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);

        //REGISTRA O VETOR DE COORDENADAS NA OPENGL
        gl.glVertexPointer(2, GL10.GL_FLOAT, 0, buffer1);
    }

    public void setCor(Float red, Float greem, Float blue, Float opacity) {
        this.blue = blue;
        this.greem = greem;
        this.red = red;
        this.opacity = opacity;

    }

    public void desenha(){

    }

    public GL10 getGl() {
        return gl;
    }

    public void setGl(GL10 gl) {
        this.gl = gl;
    }

    public float getRed() {
        return red;
    }

    public void setRed(float red) {
        this.red = red;
    }

    public float getGreem() {
        return greem;
    }

    public void setGreem(float greem) {
        this.greem = greem;
    }

    public float getBlue() {
        return blue;
    }

    public void setBlue(float blue) {
        this.blue = blue;
    }

    public float getOpacity() {
        return opacity;
    }

    public void setOpacity(float opacity) {
        this.opacity = opacity;
    }

    public float[] getCoordenadas() {
        return coordenadas;
    }

    public float getAnguloRotacao() {
        return anguloRotacao;
    }

    public void setAnguloRotacao(float anguloRotacao) {
        this.anguloRotacao = anguloRotacao;
    }

    public float getPosX() {
        return posX;
    }

    public void setPosX(float posX) {
        this.posX = posX;
    }

    public float getPosY() {
        return posY;
    }

    public void setPosY(float posY) {
        this.posY = posY;
    }

    public float getLargura() {
        return largura;
    }

    public void setLargura(float largura) {
        this.largura = largura;
    }

    public float getAltura() {
        return altura;
    }

    public void setAltura(float altura) {
        this.altura = altura;
    }

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
}
