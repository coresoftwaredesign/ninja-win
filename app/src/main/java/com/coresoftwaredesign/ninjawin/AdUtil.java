package com.coresoftwaredesign.ninjawin;

import android.content.Context;
import android.util.DisplayMetrics;
import android.widget.FrameLayout;

import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.admanager.AdManagerAdRequest;
import com.google.android.gms.ads.admanager.AdManagerAdView;

public class AdUtil {

    public AdSize getAdSize(FrameLayout adContainerView) {
        DisplayMetrics outMetrics = new DisplayMetrics();
        adContainerView.getDisplay().getMetrics(outMetrics);

        float density = outMetrics.density;

        float adWidthPixels = adContainerView.getWidth();

        if (adWidthPixels == 0) {
            adWidthPixels = outMetrics.widthPixels;
        }

        int adWidth = (int) (adWidthPixels / density);
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(adContainerView.getContext(), adWidth);
    }

    public AdManagerAdView loadAdaptiveBanner(FrameLayout adContainerView, AdSize adSize) {
        Context context = adContainerView.getContext();
        AdManagerAdView adView = new AdManagerAdView(context);

        adView.setAdUnitId(BuildConfig.adUnitIdMain);
        adView.setBackgroundColor(context.getResources().getColor(R.color.colorPrimaryDark));

        adContainerView.removeAllViews();
        adContainerView.addView(adView);
        adView.setAdSizes(adSize);

        AdManagerAdRequest adRequest = new AdManagerAdRequest.Builder().build();
        adView.loadAd(adRequest);
        return adView;
    }
}
