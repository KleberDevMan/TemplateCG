package br.edu.catolica_to.templatecg;

import javax.microedition.khronos.opengles.GL10;

public class Triangulo extends Geometria {

    public Triangulo(GL10 gl) {
        super(gl);
    }

    public Triangulo(GL10 gl, float posX, float posY, float largura, float altura) {
        super(gl, posX, posY, largura, altura);
        setCoordenadas(new float[]{
                -largura, -altura,
                -largura, altura,
                largura, -altura}
        );
    }

    public void desenha() {
        getGl().glLoadIdentity();
        this.registraBuffer(getCoordenadas());
        getGl().glColor4f(getRed(), getGreem(), getBlue(), getOpacity());
        getGl().glRotatef(getAnguloRotacao(), 0, 0, 1);
//        getGl().glTranslatef(getPosX() + getLargura() / 2, getPosY() + getAltura() / 2, 0);
        getGl().glTranslatef(getPosX(), getPosY(), 0);
        getGl().glDrawArrays(GL10.GL_TRIANGLES, 0, 3);
    }
}
