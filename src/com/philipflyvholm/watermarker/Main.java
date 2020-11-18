package com.philipflyvholm.watermarker;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class Main {

    private static final boolean cancelSaving = false;

    public static void main(String[] args) {
        System.out.println("Starting program");
        /* - ON SHUTDOWN - */
        Runtime.getRuntime().addShutdownHook(new Thread(() -> System.out.println("Program stopped")));
        String[] watermarkSrc = new String[]{"watermark.png"};
        for(String arg : args){
            String[] parameters = arg.split("=");
            switch (parameters[0].toLowerCase()){
                case "src": {
                    watermarkSrc = parameters[1].split(",");
                }
            }
        }
        String basePath = ImageUtils.getBasePath();
        System.out.println("Current basepath: " + basePath);
        File inputFolder = new File(basePath + "/input");
        if(!inputFolder.exists()){
            System.out.println("Input folder not found. Trying to create...");
            if(!inputFolder.mkdirs()){
                System.out.println("Failed creating input folder. Exiting...");
                return;
            }else{
                System.out.println("Input folder created!");
            }
        }
        File outputFolder = new File(basePath + "/output");
        if(!outputFolder.exists()){
            System.out.println("Output folder not found. Trying to create...");
            if(!outputFolder.mkdirs()){
                System.out.println("Failed creating output folder. Exiting...");
                return;
            }else{
                System.out.println("Output folder created!");
            }
        }
        for(String watermark : watermarkSrc){
            File watermarkImage = new File(basePath + "/" + watermark);
            if(!watermarkImage.exists()){
                System.out.println("No watermark with source " + watermark + " found. Please add this..");
                return;
            }
            BufferedImage waterMark;
            try {
                waterMark = ImageUtils.getImage(watermarkImage);
            } catch (IOException e) {
                System.out.println("Failed loading watermark");
                System.out.println(e.getLocalizedMessage());
                return;
            }
            Map<File, BufferedImage> inputImages = ImageUtils.getImages(inputFolder);
            if(inputImages.isEmpty()){
                System.out.println("No images in input folder");
                return;
            }
            for(File imageFile : inputImages.keySet()){
                String imageName = imageFile.getName();
                System.out.println("Converting " + imageName + "...");
                Converter converter = new Converter(inputImages.get(imageFile), waterMark);
                BufferedImage convertedImage = null;
                try {
                    convertedImage = converter.convert();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if(convertedImage == null) continue;
                int i = 0;
                String nameWithoutFormat = imageName.substring(0, imageName.lastIndexOf("."));
                String format = ImageUtils.getFormat(imageName);

                File outputFile = new File(basePath + "/output/" + nameWithoutFormat + (watermarkSrc.length > 1 ? "[" + watermark +"]." : ".") + format);
                while (outputFile.exists()){
                    outputFile = new File(basePath + "/output/" + nameWithoutFormat + (watermarkSrc.length > 1 ? "[" + watermark +"]" : "") + "(" + i + ")." + format); //Problem is that is overrides the format
                    i++;
                }
                try {
                    System.out.println("Saving " + imageName + " to /output/" + nameWithoutFormat + (watermarkSrc.length > 1 ? "[" + watermark +"]" : "") + "(" + i + ")." + format);
                    if(!cancelSaving) ImageIO.write(convertedImage, format, outputFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            System.out.println("Conversion finished");
        }

    }
}
