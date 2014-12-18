package com.op.ripplepricer.advancedwidget;



/**
 * LargeTextWidget is based on SimpleTextWidget, providing its own layout and parameters
 *
 */
public class LargeTextWidget extends SmallTextWidget {

    public LargeTextWidget(WidgetBundle bundle) {
        super(bundle);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.widget_text_box_large;
    }

    @Override
    public int getWidth() {
        return WIDTH_LARGE;
    }

    @Override
    public int getHeight() {
        return HEIGHT_LARGE;
    }

    @Override
    public int getPreviewUri() {
        return R.drawable.preview_large;
    }

    @Override
    public int getName() {
        return R.string.widget_name_2_5;
    }

}
