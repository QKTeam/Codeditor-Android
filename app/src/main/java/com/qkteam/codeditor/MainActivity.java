package com.qkteam.codeditor;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.github.clans.fab.FloatingActionButton;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private DrawerLayout drawer;
    private Toolbar toolbar;
    private ListView listView;
    private List<File> fileList = new ArrayList<>();
    int type = 0;
    private FloatingActionButton floatingActionButton;
    public static MyAdapter adapter;

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        setWindowStatus();
        initDrawer();
        initListView();
        initFloatButton();
    }

    private void initFloatButton() {
        floatingActionButton = (FloatingActionButton) findViewById(R.id.float_action_bar_new_file);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View fileTypeView = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_list, null);
                ListView listView = fileTypeView.findViewById(R.id.dialog_list_view);
                //用来选择要创建的文件类型
                final Dialog fileTypeDialog = new AlertDialog.Builder(MainActivity.this).setView(fileTypeView).create();
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        type = i+1;
                        final EditText editText = new EditText(MainActivity.this);
                        //用来给文件命名
                        final Dialog dialog = new AlertDialog.Builder(MainActivity.this).setTitle("请输入文件名").setView(editText)
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        EditFragment fragment = new EditFragment(MainActivity.this);
                                        Bundle args = new Bundle();
                                        args.putInt("fileType", type);
                                        args.putString("fileName",editText.getText().toString()+FileUtil.instance.getFileSuffix(type));
                                        fragment.setArguments(args);
                                        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, fragment).addToBackStack(null).commit();
                                    }
                                }).create();
                        dialog.show();
                        fileTypeDialog.dismiss();
                    }
                });
                fileTypeDialog.show();
            }
        });
    }

    private void initListView() {
        fileList = FileUtil.instance.readFileList(getExternalFilesDir("code").getPath());
        listView = (ListView) findViewById(R.id.list_view_main);
        adapter = new MyAdapter(MainActivity.this, fileList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                EditFragment fragment = new EditFragment(MainActivity.this);
                Bundle bundle = new Bundle();

                int start = fileList.get(i).getName().lastIndexOf(".")>0?fileList.get(i).getName().lastIndexOf("."):(fileList.get(i).getName().length()-1);
                int end = fileList.get(i).getName().length();
                switch (fileList.get(i).getName().subSequence(start+1, end).toString()){
                    case "c":
                        type = CodeEditor.C;
                        break;
                    case "cpp":
                        type = CodeEditor.CPP;
                        break;
                    case "java":
                        type = CodeEditor.JAVA;
                        break;
                    default:
                        type = 0;
                        break;
                }
                bundle.putInt("fileType", type);
                bundle.putString("fileName",fileList.get(i).getName());
                fragment.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, fragment).addToBackStack(null).commit();
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                return true;
            }
        });
    }

    private void initDrawer() {
        drawer = (DrawerLayout) findViewById(R.id.drawer_main);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawer,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_main);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.local_storage:
                        drawer.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.dropbox:
                        drawer.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.settings:
                        drawer.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.feedback:
                        drawer.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.about:
                        drawer.closeDrawer(GravityCompat.START);
                        break;
                }
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    private void setWindowStatus() {
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.TRANSPARENT);
    }
}
