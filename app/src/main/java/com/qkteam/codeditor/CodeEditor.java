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

    public static final int C = 1;
    public static final int CPP = 2;
    public static final int JAVA = 3;

    public static Pattern PATTERN_KEY_WORD_C = Pattern.compile("\\b"+
            "auto|short|int|long|float|double|char|struct|union|"+
            "enum|typedef|const|unsigned|signed|extern|register|"+
            "static|volatile|void|if|else|switch|case|for|do|while"+
            "goto|continue|break|default|sizeof|return"+
            "\\b");

    //匹配头文件等
    public static Pattern PATTERN_HEAD_FILE_C = Pattern.compile("[^\\s\\S]*\\b"+
            "#include|#Define"+"\\b");

    public static Pattern PATTERN_KEY_WORD_CPP = Pattern.compile("\\b" +
            "asm|auto|bool|break|case|catch|char|class|const|" +
            "const_cast|continue|default|delete|do|double|dyamic_cast|" +
            "else|enum|explicit|export|extern|false|float|for|friend|" +
            "goto|if|inline|int|long|mutable|namespace|new|operator|" +
            "private|protected|public|register|reinterpret_cast|return|" +
            "short|signed|sizeof|static|static_cast|struct|switch|template|" +
            "this|throw|true|try|typedef|typeid|typename|union|unsigned|" +
            "using|virtual|void|volatile|wchar_t|while" +
            "\\b");

    public static Pattern PATTERN_KEY_WORD_JAVA = Pattern.compile("\\b"+
            "private|protected|public|"+
            "abstract|class|externs|final|implements|interface|native|" +
            "new|static|strictfp|synchronized|transient|volatile|" +
            "break|case|continue|default|do|instanceof" +
            "return|switch|else|for|if|while|" +
            "boolen|byte|char|double|float|int|long|short|null" +
            "super|this|void" +
            "\\b");

    //匹配错误处理
    public static Pattern PATTERN_ERROR_MESSAGE_JAVA = Pattern.compile("\\b" +
            "(assert|catch|finally|throw|throws|try).*");

    //匹配包相关
    public static Pattern PATTERN_PACKAGE_RELATED_JAVA = Pattern.compile("[^\\s\\S]*" +
            "package|import");

    //匹配运算符
    public static Pattern PATTERN_MATH_SIGN = Pattern.compile("\\+|-|\\*|/|\\^|%|\\||&|!");

    //注释的匹配
    public static Pattern PATTERN_COMMENT_GREY = Pattern.compile("//.*");
    public static Pattern PATTERN_COMMENT_GREEN = Pattern.compile("\\B/\\*(?:.*|[\\n\\r\\t]*)*\\*/\\B");

    private int type;
    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            Editable e = getText();
            highLightCode(e, type);
        }
    };
    private Paint paint = new Paint();
    private Layout layout;
    protected Context context = getContext();

    public CodeEditor(Context context) {
        super(context);
    }

    public CodeEditor(Context context, AttributeSet attrs){
        this(context, attrs, 0);
    }

    public CodeEditor(Context context, AttributeSet attrs, int type) {
        super(context, attrs);
        this.type = type;
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

    private void init(){
        addTextChangedListener(new TextWatcher() {
            int tabType = 0;
            int start;
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
        setText(highLightCode(new SpannableStringBuilder(text), type));
    }


    /**
     * 根据语言高亮
     * @param editable : 需要高亮的内容
     * @param type : 语言类型*/
    public Editable highLightCode(Editable editable, int type){
        switch (type){
            case C:
                highLightCCode(editable);
                break;
            case CPP:
                highLightCppCOde(editable);
                break;
            case JAVA:
                highLightJavaCode(editable);
                break;
        }
        return editable;
    }

    private Editable highLightCCode(Editable e) {
        if (e.length() == 0){
            return e;
        }
        clearSpan(e);
        for (Matcher m = PATTERN_MATH_SIGN.matcher(e); m.find();){
            e.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.highLightMathSign)), m.start(), m.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        for(Matcher m = PATTERN_HEAD_FILE_C.matcher(e); m.find();){
            e.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.highLightHeadFile)), m.start(), m.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        for (Matcher m = PATTERN_KEY_WORD_C.matcher(e); m.find();){
            e.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.highLightKeyword)), m.start(), m.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        for (Matcher m = PATTERN_COMMENT_GREY.matcher(e); m.find();){
            e.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.highLightCommentGrey)), m.start(), m.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return e;
    }

    private Editable highLightCppCOde(Editable e) {
        if (e.length() == 0){
            return e;
        }
        clearSpan(e);
        for (Matcher m = PATTERN_MATH_SIGN.matcher(e); m.find();){
            e.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.highLightMathSign)), m.start(), m.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        for(Matcher m = PATTERN_HEAD_FILE_C.matcher(e); m.find();){
            e.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.highLightHeadFile)), m.start(), m.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        for (Matcher m = PATTERN_KEY_WORD_CPP.matcher(e); m.find();){
            e.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.highLightKeyword)), m.start(), m.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        for (Matcher m = PATTERN_COMMENT_GREY.matcher(e); m.find();){
            e.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.highLightCommentGrey)), m.start(), m.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return e;
    }

    private Editable highLightJavaCode(Editable e) {
        if (e.length() == 0){
            return e;
        }
        clearSpan(e);
        for (Matcher m = PATTERN_MATH_SIGN.matcher(e); m.find();){
            e.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.highLightMathSign)), m.start(), m.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        for (Matcher m = PATTERN_PACKAGE_RELATED_JAVA.matcher(e); m.find();){
            e.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.highLightHeadFile)), m.start(), m.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        for (Matcher m = PATTERN_KEY_WORD_JAVA.matcher(e); m.find();){
            e.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.highLightKeyword)), m.start(), m.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        for (Matcher m = PATTERN_ERROR_MESSAGE_JAVA.matcher(e); m.find();){
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

    private float getPixels(int dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    /**
     * 当不在布局文件里添加CodeEditor时，通过这个方法设置高亮代码的类型*/
    public void setType(int type){
        this.type = type;
    }
}

