package com.philipflyvholm.watermarker;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Main {

    private static final boolean cancelSaving = false;

    public static void main(String[] args) {
        System.out.println("Starting program");

        /* - ON SHUTDOWN - */
        Runtime.getRuntime().addShutdownHook(new Thread(() -> System.out.println("Program stopped")));

        //TODO: ADD A BETTER SETTINGS FUNCTION
        String[] watermarkSrc = new String[]{"watermark.png"};
        double opacity = 0.3;
        int margin = 5;

        for(String arg : args){
            String[] parameters = arg.split("=");
            switch (parameters[0].toLowerCase()){
                case "src": {
                    watermarkSrc = parameters[1].split(",");
                    break;
                }
                case "opacity": {
                    final String s = parameters[1].replaceAll(",", ".");
                    try{
                        double tempOpacity = Double.parseDouble(s);
                        if(tempOpacity > 1 || tempOpacity < 0){
                            System.out.println("The opacity value " + s + " is not a valid number between 0.0-1.0");
                            return;
                        }
                        opacity = tempOpacity;
                    }catch (NumberFormatException e){
                        System.out.println("The opacity value " + s + " is not a valid number between 0.0-1.0");
                        return;
                    }
                    break;
                }
                case "margin": {
                    final String s = parameters[1].replaceAll(",", ".");
                    try{
                        int tempMargin = Integer.parseInt(s);
                        if(tempMargin < 0){
                            System.out.println("The margin value " + s + " is not a valid number");
                            return;
                        }
                        margin = tempMargin;
                    }catch (NumberFormatException e){
                        System.out.println("The opacity value " + s + " is not a valid number");
                        return;
                    }
                    break;
                }
            }
        }

        String basePath = ImageUtils.getBasePath();
        System.out.println("Current basepath: " + basePath);
        File inputFolder = getFolder(basePath, "input");
        File outputFolder = getFolder(basePath, "output");

        if(inputFolder == null || outputFolder == null) return;

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

            File[] inputImages = inputFolder.listFiles();
            if(inputImages == null || inputImages.length == 0){
                System.out.println("No images in input folder");
                return;
            }
            for(File imageFile : inputImages){
                String imageName = imageFile.getName();
                System.out.println("Converting " + imageName + "...");
                BufferedImage image;
                try {
                     image = ImageUtils.getImage(imageFile);
                } catch (IOException e) {
                    System.out.println("Failed loading " + imageName + " - " + e.getMessage());
                    continue;
                }

                Converter converter = new Converter(image, waterMark, opacity, margin);

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
                String newImageName = nameWithoutFormat + (watermarkSrc.length > 1 ? "[" + watermark +"]." : ".") + format;
                File outputFile = new File(outputFolder.getPath() + "/" + newImageName);
                while (outputFile.exists()){
                    newImageName = nameWithoutFormat + (watermarkSrc.length > 1 ? "[" + watermark +"]" : "") + "(" + i + ")." + format;
                    outputFile = new File(outputFolder.getPath() + "/"  + newImageName);
                    i++;
                }
                try {
                    System.out.println("Saving " + imageName + " to /output/" + newImageName);
                    if(!cancelSaving) ImageIO.write(convertedImage, format, outputFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            System.out.println("Conversion finished");
        }

    }

    private static File getFolder(String basePath, String folderName){
        File folder = new File(basePath + "/" + folderName);
        if(!folder.exists()){
            System.out.println(folderName + " folder not found. Trying to create...");
            if(!folder.mkdirs()){
                System.out.println("Failed creating " + folderName + " folder. Exiting...");
                return null;
            }else{
                System.out.println(folderName + " folder created!");
            }
        }
        return folder;
    }
}
