package cn.linhome.library.view;

import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

/**
 * 显示默认字符串的TextView
 */
public abstract class FPlaceholderTextView extends AppCompatTextView
{
    public FPlaceholderTextView(Context context)
    {
        super(context);
    }

    public FPlaceholderTextView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public FPlaceholderTextView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    public static final String PLACEHOLDER = "$";

    @Override
    public void setText(CharSequence text, BufferType type)
    {
        final String replaceString = getReplaceString();
        if (text != null && replaceString != null)
        {
            final String content = text.toString().replace(PLACEHOLDER, replaceString);
            text = content;
        }
        super.setText(text, type);
    }

    protected abstract String getReplaceString();
}
