package com.philipflyvholm.watermarker;

import java.awt.image.BufferedImage;

public class Converter {

    private final BufferedImage image;
    private final BufferedImage watermark;

    private boolean isConverted;
    private final int margin;
    private final double weight;

    public Converter(BufferedImage image, BufferedImage watermark, double opacity, int margin){
        this.image = image;
        this.watermark = watermark;
        this.isConverted = false;
        this.weight = opacity;
        this.margin = margin;
    }


    public BufferedImage convert() {
        if(this.isConverted()) return image;
        int width = image.getWidth();
        int height = image.getHeight();
        int watermarkWidth = watermark.getWidth();
        int watermarkHeight = watermark.getHeight();

        int watermarkX = 0;
        int watermarkY = 0;
        int timesDisplayed = 0;
        for(int x = margin/2; x < width; x++){

            if(watermarkX >= watermarkWidth){
                watermarkX = 0;
                watermarkY = 0;
                timesDisplayed++;
                x += (margin);
                continue;
            }
            //int watermarkX = (int) ((int) (x-(Math.floor(timesDisplayed)*watermarkWidth)));

            if(watermarkX > 0){
                for(int y = margin/2; y < height; y++){
                    if(watermarkY >= watermarkHeight){
                        watermarkY = 0;
                        y += (margin);
                        continue;
                    }
                    //int watermarkY = y-(Math.floorDiv(y,watermarkHeight)*watermarkHeight);
                    Pixel imagePixel;
                    try{
                        imagePixel = new Pixel(image.getRGB(x,y));
                    }catch(ArrayIndexOutOfBoundsException e){
                        printOutOfBoundsMessage(e, x,y, "ImagePixel");
                        return null;
                    }
                    Pixel watermarkPixel;
                    try{
                        watermarkPixel = new Pixel(watermark.getRGB(watermarkX,watermarkY));
                    }catch(ArrayIndexOutOfBoundsException e){
                        printOutOfBoundsMessage(e, watermarkX,watermarkY, "WatermarkPixel");
                        return null;
                    }
                    if(watermarkPixel.getAlpha() > 0){

                    /*Color watermarkColor = watermarkPixel.toColor();
                    int rgb = watermarkColor.getRGB() | imageColor.getRGB();*/
                        imagePixel.blend(watermarkPixel, weight);
                        image.setRGB(x,y, imagePixel.toColor().getRGB());
                    }

                    watermarkY++;
                }
            }
            watermarkY = 0;
            watermarkX++;
        }
        this.isConverted = true;
        return image;
    }


    private void printOutOfBoundsMessage(Exception e, int x, int y, String pixel){
        System.out.println();
        System.out.println("-----------");
        System.out.println("Failed converting because of failed calculations. (ArrayIndexOutOfBoundsException)");
        System.out.println(pixel + ": x=" + x + ", y=" + y);
        System.out.println(e.getMessage());
        System.out.println("-----------");
        System.out.println();
    }

    public boolean isConverted() {
        return isConverted;
    }
}
