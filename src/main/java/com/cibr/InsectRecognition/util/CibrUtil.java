package com.cibr.InsectRecognition.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Base64;
import java.util.UUID;

public class CibrUtil {


    public static String getUUID(){
        return UUID.randomUUID().toString().toLowerCase().replace("-","");
    }

    public static String objectNumberToString(Object o){
        try {
            if (o == null){
                return null;
            }
            return ((BigDecimal) o).setScale(12,BigDecimal.ROUND_HALF_UP).toString();
        }catch (Exception e){
            if (e.getStackTrace().toString().indexOf("Integer") != 0){
                return ((Integer) o).toString();
            }else {
                return null;
            }
        }
    }

    public static String fileToBase64(String path) {
        String base64 = null;
        InputStream in = null;
        try {
            File file = new File(path);
            in = new FileInputStream(file);
            byte[] bytes = new byte[in.available()];
            base64 = Base64.getEncoder().encodeToString(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return base64;
    }
}
