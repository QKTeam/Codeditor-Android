package com.example.rr.editor;

import android.graphics.Color;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;

import android.util.Log;
import android.widget.EditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by rr on 2016/8/9.
 *
 * public void  test(){
 EditText editText =(EditText)findViewById(R.id.editText);
 new Indent(editText,'\t',true).setIndent();
 new HighLight(editText,Color.RED,0).setHighLight();
 }
 */

public class Indent {
    String tabType;
    boolean autoMatch;
    private EditText editText;
    int lang;
    public  Indent(EditText editText,char tabType,boolean autoMatch,int lang ){
        this.editText = editText;
        if(tabType=='\t')this.tabType = "\t\t";
        else this.tabType = "    ";
        this.autoMatch = autoMatch;
        this.lang = lang;
    }
    public  void setIndent(){
        IndentWatcher watcher = new IndentWatcher();
        editText.addTextChangedListener(watcher);
    }
    class IndentWatcher implements TextWatcher{
        int pos;
        Editable editable = editText.getText();
        boolean ignore = false;
        int  preLen,postLen;
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            preLen = s.length();
        }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            postLen = s.length();
            pos = editText.getSelectionStart();
            if (!ignore) {
                ignore = true;
                if (postLen > preLen) {
                    if (pos > 0 && isToMatch(pos)) {
                        editable.insert(pos, getMatch(editable.charAt(pos - 1)) + "");
                        editText.setSelection(pos - 1);
                    }
                    if (pos > 0 && pos <= editable.length() && editable.charAt(pos - 1) == '\n') {
                       if(lang<2) autoTab(editable, pos);
                        else {
                           autoTab_pascal(editable,pos);
                       }
                    }
                }
                ignore = false;
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
        boolean isToMatch(int pos){
            if (!autoMatch)return false;
            try
            {
                char self = editable.charAt(pos-1);
                if(getMatch(self)>0){
                    if(pos==editable.length())return true;
                    else {
                        char next = editable.charAt(pos);
                        if(next !=getMatch(self)&& isSpace(next)){return true;}
                    }
                }
            }catch (Exception e){
                return  false;
            }
            return  false;
        }
        char getMatch(char c){
            switch (c){
                case '(':return ')';
                case '[':return ']';
                case '{':return '}';
                case '\"':return '\"';
                case '\'':return '\'';
            }
            return 0;
        }
        boolean isSpace(char c){
            return c==' '||c=='\t'||c=='\n'|| c==']'||c== '}'||c==')'||c=='\''||c=='\"';
        }
    }
    void autoTab_pascal(Editable editable,int pos){
        int cntTab = 0;
        String s = " "+editable.subSequence(0,pos)+" ";
        String regex = "\\W(begin)\\W";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(s);
        int start = 0;
        while (matcher.find(start)){
            cntTab++;
            start = matcher.end()-1;
        }
        regex = "\\W(end)\\W";
        pattern = Pattern.compile(regex);
        matcher = pattern.matcher(s);
        start = 0;
        while (matcher.find(start)){
            cntTab--;
            start = matcher.end()-1;
        }
        StringBuilder builder = new StringBuilder();
        for(int i=0;i<cntTab; i++) {
            builder.append(tabType);
        }
        editable.insert(pos,builder.toString());
    }

    void  autoTab(Editable editable,int pos){
        int cntTab = 0;
        for(int i = 0;i<pos;i++){
            char c = editable.charAt(i);
            if(c=='{'){cntTab++;}
            else if(c=='}'){cntTab--;}
        }
        StringBuilder builder = new StringBuilder();
        for(int i=0;i<cntTab; i++) {
            builder.append(tabType);
        }
        editable.insert(pos,builder.toString());
        pos = editText.getSelectionStart();
//        builder.delete(0,builder.length());
//        for(int i=0;i<cntTab-1;i++)builder.append(tabType);
        if(pos<editable.length()&&editable.charAt(pos)=='}'){
            builder.delete(0,builder.length());
            for(int i=0;i<cntTab-1;i++)builder.append(tabType);
            editable.insert(pos,"\n"+ builder.toString());
            pos = editText.getSelectionStart();
            while (pos>0&&editable.charAt(pos--)!='\n');
            editText.setSelection(pos+1);
        }
    }

}
