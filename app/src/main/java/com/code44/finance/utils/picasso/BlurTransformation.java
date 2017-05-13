package com.code44.finance.utils.picasso;

import android.graphics.Bitmap;

import com.squareup.picasso.Transformation;

public class BlurTransformation implements Transformation {
    public BlurTransformation() {
    }

    @Override public Bitmap transform(Bitmap bitmap) {
        // Let's create an empty bitmap with the same size of the bitmap we want to blur
        //        Bitmap outBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);

        //        RenderScript renderScript = RenderScript.create(context);
        //
        //        // Create an Intrinsic Blur Script using the Renderscript
        //        ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript));
        //
        //        // Create the in/out Allocations with the Renderscript and the in/out bitmaps
        //        Allocation allIn = Allocation.createFromBitmap(renderScript, bitmap);
        //        Allocation allOut = Allocation.createFromBitmap(renderScript, outBitmap);
        //
        //        // Set the radius of the blur
        //        blurScript.setRadius(25.0f);
        //
        //        // Perform the Renderscript
        //        blurScript.setInput(allIn);
        //        blurScript.forEach(allOut);
        //
        //        // Copy the final bitmap created by the out Allocation to the outBitmap
        //        allOut.copyTo(outBitmap);
        //
        //        // Recycle the original bitmap
        //        bitmap.recycle();
        //
        //        // After finishing everything, we destroy the Renderscript.
        //        renderScript.destroy();

        //        Bitmap result = Bitmap.createScaledBitmap(outBitmap, outBitmap.getWidth() / 8, outBitmap.getHeight() / 8, true);
        //        outBitmap.recycle();

        //        return result;
        return bitmap;
    }

    @Override public String key() {
        return "blur";
    }
}
