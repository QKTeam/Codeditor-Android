package com.qkteam.codeditor;

import android.content.Context;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by 14779 on 2017-8-12.
 */

public class CodeEditor extends android.support.v7.widget.AppCompatEditText {
    private static final String TAG = "CodeEditText";
    //关键字的匹配
    public static Pattern PATTERN_KEY_WORDS = Pattern.compile("\\b("+
            "short|int|long|float|double|char|String|void|boolen|integer|List"+
            "if|else|for|foreach|switch|case|defalut|break|do|while|"+
            "scanf|printf|println|return|System|Out|"+
            "class|interface|abstract|"
            +")\\b");
    //头文件等的匹配
    public static Pattern PATTERN_PREPROCESSOR = Pattern.compile("[\\t]*(#include|#define|import|package|extends|implements)\\b");
    //注释的匹配
    public static Pattern PATTERN_COMMENT = Pattern.compile("/\\*(?:.*|[\\n\\r])*?\\*|//.*");

    private Context context = getContext();
    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            Editable e = getText();
//            if (!isModified)
//                return;
            highLight(e);
        }
    };

    public CodeEditor(Context context) {
        super(context);
        this.context = context;
        init();
    }
    public CodeEditor(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    public CodeEditor(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                handler.postDelayed(runnable, 500);
            }
        });
    }

    private void setHighLightText(CharSequence text){
        setText(highLight(new SpannableStringBuilder(text)));
    }

    private Editable highLight(Editable e){

        if (e.length() == 0){
            return e;
        }
        clearSpan(e);
        for(Matcher m = PATTERN_PREPROCESSOR.matcher(e); m.find();){
            e.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.highLightHeadFile)), m.start(), m.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        for (Matcher m = PATTERN_KEY_WORDS.matcher(e); m.find();){
            e.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.highLightKeyword)), m.start(), m.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        for (Matcher m = PATTERN_COMMENT.matcher(e); m.find();){
            e.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.highLightComment)), m.start(), m.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return e;
    }

    private void clearSpan(Editable e) {
        // remove foreground color spans
        {
            ForegroundColorSpan spans[] = e.getSpans(
                    0,
                    e.length(),
                    ForegroundColorSpan.class);

            for (int n = spans.length; n-- > 0; )
                e.removeSpan(spans[n]);
        }

        // remove background color spans
        {
            BackgroundColorSpan spans[] = e.getSpans(
                    0,
                    e.length(),
                    BackgroundColorSpan.class);

            for (int n = spans.length; n-- > 0; )
                e.removeSpan(spans[n]);
        }
    }
}

