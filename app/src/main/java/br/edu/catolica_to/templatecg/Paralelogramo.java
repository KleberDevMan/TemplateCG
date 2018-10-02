package br.edu.catolica_to.templatecg;

import javax.microedition.khronos.opengles.GL10;

public class Paralelogramo extends Geometria {

    public Paralelogramo(GL10 gl, float posX, float posY, float largura, float altura) {
        super(gl, posX, posY, largura, altura);
    }

    public Paralelogramo(GL10 gl) {
        super(gl);
    }

    public void desenha() {
        getGl().glLoadIdentity();
        this.registraBuffer(getCoordenadas());
        getGl().glColor4f(getRed(), getGreem(), getBlue(), getOpacity());
        getGl().glRotatef(getAnguloRotacao(), 0, 0, 1);
        getGl().glTranslatef(getPosX(), getPosY(), 0);
        getGl().glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
    }
}
