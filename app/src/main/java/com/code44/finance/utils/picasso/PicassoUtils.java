package com.code44.finance.utils.picasso;

import android.graphics.drawable.ColorDrawable;
import android.widget.ImageView;

import com.code44.finance.R;
import com.code44.finance.utils.ThemeUtils;
import com.google.common.base.Strings;
import com.squareup.picasso.Picasso;

public final class PicassoUtils {
    private PicassoUtils() {
    }

    public static void loadUserCover(ImageView imageView, String url) {
        if (Strings.isNullOrEmpty(url)) {
            Picasso.with(imageView.getContext())
                    .load("http://lorempixel.com/900/500/abstract/")
                    .fit()
                    .centerCrop()
                    .placeholder(new ColorDrawable(ThemeUtils.getColor(imageView.getContext(), R.attr.colorAccentDark)))
                    .transform(new ChainedTransformation(new BlurTransformation(), new OverlayTransformation(0x77000000)))
                    .into(imageView);
        } else {
            Picasso.with(imageView.getContext())
                    .load(url)
                    .fit()
                    .centerCrop()
                    .placeholder(new ColorDrawable(ThemeUtils.getColor(imageView.getContext(), R.attr.colorAccentDark)))
                    .transform(new ChainedTransformation(new BlurTransformation(), new OverlayTransformation(0x77000000)))
                    .into(imageView);
        }
    }

    public static void loadUserPhoto(ImageView imageView, String url) {
        String photoUrl = url;
        if (Strings.isNullOrEmpty(photoUrl)) {
            photoUrl = "http://lorempixel.com/160/160/abstract/";
        }
        Picasso.with(imageView.getContext())
                .load(photoUrl)
                .fit()
                .centerCrop()
                .transform(new CircleBorderTransformation(imageView.getContext()
                                                                  .getResources()
                                                                  .getDimension(R.dimen.divider), ThemeUtils.getColor(imageView.getContext(), R.attr.backgroundColorPrimary)))
                .into(imageView);
    }
}
