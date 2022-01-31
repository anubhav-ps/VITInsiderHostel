package com.anubhav.vitinsiderhostel.adapters;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

import android.os.Handler;

public class TypeWriter extends androidx.appcompat.widget.AppCompatTextView {


    private CharSequence text;
    private int index;
    private long delay = 150;

    public TypeWriter(Context context) {
        super(context);
    }

    public TypeWriter(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    private final Handler handler = new Handler();

    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            setText(text.subSequence(0, index++));
            if (index <= text.length()) {
                handler.postDelayed(runnable, delay);
            }
        }
    };

    public void animateText(CharSequence text) {
        this.text = text;
        this.index = 0;
        setText("");
        handler.removeCallbacks(runnable);
        handler.postDelayed(runnable, delay);

    }

    public void setCharacterDelay(long ms) {
        this.delay = ms;
    }

}
