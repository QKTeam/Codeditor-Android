package com.qkteam.codeditor;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.ViewTreeObserver;

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
            "class|interface|abstract|private|public|protected|firiendly|static|final"
            +")\\b");
    //头文件等的匹配
    public static Pattern PATTERN_PREPROCESSOR = Pattern.compile("[\\t]*(#include|#define|import|package|extends|implements).*");
    //注释的匹配
    public static Pattern PATTERN_COMMENT_GREY = Pattern.compile("//.*");
    public static Pattern PATTERN_COMMENT_GREEN = Pattern.compile("\\B/\\*(?:.*|[\\n\\r]*)?\\*/\\B");

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

    private Paint paint = new Paint();
    private Layout layout;
    private Context context = getContext();


    public CodeEditor(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint.setColor(Color.parseColor("#bbbbbb"));
        paint.setAntiAlias(false);
        paint.setTextSize(getPixels(14));
        paint.setStyle(Paint.Style.FILL);
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                layout = getLayout();
            }
        });
        init();
    }

    private float getPixels(int dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
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

    /**
     * 读取文件内容是高亮
     * @param text: 从文件读取的文本内容*/
    private void setHighLightText(CharSequence text){
        setText(highLight(new SpannableStringBuilder(text)));
    }

    /**
     * 高亮输入的内容*/
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
        for (Matcher m = PATTERN_COMMENT_GREY.matcher(e); m.find();){
            e.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.highLightCommentGrey)), m.start(), m.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        for (Matcher m = PATTERN_COMMENT_GREEN.matcher(e); m.find();){
            e.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.highLightCommentGreen)), m.start(), m.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return e;
    }

    private void clearSpan(Editable e) {
        // remove foreground color spans
        {
            ForegroundColorSpan spans[] = e.getSpans(0, e.length(), ForegroundColorSpan.class);

            for (int n = spans.length; n-- > 0; )
                e.removeSpan(spans[n]);
        }

        // remove background color spans
        {
            BackgroundColorSpan spans[] = e.getSpans(0, e.length(), BackgroundColorSpan.class);

            for (int n = spans.length; n-- > 0; )
                e.removeSpan(spans[n]);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int positionY = getBaseline();
        drawNumber(canvas, layout.getLineLeft(0), positionY, 0);
        for(int i=1; i<getLineCount(); i++){
            positionY += layout.getLineBaseline(i) - layout.getLineBaseline(i-1);
            drawNumber(canvas, layout.getLineLeft(i), positionY, i);
        }

    }

    private void drawNumber(Canvas canvas, float x, float y, int line) {
        canvas.drawText(""+(line+1), ((int)x)+getPixels(2), y, paint);
    }

}

