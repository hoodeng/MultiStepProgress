package com.vip.multi.progress;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import plugin.gradle.vip.com.multistepprogress.R;

/**
 * Created by wudeng on 17/3/24.
 */

public class MultiStepProgress extends View {
    static final String sTag = MultiStepProgress.class.getSimpleName();

    static final int sDefaultLineHeight = 12;
    static final int sDefaultCircleRadius = 18;
    static final int sDefaultTextSize = 40;

    private List<String> mSteps;
    private int mPosition;


    private int mWidth;
    private int mHeight;

    private Paint mGrayPaint;
    private Paint mProgressPaint;
    private Paint mTextPaint;
    private Paint mCirclePaint;


    private int mLineHeight;
    private float mTextSize;
    private int mRadius;


    private int mProgressColor;
    private int mGrayColor;
    private int mTextGrayColor;
    private int mTextColor;

    private int mLinePaddingTop;
    private int mLinePaddingLeft;
    private int mLinePaddingRight;
    private int mLinePaddingBottom;

    public MultiStepProgress(Context context) {
        super(context);
        init(context, null);
    }

    public MultiStepProgress(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public MultiStepProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mHeight = MeasureSpec.getSize(heightMeasureSpec);


        int padingTop = getPaddingTop();
        int padingBottom = getPaddingBottom();

        int textHeight = 0;
        if (mTextPaint != null) {
            mTextPaint.setTextSize(mTextSize);
            textHeight = measureTextHeight(mTextPaint);
        }
        int height = mLinePaddingTop + mLineHeight + mLinePaddingBottom + textHeight + padingBottom + padingTop;
        Log.d(sTag, mWidth + "  " + mHeight);
        setMeasuredDimension(widthMeasureSpec, MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mSteps == null || mSteps.isEmpty()) return;

        int lineWidht = mWidth - mLinePaddingLeft - mLinePaddingRight;
        int count = mSteps.size();
        int stepWidth = lineWidht / (count - 1);
        int offset = mLinePaddingLeft;

        mGrayPaint.setStrokeWidth(mLineHeight);
        mProgressPaint.setStrokeWidth(mLineHeight);


        canvas.drawLine(mLinePaddingLeft, mLinePaddingTop, mLinePaddingLeft + lineWidht, mLinePaddingTop, mGrayPaint);

        if (mPosition >= 0) {
            int lineProgress = mPosition * stepWidth + stepWidth / 2;
            lineProgress = Math.min(lineProgress, lineWidht);
            canvas.drawLine(mLinePaddingLeft, mLinePaddingTop, mLinePaddingLeft + lineProgress, mLinePaddingTop, mProgressPaint);
        }

        mTextPaint.setTextSize(mTextSize);

        int cy = mLinePaddingTop;


        for (int i = 0; i < count - 1; i++) {
            if (i <= mPosition) {
                mCirclePaint.setColor(mProgressColor);
                canvas.drawCircle(offset, cy, mRadius, mCirclePaint);
            } else {
                mCirclePaint.setColor(mGrayColor);
                canvas.drawCircle(offset, cy, mRadius, mCirclePaint);

                mCirclePaint.setColor(Color.WHITE);
                canvas.drawCircle(offset, cy, mRadius / 2, mCirclePaint);
            }

            if (i < mPosition) {
                mTextPaint.setColor(mTextColor);
                mTextPaint.setTypeface(Typeface.DEFAULT);
            } else if (i == mPosition) {
                mTextPaint.setColor(mTextColor);
                mTextPaint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            } else {
                mTextPaint.setColor(mTextGrayColor);
                mTextPaint.setTypeface(Typeface.DEFAULT);
            }
            String text = mSteps.get(i);
            float textStart = offset - mTextPaint.measureText(text) / 2;
            canvas.drawText(text, textStart, cy + mLineHeight + mLinePaddingBottom, mTextPaint);
            offset += stepWidth;
        }

        if (mPosition < count - 1) {
            mCirclePaint.setColor(mGrayColor);
            canvas.drawCircle(offset, cy, mRadius, mCirclePaint);

            mCirclePaint.setColor(Color.WHITE);
            canvas.drawCircle(offset, cy, mRadius / 2, mCirclePaint);
        } else {
//            mCirclePaint.setColor(mProgressColor);
//            canvas.drawCircle(mWidth - lineMargin, cy, radius, mCirclePaint);

            Bitmap temp = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_progress_finish);
            Matrix matrix = new Matrix();
            matrix.setTranslate(mWidth - mLinePaddingRight, cy);

            final float diameter = mRadius * 2f;
            float wScale = diameter / temp.getWidth();
            float hScale = diameter / temp.getHeight();
            matrix.setScale(wScale, hScale);

            Bitmap bitmap = Bitmap.createBitmap(temp, 0, 0, temp.getWidth(), temp.getHeight(), matrix, true);
            int left = mWidth - mLinePaddingRight;
            int top = cy - bitmap.getHeight() / 2;
            canvas.drawBitmap(bitmap, left, top, null);
        }


