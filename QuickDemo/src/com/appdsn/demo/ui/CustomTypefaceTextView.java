
package com.appdsn.demo.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;

import com.appdsn.demo.R;
import com.appdsn.qa.utils.DimenUtils;




/**
 * 自定义字体的TextView
 * 字体放在 assets/fonts/XXXX.ttf
 */
public class CustomTypefaceTextView extends TextView
{

    public CustomTypefaceTextView(Context context)
    {
        this(context, null, 0);
    }

    public CustomTypefaceTextView(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public CustomTypefaceTextView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomTypefaceTextView, defStyleAttr, 0);
        String fontName = typedArray.getString(R.styleable.CustomTypefaceTextView_customTypeface);
        Typeface mTypeface=null;
        if (!TextUtils.isEmpty(fontName)) {

            if (fontName.equals("number")) {
                if (mTypeface == null) {
                   mTypeface = Typeface.createFromAsset(context.getAssets(), "fonts/number.otf");
                }
                setTypeface(mTypeface);
            }
        }
        else {
        	mTypeface = Typeface.createFromAsset(context.getAssets(), "fonts/chinese.otf");
        	setTypeface(mTypeface);
            setIncludeFontPadding(false);
            setLineSpacing(DimenUtils.dp2px(context, 3), (float) 1);//1倍间距
            correctFont();
        }

        typedArray.recycle();
    }

    @Override
    public void setTextSize(float size)
    {
        super.setTextSize(size);
        correctFont();
    }

    @Override
    public void setTextSize(int unit, float size)
    {
        super.setTextSize(unit, size);
        correctFont();
    }

    private void correctFont()
    {
        this.post(new Runnable()
        {
            @Override
            public void run()
            {
                float height = getHeight();
                float size = getTextSize();

                if (size >= height) {
                    setMinimumHeight((int) (size + DimenUtils.dp2px(getContext(), 3)));
                }
            }
        });
    }
}
