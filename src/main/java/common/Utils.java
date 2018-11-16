package common;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Utils {
    public static String md5(String md5) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("md5");
            byte[] array = md.digest(md5.getBytes());
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
        }
        return null;
    }

    public static void saveData(String text, File file) throws Exception{
        Writer w = null;
        OutputStreamWriter osw = null;
        FileOutputStream is = null;
        try {
            is = new FileOutputStream(file);
            osw = new OutputStreamWriter(is);
            w = new BufferedWriter(osw);
            w.write(text);
        }
        finally {
            if(is != null){
                is.close();
            }

            if(w != null){
                w.close();
            }

            if(osw != null){
                osw.close();
            }
        }
    }

    public static String readFileAsString(String fileName)throws Exception {
        return new String(Files.readAllBytes(Paths.get(fileName)));
    }
}
