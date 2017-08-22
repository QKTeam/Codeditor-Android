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
import android.util.Log;
import android.util.TypedValue;
import android.view.ViewTreeObserver;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by 14779 on 2017-8-12.
 */

public class CodeEditor extends android.support.v7.widget.AppCompatEditText {
    private static final String TAG = "CodeEditText";

    //头文件等的匹配
    public static Pattern PATTERN_PREPROCESSOR = Pattern.compile("[\\t]*(#include|#define|import|package|extends|implements).*");
    //注释的匹配
    public static Pattern PATTERN_COMMENT_GREY = Pattern.compile("//.*");
    public static Pattern PATTERN_COMMENT_GREEN = Pattern.compile("\\B/\\*(?:.*|[\\n\\r\\t]*)*\\*/\\B");

    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            Editable e = getText();
            highLight(e);
        }
    };

    private Paint paint = new Paint();
    private Layout layout;
    protected Context context = getContext();


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
            int tabType = 0;
            int start,indenCount=0;
            String s;
            /**
             * 在 charSequence 中，从 i 处开始的 i1 个字符将要被长度为 i2 的文本替代
             * @param charSequence:变化前的文本内容
             * @param i:start 开始变化的位置索引，从0开始计数
             * @param i1:count 将要发生变化的字符数
             * @param i2:after 用来替换旧文本的新文本的长度*/
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            /**
             * 在 charSequence 中，从 i 处开始的 i2 个字符刚刚替换了原来长度为 i1 的文本
             * @param charSequence: 变化后的内容
             * @param i:start 开始变化的位置索引
             * @param i1:before 被取代的老文本的长度
             * @param i2:count 将要发生变化的字符数 count为0表示删除文本*/
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.i(TAG, "onTextChanged: start : "+i+"\n"+"before: "+i1+"\n"+"count: "+i2+"\n"+charSequence+"\n ");
                if (i2 > 0 && i+1< charSequence.length()) {
                    if (charSequence.charAt(i) == '\n'&&charSequence.charAt(i+1) != '}')
                        tabType = 1;
                    if (charSequence.charAt(i) == '\n'&&charSequence.charAt(i+1) == '}')
                        tabType = 2;
                }
                start = i;
            }

            /**
             * @param editable: 变化后的文本内容*/
            @Override
            public void afterTextChanged(Editable editable) {
                handler.postDelayed(runnable, 500);
                if (tabType > 0){
                    removeTextChangedListener(this);
                    switch (tabType){
                        case 1:
                            addTab(editable, start, getEffectiveCount(editable, start));
                            break;
                        case 2:
                            Log.i(TAG, "afterTextChanged: *******************"+getEffectiveCount(editable, start));
                            addTab(editable, start, getEffectiveCount(editable, start));
                            editable.insert(start + 2*getEffectiveCount(editable, start)+1, "\n");
                            addTab(editable, start + 2*getEffectiveCount(editable, start)+1, getEffectiveCount(editable, start)-1);
                            setSelection(start+2*getEffectiveCount(editable, start)+1);
                            break;
                    }
                    tabType = 0;
                    addTextChangedListener(this);
                }
            }
        });
    }

    /**
     * 检查{@code editable}的{@code position}位置之前的有效的“{”的数量
     * @param editable 需要检查的字符串
     * @param position 所需检查的位置终点*/
    public int getEffectiveCount(Editable editable, int position){
        int count = 0;
        for (int i = 0; i<position; i++){
            if (editable.charAt(i) == '{')
                count++;
            if (editable.charAt(i) == '}')
                count--;
        }
        return count;
    }
    /**
     * 在{@code editable}的{@code position}位置之后添加{@code count}个制表符}*/
    private void addTab(Editable editable, int position, int count){
        for (int i=0; i<count; i++){
            editable.insert(position+1, "\u3000\u3000");
        }
    }

    /**
     * 读取文件内容是高亮
     * @param text: 从文件读取的文本内容*/
    private void setHighLightText(CharSequence text){
        setText(highLight(new SpannableStringBuilder(text)));
    }

    /**
     * 高亮输入的内容*/
    public Editable highLight(Editable e){

        if (e.length() == 0){
            return e;
        }
        clearSpan(e);

        for(Matcher m = PATTERN_PREPROCESSOR.matcher(e); m.find();){
            e.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.highLightHeadFile)), m.start(), m.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        for (Matcher m = PATTERN_COMMENT_GREY.matcher(e); m.find();){
            e.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.highLightCommentGrey)), m.start(), m.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        for (Matcher m = PATTERN_COMMENT_GREEN.matcher(e); m.find();){
            e.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.highLightCommentGreen)), m.start(), m.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return e;
    }

    public void clearSpan(Editable e) {
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

    /**
     * 判断行数的位数，用于缩进*/
    public int getDigitCount(){
        int len = getLineCount();
        int count=0;
        while (len/10>0){
            count++;
            len /= 10;
        }
        return count;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
         //将光标左移，与左边的数隔开
        setPadding((int) getPixels(getDigitCount()*10+10),0,0,0);

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

