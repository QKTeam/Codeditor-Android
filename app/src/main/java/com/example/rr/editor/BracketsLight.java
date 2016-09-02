package com.example.rr.editor;

import android.graphics.Color;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.KeyListener;
import android.text.style.BackgroundColorSpan;

import android.util.Log;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;


/**
 * Created by rr on 2016/8/14.
 */
public class BracketsLight {
    private EditText editText;
    private Editable editable;
    private int backColor = Color.GREEN;
    Pair<Integer, Integer> pair = new Pair<>(-1, -1);

    BracketsLight(EditText editText) {
        this.editText = editText;
        editable = editText.getText();
    }

    void setBracketsLight() {
        ClickWatcher clickWatcher = new ClickWatcher();
        editText.setOnClickListener(clickWatcher);
        KeyListener keyListener  = new KeyListener();
        editText.setOnKeyListener(keyListener);
        editText.addTextChangedListener(new Textwatcher());
    }

    class KeyListener implements View.OnKeyListener {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (keyCode == 21 || keyCode == 22) {
                change();
            }
            return false;
        }
    }
    class ClickWatcher implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            change();
        }
    }
    class Textwatcher implements TextWatcher{
        boolean ignoreChange = false;

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if(!ignoreChange){
                ignoreChange = true;
                change();
                ignoreChange = false;
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }

    void  change(){
        int pos = editText.getSelectionStart();
        if(pos<editable.length()){
            char c = editable.charAt(pos);
            if(isBracket(c)==1){
                light(pos,c,-1);
            }
            else  if(isBracket(c)==-1){
                light(pos,c,1);
            }
            else if(pair.first>=0&&pair.second>=0){
                dark();
            }
        }
        else if(pair.first>=0&&pair.second>=0){
            dark();
        }
    }

    int isBracket(char c) {
        if (c == '[' || c == '{' || c == '(') return -1;
        else if (c == ']' || c == '}' || c == ')') return 1;
        else return 0;
    }

    char getMatch(char c) {
        switch (c) {
            case '[':
                return ']';
            case '{':
                return '}';
            case '(':
                return ')';
        }
        return 0;
    }

    void light(int pos, char c, int mode) {
        if(pair.first>=0&&pair.second>=0)dark();
        char d = getMatch(c);
        int cnt = 0, len = editable.length();
        for (int i = pos; i < len && i >= 0; i += mode) {
            if (editable.charAt(i) == c) {
                cnt++;
            } else if (editable.charAt(i) == d) {
                cnt--;
            }
            if (cnt == 0) {
                pair = new Pair<>(pos, i);
                SpannableString spannableString;
                spannableString = setTextBackground(c + "", 0, 1, backColor);
                editable.replace(pos, pos + 1, spannableString);
                spannableString = setTextBackground(d + "", 0, 1, backColor);
                editable.replace(i, i + 1, spannableString);
                break;
            }
        }
    }

    void dark() {
       try{
           SpannableString spannableString;
           spannableString = setTextBackground(editable.charAt(pair.first) + "", 0, 1, editText.getDrawingCacheBackgroundColor());
           editable.replace(pair.first, pair.first + 1, spannableString);
           spannableString = setTextBackground(editable.charAt(pair.second) + "", 0, 1, editText.getDrawingCacheBackgroundColor());
           editable.replace(pair.second, pair.second + 1, spannableString);
           pair = new Pair<>(-1, -1);
       }catch (Exception e){
           Log.d("Errorss", "dark: "+e);
       }
    }

    private SpannableString setTextBackground(String content, int startIndex, int endIndex, int backgroundColor) {
        if (startIndex < 0 || endIndex > content.length() || startIndex >= endIndex) {
            return null;
        }
        SpannableString spannableString = new SpannableString(content);
        spannableString.setSpan(new BackgroundColorSpan(backgroundColor), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }
}
