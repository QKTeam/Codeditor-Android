package com.qkteam.codeditor;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 14779 on 2017-8-22.
 */

public class FileUtil {
    private static final String TAG = "FileUtil";
    public static FileUtil instance = new FileUtil();

    public List<File> readFileList(String path){
        File file = new File(path);
        File[] files = file.listFiles();
        List<File> fileList = new ArrayList<>();
        for (int i = 0; i<files.length; i++){
            fileList.add(files[i]);
        }
        return fileList;
    }

    public String readFile(String filePath, String fileName){
        String result = "";
        byte b[] = new byte[1024];
        File file = new File(filePath+"/"+fileName);
        if (file.exists()){
            try {
                FileInputStream inputStream = new FileInputStream(file);
                int len;
                while((len=inputStream.read(b))>0){
                    result += new String(b);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public boolean saveFile(Context context, String fileName, String content){

        if (content.length() == 0)
            return false;

        File file = new File(context.getExternalFilesDir("code"), fileName);
        try {
            if (!file.exists()) {
                file.createNewFile();
            } else {
                FileOutputStream outputStream = new FileOutputStream(file);
                outputStream.write(content.getBytes());
                outputStream.flush();
                outputStream.close();

            }
        }catch (IOException e) {
                e.printStackTrace();
            }
        return true;
        }

        public String getFileSuffix(int type){
            switch (type){
                case 1:
                    return ".c";
                case 2:
                    return ".cpp";
                case 3:
                    return ".java";
            }
            return "";
        }

}
