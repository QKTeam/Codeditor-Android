package com.qkteam.codeditor;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

/**
 * Created by 14779 on 2017-8-22.
 */

public class EditFragment extends Fragment {
    public static String DEFAULT_FILE_NAME = "UntitledFile";
    private CodeEditor editor;
    private Toolbar toolbar;
    private Context context;
    private AppCompatActivity appCompatActivity;
    private int editType;
    private String fileName;

    public EditFragment(Context context) {
        this.context = context;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        appCompatActivity = (AppCompatActivity) context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        editor = view.findViewById(R.id.code_editor);
        editType = getArguments().getInt("fileType");
        fileName = (getArguments().getString("fileName")==null)?"":getArguments().getString("fileName");
        editor.setType(editType);
        editor.setText(FileUtil.instance.readFile(context.getExternalFilesDir("code").getPath(), fileName));
        Log.i("EditFragment", "onViewCreated: "+FileUtil.instance.readFile(context.getExternalFilesDir("code").getPath(), fileName));
        setHasOptionsMenu(true);
        initToolbar(view);
    }

    private void initToolbar(View view) {
        toolbar = view.findViewById(R.id.toolbar_edit_fragment);
        toolbar.setTitle(fileName);
        appCompatActivity.setSupportActionBar(toolbar);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.edit_fragment_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_save:
                boolean b = FileUtil.instance.saveFile(context, toolbar.getTitle().toString(), editor.getText().toString());
                Toast.makeText(context, "保存"+(b?"成功":"失败"), Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }

    @Override
    public void setHasOptionsMenu(boolean hasMenu) {
        super.setHasOptionsMenu(hasMenu);
    }
}
