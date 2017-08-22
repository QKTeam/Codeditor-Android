package com.qkteam.codeditor;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by 14779 on 2017-8-22.
 */

public class CodeEditForC extends CodeEditor {

    public static Pattern PATTERN_KEY_WORD = Pattern.compile("\\b"+
            "auto|short|int|long|float|double|char|struct|union|"+
            "enum|typedef|const|unsigned|signed|extern|register|"+
            "static|volatile|void|if|else|switch|case|for|do|while"+
            "goto|continue|break|default|sizeof|return"+
            "\\b");

    //匹配运算符
    public static Pattern PATTERN_MATH_SIGN = Pattern.compile("\\+|-|\\*|/|\\^|%|\\||&|!");

    //匹配头文件等
    public static Pattern PATTERN_HEAD_FILE = Pattern.compile("[^\\s\\S]*\\b"+
            "#include|#Define"+"\\b");


    private Context context;

    public CodeEditForC(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Editable highLight(Editable e){
        super.highLight(e);
        for (Matcher m = PATTERN_MATH_SIGN.matcher(e); m.find();){
            e.setSpan(new ForegroundColorSpan(Color.parseColor("#D32F2F")), m.start(), m.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        for(Matcher m = PATTERN_HEAD_FILE.matcher(e); m.find();){
            e.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.highLightHeadFile)), m.start(), m.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        for (Matcher m = PATTERN_KEY_WORD.matcher(e); m.find();){
            e.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.highLightKeyword)), m.start(), m.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        for (Matcher m = PATTERN_COMMENT_GREY.matcher(e); m.find();){
            e.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.highLightCommentGrey)), m.start(), m.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        return e;
    }
}
