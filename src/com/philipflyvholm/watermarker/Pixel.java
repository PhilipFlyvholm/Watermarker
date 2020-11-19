package com.philipflyvholm.watermarker;

import java.awt.*;

public class Pixel {

    int alpha;
    int red;
    int green;
    int blue;

    public Pixel(int pixel){
        this.alpha =  (pixel >> 24) & 0xff;
        this.red = (pixel >> 16) & 0xff;
        this.green = (pixel >> 8) & 0xff;
        this.blue = pixel & 0xff;
    }

    public void blend(Pixel pixel, double weight){
        if(pixel.getAlpha() <= 0) return;
        double alphaDifference = (pixel.getAlpha()/255D);
        weight *= alphaDifference;
        this.red = (int) ((this.red*(1-weight)) + (pixel.getRed()*(weight)));
        this.green = (int) ((this.green*(1-weight)) + (pixel.getGreen()*(weight)));
        this.blue = (int) ((this.blue*(1-weight)) + (pixel.getBlue()*(weight)));
        this.alpha = (int) ((this.alpha*(1-weight)) + (pixel.getAlpha()*(weight)));
    }

    public int getAlpha() {
        return alpha;
    }

    public int getBlue() {
        return blue;
    }

    public int getGreen() {
        return green;
    }

    public int getRed() {
        return red;
    }

    public Color toColor(){
        return new Color(this.getRed(), this.getGreen(), this.getBlue(), this.getAlpha());
    }
}
