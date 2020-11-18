package com.philipflyvholm.watermarker;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

public class ImageUtils {
    private final static URL baseURL = Main.class.getProtectionDomain().getCodeSource().getLocation();
    private enum SUPPORTED_FORMATS{
        PNG, JPEG, JPG
    }

    public static BufferedImage getImage(File file) throws IOException{
        return ImageIO.read(file);
    }

    public static Map<File, BufferedImage> getImages(File directoryPath){
        //File directoryPath = new File(basePath + folderPath);
        Map<File, BufferedImage> images = new HashMap<>();
        for(File file : Objects.requireNonNull(directoryPath.listFiles())){
            String fileName = file.getName();

            if(!isValidFormat(getFormat(fileName))) continue;
            try {
                BufferedImage image = getImage(file);
                images.put(file ,image);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return images;
    }

    public static String getFormat(String fileName){
        String[] sections = fileName.split("\\.");
        return sections[sections.length-1];
    }

    private static boolean isValidFormat(String format){
        return Arrays.stream(SUPPORTED_FORMATS.values()).filter(e -> e.name().equalsIgnoreCase(format)).findAny().orElse(null) != null;
    }

    public static String getBasePath(){
        try {
            String path = baseURL.toURI().getPath();
            return path.substring(0, path.lastIndexOf("/"));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }



}
