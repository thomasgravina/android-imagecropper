package com.thomasgravina.imagecropper;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by thomas on 5/29/14.
 */
public class CropperView extends View
{

    private HANDLE mCurrentHandle = HANDLE.NONE;
    private Context mContext;
    private Paint   mPaintBlue, mPaintRed;
    private RectF mMiddleRight, mMiddleLeft, mMiddleTop, mMiddleBottom, mCenter;
    private boolean mInitialized = false;
    //    private RectF mTopLeft, mTopRight, mBottomLeft, mBottomRight;
    private float mScreenWidth, mScreenHeight;
    private float mWidth, mHeight;
    private float mBoundTop, mBoundRight, mBoundBottom, mBoundLeft;
    private float mTop, mRight, mBottom, mLeft;
    private Map<RectF, HANDLE> mRectFHANDLEMap = new HashMap<RectF, HANDLE>();

    public CropperView(final Context context)
    {
        super(context);
        mContext = context;
    }

    public CropperView(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
        mContext = context;
    }

    public CropperView(final Context context, final AttributeSet attrs, final int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }

    public void init(final int width, final int height)
    {
        mWidth = width;
        mHeight = height;
        invalidate();
    }

    public float getImageWidth()
    {
        return mWidth;
    }

    public float getImageHeight()
    {
        return mHeight;
    }

    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mScreenWidth = getScreenWidth();
        mScreenHeight = getScreenHeight();
        setMeasuredDimension(getScreenWidth(), getScreenHeight());
    }

    @Override
    protected void onDraw(final Canvas canvas)
    {
        super.onDraw(canvas);
        final ViewGroup.MarginLayoutParams vlp = (ViewGroup.MarginLayoutParams) getLayoutParams();
        final View parent = (View) getParent();
        final int offsetH = (int) (vlp.topMargin + vlp.bottomMargin - parent.getHeight() +
                mScreenHeight);

        if (!mInitialized)
        {
            mLeft = mScreenWidth / 2 - mWidth / 2;
            mTop = mScreenHeight / 2 - mHeight / 2 - offsetH / 2;
            mRight = mLeft + mWidth;
            mBottom = mTop + mHeight;

            mBoundTop = mTop;
            mBoundLeft = mLeft;
            mBoundBottom = mBottom;
            mBoundRight = mRight;

            mInitialized = true;
        }

        // Define paints and colors
        mPaintBlue = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintRed = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintBlue.setColor(Color.CYAN);
        mPaintRed.setColor(Color.RED);

        // Draw borders
        canvas.drawLine(mLeft, mTop, mRight, mTop, mPaintBlue);
        canvas.drawLine(mRight, mTop, mRight, mBottom, mPaintBlue);
        canvas.drawLine(mRight, mBottom, mLeft, mBottom, mPaintBlue);
        canvas.drawLine(mLeft, mBottom, mLeft, mTop, mPaintBlue);

        // Draw handles
        final int size = 45;

        mMiddleTop = new RectF(mLeft + ((mRight - mLeft) / 2) - size, mTop - size,
                mLeft + ((mRight - mLeft) / 2) + size, mTop + size);
        canvas.drawArc(mMiddleTop, 0, 180, true,
                mCurrentHandle == HANDLE.MIDDLE_TOP ? mPaintRed : mPaintBlue);

        mMiddleRight = new RectF(mRight - size, mTop + ((mBottom - mTop) / 2) - size, mRight + size,
                mTop + ((mBottom - mTop) / 2) + size);
        canvas.drawArc(mMiddleRight, 90, 180, true,
                mCurrentHandle == HANDLE.MIDDLE_RIGHT ? mPaintRed : mPaintBlue);

        mMiddleBottom = new RectF(mLeft + ((mRight - mLeft) / 2) - size, mBottom - size,
                mLeft + ((mRight - mLeft) / 2) + size, mBottom + size);
        canvas.drawArc(mMiddleBottom, 180, 180, true,
                mCurrentHandle == HANDLE.MIDDLE_BOTTOM ? mPaintRed : mPaintBlue);

        mMiddleLeft = new RectF(mLeft - size, mTop + ((mBottom - mTop) / 2) - size, mLeft + size,
                mTop + ((mBottom - mTop) / 2) + size);
        canvas.drawArc(mMiddleLeft, 270, 180, true,
                mCurrentHandle == HANDLE.MIDDLE_LEFT ? mPaintRed : mPaintBlue);

        mCenter = new RectF(mLeft + ((mRight - mLeft) / 2) - size,
                mTop + ((mBottom - mTop) / 2) - size, mLeft + ((mRight - mLeft) / 2) + size,
                mTop + ((mBottom - mTop) / 2) + size);
        canvas.drawArc(mCenter, 0, 360, true,
                mCurrentHandle == HANDLE.CENTER ? mPaintRed : mPaintBlue);

        mRectFHANDLEMap.put(mMiddleTop, HANDLE.MIDDLE_TOP);
        mRectFHANDLEMap.put(mMiddleRight, HANDLE.MIDDLE_RIGHT);
        mRectFHANDLEMap.put(mMiddleBottom, HANDLE.MIDDLE_BOTTOM);
        mRectFHANDLEMap.put(mMiddleLeft, HANDLE.MIDDLE_LEFT);
        mRectFHANDLEMap.put(mCenter, HANDLE.CENTER);

        {
            /*
            mTopLeft = new RectF(-size, -size, size, size);
            canvas.drawArc(mTopLeft, 0, 90, true, mCurrentHandle == HANDLE.TOP_LEFT ? mPaintRed :
             mPaintBlue);
            mTopRight = new RectF(width - size, -size, width + size, size);
            canvas.drawArc(mTopRight, 90, 90, true, mCurrentHandle == HANDLE.TOP_RIGHT ?
            mPaintRed : mPaintBlue);
            mBottomRight = new RectF(width - size, height - size, width + size, height + size);
            canvas.drawArc(mBottomRight, 180, 90, true, mCurrentHandle == HANDLE.BOTTOM_RIGHT ?
            mPaintRed : mPaintBlue);
            mBottomLeft = new RectF(-size, height - size, size, height + size);
            canvas.drawArc(mBottomLeft, 270, 90, true, mCurrentHandle == HANDLE.BOTTOM_LEFT ?
            mPaintRed : mPaintBlue);
            mRectFHANDLEMap.put(mTopLeft, HANDLE.TOP_LEFT);
            mRectFHANDLEMap.put(mTopRight, HANDLE.TOP_RIGHT);
            mRectFHANDLEMap.put(mBottomRight, HANDLE.BOTTOM_RIGHT);
            mRectFHANDLEMap.put(mBottomLeft, HANDLE.BOTTOM_LEFT);
             */
        }
    }

    private int getScreenWidth()
    {
        final WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        final Display display = wm.getDefaultDisplay();
        return display.getWidth();
    }

    private int getScreenHeight()
    {
        final WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        final Display display = wm.getDefaultDisplay();
        return display.getHeight();
    }

    @Override
    public boolean onTouchEvent(final MotionEvent event)
    {
        final int action = event.getActionMasked();
        final float x = event.getX();
        final float y = event.getY();

        switch (action)
        {
            case MotionEvent.ACTION_DOWN:
                mCurrentHandle = getClosestHandle(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mCurrentHandle = HANDLE.NONE;
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                move(x, y);
                invalidate();
                break;
        }
        return true;
    }

    private void move(final float x, final float y)
    {
        if (mCurrentHandle == HANDLE.NONE)
        {
            return;
        }

        if (mCurrentHandle == HANDLE.MIDDLE_TOP)
        {
            if (mBottom - y < mHeight && y >= mBoundTop)
            {
                mTop = y;
            }
        }
        else if (mCurrentHandle == HANDLE.MIDDLE_BOTTOM)
        {
            if (y - mTop < mHeight && y <= mBoundBottom)
            {
                mBottom = y;
            }
        }
        else if (mCurrentHandle == HANDLE.MIDDLE_LEFT)
        {
            if (mRight - x < mWidth && x >= mBoundLeft)
            {
                mLeft = x;
            }
        }
        else if (mCurrentHandle == HANDLE.MIDDLE_RIGHT)
        {
            if (x - mLeft < mWidth && x <= mBoundRight)
            {
                mRight = x;
            }
        }
        else if (mCurrentHandle == HANDLE.CENTER)
        {
            final float currentHeight = mBottom - mTop;
            final float currentWidth = mRight - mLeft;

            final float newTop = y - currentHeight / 2;
            final float newBottom = newTop + currentHeight;
            if (newTop > mBoundTop && newBottom < mBoundBottom)
            {
                mTop = newTop;
                mBottom = newBottom;
            }

            final float newLeft = x - currentWidth / 2;
            final float newRight = newLeft + currentWidth;
            if (newLeft > mBoundLeft && x + currentWidth / 2 < mBoundRight)
            {
                mLeft = x - currentWidth / 2;
                mRight = newRight;
            }
        }

    }

    public HANDLE getClosestHandle(final float x, final float y)
    {
        for (final RectF rect : mRectFHANDLEMap.keySet())
        {
            if (rect.contains(x, y))
            {
                return mRectFHANDLEMap.get(rect);
            }
        }
        return HANDLE.NONE;
    }

    public int[] getCurrentBounds()
    {
        final int[] bounds = new int[4];
        bounds[0] = (int) (mTop - mBoundTop);
        bounds[1] = (int) (mRight - mBoundLeft);
        bounds[2] = (int) (mBottom - mBoundTop);
        bounds[3] = (int) (mLeft - mBoundLeft);
        return bounds;
    }

    public void reset()
    {
        mInitialized = false;
        invalidate();
    }

    private enum HANDLE
    {
        TOP_LEFT, TOP_RIGHT, BOTTOM_RIGHT, BOTTOM_LEFT,
        MIDDLE_TOP, MIDDLE_RIGHT, MIDDLE_BOTTOM, MIDDLE_LEFT,
        CENTER, NONE
    }

}
