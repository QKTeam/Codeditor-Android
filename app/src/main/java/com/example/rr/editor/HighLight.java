package com.example.rr.editor;
import android.graphics.Color;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.Pair;
import android.widget.EditText;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Character.isAlphabetic;


/**
 * @ method : new HighLight(EditText editText,int colorOfKeywords,int Language ).setHighLight()
 * language: 0-c/c++ 1-java 2-pascal
 *
  public void  test(){
 EditText editText =(EditText)findViewById(R.id.editText);
 new Indent(editText,'\t',true).setIndent();
 new HighLight(editText,Color.RED,0).setHighLight();
 }
 */
public class HighLight {
    private final String[] keys_c ={"do","if","return","typedef","auto","double","inline","short","typeid","bool","dynamic_cast","int","signed","typename","break","else","long","sizeof","union","case","enum","mutable","static","unsigned","catch","explicit","namespace","static_cast","using","char","export","new","struct","virtual","class","extern","operator","switch","void","const","false","private","template","volatile","const_cast","float","protected","this","wchar_t","continue","for","public","throw","while","default","friend","register","true","delete","goto","reinterpret_cast","try"

    };
    private final String[] keys_p ={
            "absolute","abstract","and","array","as","asm","assembler","at","automated","begin","case","cdecl","class","const","constructor","contains","default","destructor","dispid","dispinterface","div","do","downto","dynamic","else","end","except","export","exports","external","far","file","finalization","finally","for","forward","function","goto","if","implementation","implements","in","index","inherited","initialization","inline","interface","is","label","library","message","mod","name","near","nil","nodefault","not","object","of","on","or","out","overload","packed","pascal","private","procedure","program","property","public","published","raise","read","readonly","record","register","reintroduce","repeat","requires","resident","resourcestring","safecall","set","shl","shr","stdcall","stored","string","then","to","try","type","unit","until","uses","var","virtual","while","with","write","writeonly","xor"
    };
    private final String[] keys_j={"abstract","boolean","break","byte","case","catch","char","class","continue","default","do","double","else","extends","false","final","finally","float","for","if","implements","import","instanceof","int","interface","long","native","new","null","package","private","protected","public","return","short","static","super","switch","synchronized","this","throw","throws","transient","try","true","void","volatile","while"};
    private EditText editText;
    private Editable toLight;
    private int color;
    private int normalColor;
    private static int noteColor = Color.GRAY;
    String[]  keys;
    public HighLight(EditText editText,int color,int language){
        this.editText = editText;
        this.color = color;
        this.normalColor = editText.getCurrentTextColor();
        if(language == 0)keys = keys_c;
        else  if(language==1)keys = keys_j;
        else if(language==2)keys = keys_p;

    }
    public  void setHighLight(){
        HighLightWatcher watcher = new HighLightWatcher();
        editText.addTextChangedListener(watcher);
    }


    public class  HighLightWatcher implements TextWatcher{
        boolean ignoreChange = false;
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if(!ignoreChange){
                ignoreChange = true;
                refresh();
                darkenNote();
                ignoreChange = false;
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }
    private static SpannableString setTextForeground( String content, int startIndex, int endIndex, int backgroundColor ){
        if( startIndex < 0 || endIndex > content.length( ) || startIndex >= endIndex ){
            return null;
        }
        SpannableString spannableString = new SpannableString(content);
        spannableString.setSpan(new ForegroundColorSpan(backgroundColor), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }
    private void darkenNote(){
        int index,endIndex = 0;
        if(toLight == null)toLight = editText.getText();
        String string = toLight.toString();
        while ((index = string.indexOf("//",endIndex))!=-1){
            endIndex = string.indexOf("\n",index+2)+1;
            if(endIndex == 0){endIndex = string.length();}
            String sub = string.substring(index, endIndex);
            SpannableString spannableString = setTextForeground(sub,0 ,sub.length(), noteColor);
            if(spannableString!=null)toLight.replace(index,endIndex,spannableString);
        }
        endIndex = 0;
        String note1,note2;int len;
        if(keys==keys_p){
            note1 ="{";
            note2 = "}";
            len = 1;
        }
        else {
            note1 = "/*";
            note2 ="*/";
            len=2;
        }
        while ((index = string.indexOf(note1,endIndex))!=-1){
            endIndex = string.indexOf(note2,index+len)+len;
            if(endIndex == len-1){endIndex = string.length();}
            String sub = string.substring(index, endIndex);
            SpannableString spannableString = setTextForeground(sub,0 ,sub.length(), noteColor);
            if(spannableString!=null)toLight.replace(index,endIndex,spannableString);
        }
    }

    void lightKeys(String string,int left,int right){
        boolean high = false;
        for(int i=0;i<keys.length;i++){
            if(keys[i].equals(string)){
                SpannableString spannableString = setTextForeground(string, 0, string.length() , color);
                toLight.replace(left,right,spannableString);
                high = true;
                break;
            }
        }
        if(!high) {
            try {
                SpannableString spannableString = setTextForeground(string, 0, string.length() ,normalColor);
                if(spannableString!=null)
                    toLight.replace(left,right,spannableString);
            }catch (Exception e){
                Log.d("exption",e.toString());
            }
        }
    }
    public void refresh(){
        String s = ' '+editText.getText().toString()+' ';
        String regex = "\\W(\\w+)\\W";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(s);
        int start = 0;
        while (matcher.find(start)){
            lightKeys(matcher.group(1),matcher.start(1)-1,matcher.end(1)-1);
            start = matcher.end()-1;
        }
    }
}
/*
 protected void lightKeys(){
        int pos;
        toLight = editText.getText();
        pos = editText.getSelectionStart()-1;
        int left=-1,right=-1;
        if(pos<0){
            left = 0;
            for(int i=pos+1;i<toLight.length();i++){
                if(isspace(toLight.charAt(i))){
                    right = i;
                    break;
                }
            }
            if(right==-1)right = toLight.length();
            String string = toLight.subSequence(left,right).toString();
            light(string,left,right);
        }
        Pair<Integer,Integer> tmpPair = getBorder(pos,toLight);
        left = tmpPair.first;right = tmpPair.second;
        String string = toLight.subSequence(left,right).toString();
        light(string,left,right);
        for(;pos>0;pos--){
            if(isAlphabetic(toLight.charAt(pos)))break;
        }
        tmpPair = getBorder(pos,toLight);
        left = tmpPair.first;right = tmpPair.second;
        string = toLight.subSequence(left,right).toString();
        light(string,left,right);
        if(pos+2<toLight.length()){
            tmpPair = getBorder(pos+2,toLight);
            left = tmpPair.first;right = tmpPair.second;
            string = toLight.subSequence(left,right).toString();
            light(string,left,right);
        }
    }
Pair<Integer,Integer> getBorder(int pos,Editable editable)
    {
        int left=-1,right=-1;
        for(int i=pos+1;i<toLight.length();i++){
            if(isspace(toLight.charAt(i))){
                right = i;
                break;
            }
        }
        if(right == -1) right = toLight.length();
        for(int i=pos-1;i>=0;i--){
            if(isspace(toLight.charAt(i))){
                left = i+1;break;
            }
        }
        if(left == -1)left = 0;
        else  if(left>right) left = right;
        for(;left<right&&isspace(editable.charAt(left));left++);
        for(;left<right&&isspace(editable.charAt(right-1));right--);
        return new Pair<>(left,right);
    }
*/
/*    boolean isspace(char c){
        return !isAlphabetic(c)&&c!= '_';
    }
*/