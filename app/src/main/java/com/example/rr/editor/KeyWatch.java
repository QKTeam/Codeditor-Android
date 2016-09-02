package com.example.rr.editor;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.widget.EditText;


public class KeyWatch {
    HighLight highLight;
    Undo undo;
    public KeyWatch(HighLight highLight,Undo undo){
        this.highLight = highLight;
        this.undo = undo;
    }

    public void  SelectAll(EditText editText){
        editText.setSelection(0,editText.getText().length());
    }
    public  void copyText(EditText editText,ClipboardManager clip){
        int start = editText.getSelectionStart();
        int end = editText.getSelectionEnd();
        ClipData data = ClipData.newPlainText("text",editText.getText().subSequence(start,end));
        clip.setPrimaryClip(data);
    }
    public   void cutText(EditText editText,ClipboardManager clip){
        int start = editText.getSelectionStart();
        int end = editText.getSelectionEnd();
        ClipData data = ClipData.newPlainText("text",editText.getText().subSequence(start,end));
        clip.setPrimaryClip(data);
        editText.getText().delete(start,end);
       if(highLight!=null) highLight.refresh();
    }
    public  void pasteText(EditText editText,ClipboardManager clipboard,Context context){
        if (!clipboard.hasPrimaryClip()){
            return;
        }
       else {
            String resultString = "";
            ClipData clipData = clipboard.getPrimaryClip();
            int count = clipData.getItemCount();
            for (int i = 0; i < count; ++i) {
                ClipData.Item item = clipData.getItemAt(i);
                CharSequence str = item.coerceToText(context);
                resultString += str;
            }
            editText.getText().insert(editText.getSelectionStart(),resultString);
            if(highLight!=null) highLight.refresh();
            if(undo!=null&&!resultString.equals(""))undo.setPaste();
        }
    }
    public void undo(){
        if(undo!=null){
            undo.undo();
        }
    }
    public void redo(){
        if(undo!=null)undo.redo();
    }
}
