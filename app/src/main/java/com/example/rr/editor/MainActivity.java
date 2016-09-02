package com.example.rr.editor;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

/**
*
*public void  test(){
        EditText editText =(EditText)findViewById(R.id.editText);
        new Indent(editText,'\t',true).setIndent();
        new HighLight(editText,Color.RED,0).setHighLight();
    }
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        test();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
    public void  test(){
        int lang = 2;
        EditText editText =(EditText)findViewById(R.id.editText);
        TextView textView =(TextView)findViewById(R.id.textView);
        new Indent(editText,'\t',true,lang).setIndent();
        new HighLight(editText,Color.RED,lang).setHighLight();
        new BracketsLight(editText).setBracketsLight();
       // new AutoComplete(editText,0).setAutoComplete();


    }
}
