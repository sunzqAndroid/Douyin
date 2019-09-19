package com.douyin;

import android.support.v4.view.ViewPager;
import android.view.View;

public class SimpleTransformation implements ViewPager.PageTransformer {
    @Override
    public void transformPage(View page, float position) {
        Object tag = page.getTag();
        if (tag != null && tag instanceof ViewPager.PageTransformer) {
            ViewPager.PageTransformer transformer = (ViewPager.PageTransformer) tag;
            transformer.transformPage(page, position);
        }
    }
}