package com.josephwang.util.ui;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.PowerManager;
import android.text.Html;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * JosephWang
 */
public class UIAdjuster
{
    public static float[] getScaleWidth(int viewWidth, int viewHeight, int bitmapWidth, int bitmapHeight)
    {
        float[] widthHeight = new float[2];
        if (bitmapWidth > viewWidth)
        {
            int width = viewWidth;
            int height = viewHeight;

            //Calculate the max changing amount and decide which dimension to use
            float widthRatio = (float) bitmapWidth / (float) width;
            float heightRatio = (float) bitmapHeight / (float) height;

            //Use the ratio that will fit the image into the desired sizes
            int finalWidth = (int) Math.floor(width * widthRatio);
            int finalHeight = (int) Math.floor(height * widthRatio);

            if (finalWidth > bitmapWidth || finalHeight > bitmapHeight)
            {
                finalWidth = (int) Math.floor(width * heightRatio);
                finalHeight = (int) Math.floor(height * heightRatio);
            }
            widthHeight[0] = finalWidth;
            widthHeight[1] = finalHeight;
        }
        return widthHeight;
    }

    /**
     * 要在 dispatchTouchEvent 使用
     *
     * @param activity
     * @param event
     */
    public static void closeKeyBoardOnTouchOutSide(Activity activity, MotionEvent event)
    {
        if (event.getAction() == MotionEvent.ACTION_DOWN)
        {
            View v = activity.getCurrentFocus();
            if (v instanceof EditText)
            {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY()))
                {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
    }

    public static void setUrlMarkText(TextView view, String res)
    {
        Pattern urlPattern = Pattern.compile("(?:^|[\\W])((ht|f)tp(s?):\\/\\/|www\\.|tw.)(([\\w\\-]+\\.){1,}?([\\w\\-.~]+\\/?)*" + "[\\p{Alnum}.,%_=?&#\\-+()\\[\\]\\*$~@!:/{};']*)",
                Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
        Matcher matcher = urlPattern.matcher(res);
        if (matcher.find())
        {
            String urlText = res.substring(matcher.start(), matcher.end());
            String result = "<font color='#FFFFFF'>" + res.substring(0, matcher.start()) + "</font>" + "<a href='" + urlText + "'> " + urlText + "</a> " + "<font color='#FFFFFF'>"
                    + res.substring(matcher.end(), res.length()) + "</font>";
            view.setText(Html.fromHtml(result));
        }
    }

    public static int getNavigationHeight(Context ctx)
    {
        Resources resources = ctx.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0)
        {
            return resources.getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    public static int getStatusBarHeight2(Context context)
    {
        int statusBarHeight2 = -1;
        if (statusBarHeight2 == -1)
        {
            try
            {
                Class<?> cl = Class.forName("com.android.internal.R$dimen");
                Object obj = cl.newInstance();
                Field field = cl.getField("status_bar_height");

                int x = Integer.parseInt(field.get(obj).toString());
                statusBarHeight2 = context.getResources()
                        .getDimensionPixelSize(x);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        return statusBarHeight2;
    }

    public static int getDisplayAreaHeight(Context context)
    {
        return getScreenHeight(context) - getStatusBarHeight2(context);
    }

    public static int getScreenWidth(Context context)
    {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight(Context context)
    {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    public static int sp2px(Context context, float spValue)
    {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }


    public static void showClickAnimation(final View v)
    {
        v.clearAnimation();
        AlphaAnimation anim = new AlphaAnimation(0.5f, 1.2f);
        anim.setDuration(120);
        anim.setFillEnabled(true);
        anim.setFillAfter(true);
        v.startAnimation(anim);
        anim.setAnimationListener(new Animation.AnimationListener()
        {
            @Override
            public void onAnimationStart(Animation animation)
            {

            }

            @Override
            public void onAnimationEnd(Animation animation)
            {
                animation.cancel();
                v.clearAnimation();
            }

            @Override
            public void onAnimationRepeat(Animation animation)
            {

            }
        });
    }

    public static void adjustViewStatus(View view, boolean enable)
    {
        if (view != null)
        {
            view.setClickable(enable);
            view.setEnabled(enable);
        }
    }

    public static void closeKeyBoardForcely(Activity act)
    {
        if (act.getCurrentFocus() != null)
        {
            InputMethodManager imm = (InputMethodManager) act.getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        }
    }

    public static Drawable getDrawable(Context ctx, int id)
    {
        return ctx.getResources().getDrawable(id);
    }

    public static void closeKeyBoard(Activity act)
    {
        if (act != null)
        {
            InputMethodManager imm = ((InputMethodManager) act.getSystemService(Context.INPUT_METHOD_SERVICE));
            if (act.getCurrentFocus() != null && act.getCurrentFocus().getWindowToken() != null)
            {
                imm.hideSoftInputFromWindow(act.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
            if (act.getWindow() != null)
            {
                act.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            }
        }
    }

    public static void showKeyBoard(Activity act)
    {
        ((InputMethodManager) act.getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    public static void closeKeyBoard(Activity act, EditText edit)
    {
        InputMethodManager imm = ((InputMethodManager) act.getSystemService(Context.INPUT_METHOD_SERVICE));
        if (edit.getWindowToken() != null)
        {
            imm.hideSoftInputFromWindow(edit.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @SuppressWarnings("deprecation")
    public static void keepScreenOn(Context ctx)
    {
        PowerManager pm = (PowerManager) ctx.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "ConnectionUtil");
        mWakeLock.acquire();
    }

    @SuppressWarnings("deprecation")
    public static void closeScreenOn(Context ctx)
    {
        PowerManager pm = (PowerManager) ctx.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "ConnectionUtil");
        mWakeLock.release();
    }

    public static void restartInput(Activity act, EditText edit)
    {
        InputMethodManager imm = ((InputMethodManager) act.getSystemService(Context.INPUT_METHOD_SERVICE));
        imm.restartInput(edit);
    }


    @SuppressWarnings("deprecation")
    public static float getDeviceWidth(Context context)
    {
        Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        return display.getWidth();
    }

    @SuppressWarnings("deprecation")
    public static float getDeviceWidthDip(Context context)
    {
        Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        return pxToDp(display.getWidth(), context);
    }


    @SuppressWarnings("deprecation")
    public static float getDeviceHeight(Context context)
    {
        Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        return display.getHeight();
    }

    @SuppressWarnings("deprecation")
    public static float getDeviceHeightDip(Context context)
    {
        Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        return pxToDp(display.getHeight(), context);
    }

    /**
     * This method converts device specific pixels to density independent
     * pixels.
     *
     * @param px      A value in px (pixels) unit. Which we need to convert into db
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent dp equivalent to px value
     */
    public static float pxToDp(float px, Context context)
    {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / (metrics.densityDpi / 160f);
        return dp;
    }


    public static int dip2px(Context context, float dipValue)
    {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public static float dip2pxFloat(Context context, float dipValue)
    {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (dipValue * scale + 0.5f);
    }


    public static int px2dip(Context context, float pxValue)
    {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int setListViewHeightBasedOnChildren(ListView listView)
    {
        int height = 0;
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
        {
            return height;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++)
        {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1)) + listView.getPaddingTop() + listView.getPaddingBottom();
        height = params.height;
        listView.setLayoutParams(params);
        listView.requestLayout();
        return height;
    }

    public static int setListViewHeightBasedOnChildren(GridView listView)
    {
        int height = 0;
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
        {
            return height;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++)
        {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + listView.getPaddingTop() + listView.getPaddingBottom();
        height = params.height;
        listView.setLayoutParams(params);
        listView.requestLayout();
        return height;
    }

    public static void setListViewWidth(ListView listView, int width)
    {
        LayoutParams params = listView.getLayoutParams();
        params.width = width;
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    public static void setViewGroupWidthAndHeight(ViewGroup group, int width, int height)
    {
        LayoutParams params = group.getLayoutParams();
        if (width == 0)
        {
            params.width = LayoutParams.WRAP_CONTENT;
        }
        else
        {
            params.width = width;
        }

        if (height == 0)
        {
            params.height = LayoutParams.WRAP_CONTENT;
        }
        else
        {
            params.height = height;
        }
        group.setLayoutParams(params);
        group.requestLayout();
    }

    public static void setViewWidthAndHeight(View group, int width, int height)
    {
        LayoutParams params = group.getLayoutParams();
        if (width == 0)
        {
            params.width = LayoutParams.WRAP_CONTENT;
        }
        else
        {
            params.width = width;
        }

        if (height == 0)
        {
            params.height = LayoutParams.WRAP_CONTENT;
        }
        else
        {
            params.height = height;
        }
        group.setLayoutParams(params);
        group.requestLayout();
    }

    public static int setListViewHeightBasedOnChildren(ListView listView, int surplus)
    {
        int height = 0;
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
        {
            return height;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++)
        {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1)) + listView.getPaddingTop() + listView.getPaddingBottom() + surplus;
        height = params.height;
        listView.setLayoutParams(params);
        listView.requestLayout();
        return height;
    }


    public static int getListViewHeightBasedOnChildren(ListView listView)
    {
        int height = 0;
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
        {
            return height;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++)
        {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1)) + listView.getPaddingTop() + listView.getPaddingBottom();
        return height;
    }

    public static int setListViewHeightBasedOnChildren(ListView listView, View footView)
    {
        int height = 0;
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
        {
            return height;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++)
        {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        footView.measure(0, 0);
        LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1)) + listView.getPaddingTop() + listView.getPaddingBottom() + footView.getMeasuredHeight();
        height = params.height;
        listView.setLayoutParams(params);
        listView.requestLayout();
        return height;
    }

    public static int getListViewLastRowWHeight(ListView listView)
    {
        int height = 0;
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
        {
            return height;
        }
        View listItem = listAdapter.getView(listAdapter.getCount() - 1, null, listView);
        listItem.measure(0, 0);
        int totalHeight = listItem.getMeasuredHeight();
        return totalHeight;
    }

    public static void addViewIfNotNull(ViewGroup group, View view)
    {
        if (view != null)
        {
            group.addView(view);
        }
    }

    public static void expand(final View v, Animation.AnimationListener listener)
    {
        v.measure(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        final int targetHeight = v.getMeasuredHeight();

        v.getLayoutParams().height = 0;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t)
            {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? LayoutParams.WRAP_CONTENT
                        : (int) (targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds()
            {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int) (targetHeight / (v.getContext().getResources().getDisplayMetrics().density * 5)));
        if (listener != null)
        {
            a.setAnimationListener(listener);
        }
        v.startAnimation(a);
    }

    public static void expand(final View v)
    {
        expand(v, null);
    }

    public static void collapse(final View v, Animation.AnimationListener listener)
    {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t)
            {
                if (interpolatedTime == 1)
                {
                    v.setVisibility(View.GONE);
                }
                else
                {
                    v.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds()
            {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int) (initialHeight / (v.getContext().getResources().getDisplayMetrics().density * 5)));
        if (listener != null)
        {
            a.setAnimationListener(listener);
        }
        v.startAnimation(a);
    }

    public static void collapse(final View v)
    {
        collapse(v, null);
    }


    public static float getSpTextSize(Context ctx, float spTextSize)
    {
        float textSize = spTextSize * ctx.getResources().getDisplayMetrics().scaledDensity;
        return textSize;
    }


    public static boolean toggleVisibility(View v)
    {
        switch (v.getVisibility())
        {
            case View.VISIBLE:
                v.setVisibility(View.GONE);
                break;
            default:
                v.setVisibility(View.VISIBLE);
                break;
        }
        return v.getVisibility() == View.VISIBLE;
    }


    public static void adjustViewParamsInPixel(ViewGroup group, int pixel)
    {
        LayoutParams params = group.getLayoutParams();
        params.width = (int) pixel;
        group.setLayoutParams(params);
        group.requestLayout();
    }

    public static void toogleViewEnable(View view, boolean enable, View.OnClickListener clickListener)
    {
        if (enable)
        {
            view.setOnClickListener(clickListener);
            view.setAlpha(1.0f);
        }
        else
        {
            view.setOnClickListener(null);
            view.setAlpha(0.5f);
        }
    }

    public static void disableViewAction(View view)
    {
        view.setAlpha(0.5f);
        view.setEnabled(false);
        view.setOnTouchListener(null);
        view.setOnClickListener(null);
    }

    public static void clearImage(ImageView image)
    {
        if (image != null)
        {
            image.setBackgroundDrawable(null);
            image.setImageBitmap(null);
        }
    }

    public static void recycleImage(ImageView image)
    {
        if (image != null)
        {
            if (image.getDrawable() != null &&
                    image.getDrawable() instanceof BitmapDrawable &&
                    ((BitmapDrawable) image.getDrawable()).getBitmap() != null &&
                    !((BitmapDrawable) image.getDrawable()).getBitmap().isRecycled())
            {

                ((BitmapDrawable) image.getDrawable()).getBitmap().recycle();
            }
            image.setImageBitmap(null);
        }
    }

    public static boolean isEmpty(TextView editor)
    {
        return TextUtils.isEmpty(editor.getText().toString());
    }

    public static String getText(TextView textiew)
    {
        final String text = textiew.getText().toString();
        return text;
    }

}