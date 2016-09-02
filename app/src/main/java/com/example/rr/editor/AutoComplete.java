package com.example.rr.editor;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.StringTokenizer;

import static java.lang.Character.isAlphabetic;

/**
 * new AutoComplete().setAutoComplete();
 * getList(ArrayList<string>);
 * Created by rr on 2016/8/12.
 */

public class AutoComplete {
    private final String[] keys_c = {"do", "if", "return", "typedef", "auto", "double", "inline", "short", "typeid", "bool", "dynamic_cast", "int", "signed", "typename", "break", "else", "long", "sizeof", "union", "case", "enum", "mutable", "static", "unsigned", "catch", "explicit", "namespace", "static_cast", "using", "char", "export", "new", "struct", "virtual", "class", "extern", "operator", "switch", "void", "const", "false", "private", "template", "volatile", "const_cast", "float", "protected", "this", "wchar_t", "continue", "for", "public", "throw", "while", "default", "friend", "register", "true", "delete", "goto", "reinterpret_cast", "try"

    };
    private final String[] keys_p = {
            "absolute", "abstract", "and", "array", "as", "asm", "assembler", "at", "automated", "begin", "case", "cdecl", "class", "const", "constructor", "contains", "default", "destructor", "dispid", "dispinterface", "div", "do", "downto", "dynamic", "else", "end", "except", "export", "exports", "external", "far", "file", "finalization", "finally", "for", "forward", "function", "goto", "if", "implementation", "implements", "in", "index", "inherited", "initialization", "inline", "interface", "is", "label", "library", "message", "mod", "name", "near", "nil", "nodefault", "not", "object", "of", "on", "or", "out", "overload", "packed", "pascal", "private", "procedure", "program", "property", "public", "published", "raise", "read", "readonly", "record", "register", "reintroduce", "repeat", "requires", "resident", "resourcestring", "safecall", "set", "shl", "shr", "stdcall", "stored", "string", "then", "to", "try", "type", "unit", "until", "uses", "var", "virtual", "while", "with", "write", "writeonly", "xor"
    };
    private final String[] keys_j = {"abstract", "boolean", "break", "byte", "case", "catch", "char", "class", "continue", "default", "do", "double", "else", "extends", "false", "final", "finally", "float", "for", "if", "implements", "import", "instanceof", "int", "interface", "long", "native", "new", "null", "package", "private", "protected", "public", "return", "short", "static", "super", "switch", "synchronized", "this", "throw", "throws", "transient", "try", "true", "void", "volatile", "while"};
    private EditText editText;
    String[] keys;
    TextView textView;
    ArrayList<String> stringList;

    public AutoComplete( EditText editText, int language) {
        // this.list = list;
       // this.textView = textView;
        this.editText = editText;
        if (language == 0) keys = keys_c;
        else if (language == 1) keys = keys_j;
        else if (language == 2) keys = keys_p;
    }

    public void setAutoComplete() {
        AutoCompleteWatcher watcher = new AutoCompleteWatcher();
        editText.addTextChangedListener(watcher);
    }

    private class AutoCompleteWatcher implements TextWatcher {
        int preLen, postLen;

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            preLen = s.length();
            return;
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            postLen = s.length();
            int pos = editText.getSelectionStart();
            if (!(pos > 0) || !isAlphabetic(s.charAt(pos - 1))) {
                return;
            }
            if (postLen > preLen)
                complete(s.toString(), pos);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }

        private void complete(String s, int pos) {
            int left = 0, right = pos;
            for (int i = pos - 1; i >= 0; i--) {
                if (!isAlphabetic(s.charAt(i))) {
                    left = i + 1;
                    break;
                }
            }
            String sub = s.substring(left, right);
            //ArrayList<String> arrayList = new ArrayList<>();
            //textView.setText("");
            for (int i = 0; i < keys.length; i++) {
                if (keys[i].startsWith(sub)) {
                    editText.getText().insert(pos, keys[i], sub.length() - 1, keys[i].length());
                    editText.setSelection(pos + sub.length() - 1, pos + keys[i].length());
                    break;
                }
            }

        }

        public void getList(ArrayList<String> word) {
            if (stringList == null) {
                word = new ArrayList<>();
            } else word = stringList;
        }
    }
}
