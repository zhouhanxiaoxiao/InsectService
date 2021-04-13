package com.cibr.InsectRecognition.util;

import java.util.Arrays;
import java.util.List;

public class FileUtil {

    public static final List<String> imageTypes = Arrays.asList("jpg","jpeg","png","gif","bmp","tiff","ai","cdr","eps");

    public static boolean isImageFile(String suffix){
        for (String type : imageTypes){
            if (type.equals(suffix)){
                return true;
            }
        }
        return false;
    }
}
