package com.qkteam.codeditor;

import android.content.Context;

import java.io.File;
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

    public void saveFile(Context context, String fileName, String content){
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
        }
}
