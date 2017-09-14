package com.qkteam.codeditor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by 14779 on 2017-8-22.
 */

public class MyAdapter extends BaseAdapter {

    private List<File> fileList;
    private Context context;
    private LayoutInflater inflater;

    public MyAdapter(Context context, List<File> fileList) {
        this.fileList = fileList;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return fileList.size();
    }

    @Override
    public Object getItem(int i) {
        return fileList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public void addItem(String fileName, int position){
        fileList.add(0, new File(context.getExternalFilesDir("code"), fileName));
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View convertView = view;
        if (convertView == null){
            convertView = inflater.inflate(R.layout.file_list_item, viewGroup, false);
        }
        TextView fileName = convertView.findViewById(R.id.list_item_name);
        TextView createTime = convertView.findViewById(R.id.list_item_create_time);

        fileName.setText(fileList.get(i).getName());
        createTime.setText(TimeFormat(fileList.get(i).lastModified(), "yyyy年hh月dd日 HH:mm:ss"));
        return convertView;
    }

    public String TimeFormat(long time, String format){
        return new SimpleDateFormat(format).format(time);

    }
}
