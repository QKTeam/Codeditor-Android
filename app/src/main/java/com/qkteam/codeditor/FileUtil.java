package com.qkteam.codeditor;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 14779 on 2017-8-22.
 */

public class FileUtil {
    private static final String TAG = "FileUtil";
    public static FileUtil instance = new FileUtil();

    public List<File> readFileList(String path) {
        File file = new File(path);
        File[] files = file.listFiles();
        List<File> fileList = new ArrayList<>();
        for (int i = 0; i < files.length; i++) {
            fileList.add(files[i]);
        }
        return fileList;
    }

    public String readFile(String filePath, String fileName) {
        String content = "";

        File file = new File(filePath + "/" + fileName);
        try {
            FileInputStream inputStream = new FileInputStream(file);
            byte b[] = new byte[inputStream.available()];
            inputStream.read(b);
            content += new String(b);
            return content;
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    public boolean saveFile(Context context, String fileName, String content) {

        if (content.length() == 0)
            return false;
        File file = new File(context.getExternalFilesDir("code"), fileName);
        try {
            if (!file.exists()) {
                file.createNewFile();
            } else {
                PrintWriter printWriter = new PrintWriter(new FileOutputStream(file));
                printWriter.write(content);
                printWriter.flush();
                printWriter.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    public String getFileSuffix(int type) {
        switch (type) {
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
