package com.thomasgravina.imagecropper;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.util.UUID;

public class CropActivity extends ActionBarActivity
{

    public static final String CROPPED_FILE_URI = "cropped_uri";
    public static final String ORIG_FILE_URI = "orig_uri";
    public static final String COMPRESSION = "compression";
    private ImageView mImageView;
    private CropperView mCropperView;
    private ImageButton mOk, mCancel, mReset;

    private String mOriginalPath;
    private String mCroppedPath;
    private int mCompression = 60;
    private Bitmap mBitmap;

    private int mOriginalWidth;
    private int mOriginalHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);

        if (!getIntent().hasExtra(ORIG_FILE_URI))
        {
            throw new NullPointerException("Please provide original image URI as a parameters.");
        }

        mOriginalPath = getIntent().getStringExtra(ORIG_FILE_URI);
        mCompression = getIntent().getIntExtra(COMPRESSION, 60);

        mImageView = (ImageView) findViewById(R.id.image);
        mCropperView = (CropperView) findViewById(R.id.cropper);
        mOk = (ImageButton) findViewById(R.id.ok);
        mCancel = (ImageButton) findViewById(R.id.cancel);
        mReset = (ImageButton) findViewById(R.id.reset);

        mBitmap = BitmapFactory.decodeFile(mOriginalPath);

        mOriginalWidth = mBitmap.getWidth();
        mOriginalHeight = mBitmap.getHeight();

        mImageView.setImageBitmap(mBitmap);
        mOk.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View view)
            {

                final int[] bounds = mCropperView.getCurrentBounds();
                final String croppedPath = cropAndSaveBitmap(bounds);
                final Intent intent = new Intent();
                intent.putExtra(CROPPED_FILE_URI, croppedPath);
                if (croppedPath != null)
                {
                    setResult(RESULT_OK, intent);
                }
                else
                {
                    setResult(RESULT_CANCELED);
                }
                finish();
            }
        });
        mCancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View view)
            {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
        mReset.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View view)
            {
                mImageView.setImageBitmap(mBitmap);
                onWindowFocusChanged(false);
                mCropperView.reset();
            }
        });
    }

    private String cropAndSaveBitmap(final int[] bounds)
    {
        final float ratio = mOriginalHeight / mCropperView.getImageHeight();
        final int x = (int) (ratio * bounds[3]);
        final int y = (int) (ratio * bounds[0]);
        final int width = (int) (ratio * bounds[1] - x);
        final int height = (int) (ratio * bounds[2] - y);
        final Bitmap cropped = Bitmap.createBitmap(mBitmap, x, y, width, height);
        return saveBitmapToFile(cropped);
    }

    @Override
    public void onWindowFocusChanged(final boolean hasFocus)
    {
        super.onWindowFocusChanged(hasFocus);

        final float[] f = new float[9];
        mImageView.getImageMatrix().getValues(f);

        // Extract the scale values using the constants (if aspect ratio maintained,
        // scaleX == scaleY)
        final float scaleX = f[Matrix.MSCALE_X];
        final float scaleY = f[Matrix.MSCALE_Y];

        // Get the drawable (could also get the bitmap behind the drawable and getWidth/getHeight)
        final Drawable d = mImageView.getDrawable();
        final int originalWidth = d.getIntrinsicWidth();
        final int originalHeight = d.getIntrinsicHeight();

        // Calculate the actual dimensions
        final int actualWidth = Math.round(originalWidth * scaleX);
        final int actualHeight = Math.round(originalHeight * scaleY);

        mCropperView.init(actualWidth, actualHeight);
    }

    private String saveBitmapToFile(final Bitmap cropped)
    {
        final String filename = "cropped-" + UUID.randomUUID() + ".jpg";
        mCroppedPath = getFilesDir() + "/imagecropper/" + filename;
        try
        {
            final File file = new File(mCroppedPath);
            file.getParentFile().mkdirs();
            file.createNewFile();
            final FileOutputStream out = new FileOutputStream(file);
            cropped.compress(Bitmap.CompressFormat.PNG, mCompression, out);
            out.close();
            return mCroppedPath;
        }
        catch (final Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

}