        String text = mSteps.get(count - 1);
        if (mPosition == count - 1) {
            mTextPaint.setColor(mTextColor);
            mTextPaint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        } else {
            mTextPaint.setColor(mTextGrayColor);
            mTextPaint.setTypeface(Typeface.DEFAULT);
        }
        float textStart = mWidth - mLinePaddingRight - mTextPaint.measureText(text) / 2;
        canvas.drawText(text, textStart, cy + mLineHeight + mLinePaddingBottom, mTextPaint);
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;

        post(new Runnable() {
            @Override
            public void run() {
                invalidate();
            }
        });
        Log.d(sTag, "onSizeChanged ----  " + mWidth + "  " + mHeight);
    }

    private void init(Context context, AttributeSet attrs) {
        test();
        mLineHeight = sDefaultLineHeight;
        mTextSize = sDefaultTextSize;
        mRadius = sDefaultCircleRadius;

        TypedArray arr = context.obtainStyledAttributes(attrs, R.styleable.MultiStepProgress, 0, 0);

        if (arr != null) {
            mLinePaddingTop = arr.getDimensionPixelSize(R.styleable.MultiStepProgress_linePaddingTop, 0);
            mLinePaddingLeft = arr.getDimensionPixelSize(R.styleable.MultiStepProgress_linePaddingLeft, 0);
            mLinePaddingRight = arr.getDimensionPixelSize(R.styleable.MultiStepProgress_linePaddingRight, 0);
            mLinePaddingBottom = arr.getDimensionPixelSize(R.styleable.MultiStepProgress_linePaddingBottom, 0);

            mLineHeight = arr.getDimensionPixelSize(R.styleable.MultiStepProgress_lineHeight, sDefaultLineHeight);
            mRadius = arr.getDimensionPixelSize(R.styleable.MultiStepProgress_circleRedius, sDefaultCircleRadius);
            mTextSize = arr.getDimensionPixelSize(R.styleable.MultiStepProgress_textSize, sDefaultTextSize);
            Log.d(sTag, mLinePaddingLeft + "   " + mLinePaddingTop + "  " + mLinePaddingRight + "   " + mLinePaddingBottom + "  " + mTextSize);
            arr.recycle();
        }

        Resources resources = getResources();
        mProgressColor = resources.getColor(R.color.colorProgress);
        mGrayColor = resources.getColor(R.color.colorGray);
        mTextColor = resources.getColor(R.color.colorTextProgress);
        mTextGrayColor = resources.getColor(R.color.colorTextGray);

        mGrayPaint = new Paint();
        mGrayPaint.setAntiAlias(true);
        mGrayPaint.setColor(mGrayColor);

        mCirclePaint = new Paint();
        mCirclePaint.setColor(mProgressColor);
        mCirclePaint.setAntiAlias(true);

        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);

        mProgressPaint = new Paint();
        mProgressPaint.setColor(mProgressColor);
        mProgressPaint.setAntiAlias(true);
    }

    public int measureTextHeight(Paint mPaint) {
        Paint.FontMetrics fm = mPaint.getFontMetrics();
        return (int) Math.ceil(fm.descent - fm.ascent);
    }

    public void setCircleRadius(int radius) {
        mRadius = radius;
    }

    public void setTextSize(float textSize) {
        mTextSize = textSize;
    }

    public void setLineHeight(int lineHeight) {
        mLineHeight = lineHeight;
    }

    public void setLinePaddingTop(int linePaddingTop) {
        mLinePaddingTop = linePaddingTop;
    }

    public void setLinePaddingLeft(int linePaddingLeft) {
        mLinePaddingLeft = linePaddingLeft;
    }

    public void setLinePaddingRight(int linePaddingRight) {
        mLinePaddingRight = linePaddingRight;
    }

    public void setLinePaddingBottom(int linePaddingBottom) {
        mLinePaddingBottom = linePaddingBottom;
    }

    private void test() {
        mSteps = new ArrayList<>();
        for (int i = 1; i < 6; i++) {
            mSteps.add("步骤" + i);
        }
    }

    public void add() {
        int size = mSteps.size();
        mSteps.add("步骤" + (size + 1));
        invalidate();
    }

    public void remove() {
        if (mSteps.size() > 2) {
            mSteps.remove(mSteps.size() - 1);
            invalidate();
        }
    }

    public void next() {
        mPosition++;
        mPosition = Math.min(mPosition, mSteps.size() - 1);
        invalidate();
    }

    public void pre() {
        mPosition--;
//        mPosition = Math.max(0, mPosition);
        invalidate();
    }

    public void setSteps(List<String> steps) {
        mSteps = steps;
    }

    public void setPosition(int position) {
//        if (position < 0) {
//            position = 0;
//        }
        position--;
        if (position > mSteps.size() - 1) {
            position = mSteps.size() - 1;
        }
        mPosition = position;
        invalidate();
    }
}
