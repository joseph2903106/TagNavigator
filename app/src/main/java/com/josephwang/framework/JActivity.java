package com.josephwang.framework;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.IntentCompat;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.josephwang.util.JLog;
import com.josephwang.util.JUtil;
import com.josephwang.util.ui.JDialog;
import com.josephwang.util.ui.UIAdjuster;
import com.tabnavigator.R;

import java.util.List;



/**
 * middle-ware of real Activity
 *
 * @author JosephWang
 */
public abstract class JActivity extends FragmentActivity
{
    protected String TAG = ((Object) this).getClass().getSimpleName();
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    protected ProgressDialog loading = null;
    private Dialog messageDialg;

    private boolean isFrontVisible = false;
    protected final Handler handler = new Handler();
    private boolean isAlive = true;

    public Handler getHandler()
    {
        return handler;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        isAlive = true;
        AppManager.addActivity(this);
        JUtil.allowNetWorkRunOnUI();
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        UIAdjuster.closeKeyBoard(this);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event)
    {
        UIAdjuster.closeKeyBoardOnTouchOutSide(this, event);
        return super.dispatchTouchEvent(event);
    }


    protected void setProgressInLoading(String progress)
    {
        if (loading != null)
        {
            ((TextView) loading.findViewById(R.id.progressTitle)).setText("" + progress);
        }
    }


    @Override
    protected void onResume()
    {
        super.onResume();
        isFrontVisible = true;
        UIAdjuster.closeKeyBoard(this);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        isFrontVisible = false;
    }

    public void dissmissMessageDialog()
    {
        if (messageDialg != null)
        {
            messageDialg.dismiss();
        }
    }

    @Override
    protected void onDestroy()
    {
        isAlive = false;
        cancelLoading();
        UIAdjuster.closeKeyBoard(this);
        handler.removeCallbacks(null);
        super.onDestroy();
    }

    public void startClearTopIntent(Intent intent, boolean isCloseStartActivity)
    {
        startClearTopIntent(intent, Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK, isCloseStartActivity);
    }

    public void startClearTopIntent(Intent intent)
    {
        startClearTopIntent(intent, Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK, true);
    }

    public void startClearTopIntent(Class<? extends FragmentActivity> inclass)
    {
        startClearTopIntent(new Intent(this, inclass), Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK, true);
    }

    protected void closeRepeatActivity()
    {
        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0)
        {
            finish();
        }
    }

    public void startActivity(Class<? extends FragmentActivity> inclass)
    {
        startActivity(new Intent(this, inclass));
    }

    public void startClearTopIntent(Intent intent, int action, boolean isCloseStartActivity)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
        {
            intent.setFlags(action);
            startActivity(intent);
        }
        else
        {
            ComponentName cn = intent.getComponent();
            Intent mainIntent = IntentCompat.makeRestartActivityTask(cn);
            startActivity(mainIntent);
        }
        if (isCloseStartActivity)
        {
            finish();
        }
    }

    @Override
    public void finish()
    {
        AppManager.removeRecord(this);
        JLog.d(" finish " + TAG);
        innerFinish();
    }

    public void backAction()
    {
    }

    public void onFinish()
    {
    }

    protected void onErrorMessageClick()
    {
    }

    protected void innerFinish()
    {
        onFinish();
        cancelLoading();
        clearAllFragment();
        super.finish();
        isAlive = false;
        if (!isLastOneActivity())
        {
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }
    }

    public void finishWithAnimation(boolean hasAnimation)
    {
        if (hasAnimation)
        {
            innerFinish();
        }
        else
        {
            onFinish();
            super.finish();
        }
    }

    public boolean isLastOneActivity()
    {
       return AppManager.isLastOneActivity(this);
    }

    /**
     * showLoading ,check network state and set Timer of timeout
     *
     * @return true
     */
    public boolean showLoading()
    {
        return showLoading("");
    }

    public boolean showLoading(String text)
    {
        if (loading == null && getBaseContext() != null && !isFinishing())
        {
            loading = JDialog.showProgressDialog(this, "" + text, false);
        }
        return true;
    }

    public boolean isReclaim()
    {
//		return isFinishing() || isDestroyed();
        return isFinishing();
    }

    public void updateLoading(String statusText)
    {
        if (loading == null)
        {
            loading = JDialog.showProgressDialog(this, "" + statusText, false);
        }
        TextView status = (TextView) loading.findViewById(R.id.progressTitle);
        status.setText(statusText);
    }

    public boolean hasInternet()
    {
        return hasInternet(true);
    }

    public boolean hasInternet(boolean showAlert)
    {
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connManager.getActiveNetworkInfo();
        if (info == null || !info.isConnected() || !info.isAvailable())
        {
            cancelLoading();
            if (showAlert)
            {
                if (messageDialg != null)
                {
                    messageDialg.dismiss();
                }
                messageDialg = JDialog.showMessage(this,
                        "訊息",
                        "無網路連線",
                        new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                onErrorMessageClick();
                                dialog.dismiss();
                            }
                        });
            }
            return false;
        }
        else
        {
            return true;
        }
    }


    /**
     * dismiss ProgressDialog
     */
    public void cancelLoading()
    {
        if (loading != null && loading.isShowing())
        {
            loading.dismiss();
            loading = null;
        }
    }

    protected void onError()
    {
        backPress();
    }

    protected boolean backPress()
    {
        finish();
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK ||
                event.getAction() == KeyEvent.KEYCODE_BACK)
        {
            backAction();
            if (backPress())
            {
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    public Bitmap takeScreenShot()
    {
        return takeScreenShot(findViewById(android.R.id.content).getRootView());
    }

    public Bitmap takeScreenShot(View rootView)
    {
        rootView.destroyDrawingCache();
        rootView.setDrawingCacheEnabled(true);
        rootView.buildDrawingCache(true);
        rootView.invalidate();
        final Bitmap map = rootView.getDrawingCache(true).copy(Bitmap.Config.ARGB_8888, false);
        rootView.setDrawingCacheEnabled(false);
        rootView.destroyDrawingCache();
        return map;
    }

    public void commitFragment(int replaceId, Fragment fragment)
    {
        commitFragment(replaceId,  fragment, FragmentAnimationType.None);
    }

    public void commitFragment(int replaceId, Fragment fragment, FragmentAnimationType type)
    {
        commitFragment(replaceId, fragment, fragment.getClass().getSimpleName(), type);
    }

    public void commitFragment(int replaceId, Fragment fragment, String tag, FragmentAnimationType type)
    {
        commitFragment(replaceId, fragment, tag, type, false);
    }

    public void commitFragment(int replaceId, Fragment fragment, String tag, FragmentAnimationType type, boolean keepInHistory)
    {
        if (!isFinishing() && isAlive() && getSupportFragmentManager() != null)
        {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            switch (type)
            {
                case LeftIn:
                    transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
                    break;
                case RightIn:
                    transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
                    break;
                case TopDown:
                    transaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_down);
                    break;
                case BottomRise:
                    transaction.setCustomAnimations(R.anim.slide_down, R.anim.slide_up);
                    break;
                case None:
                    break;
            }
            if (hasSameFragment(fragment))
            {
                Fragment historyFragment = getHistoryFragment(fragment.getClass());
                transaction.replace(replaceId, historyFragment, tag);
            }
            else
            {
                if (keepInHistory)
                {
                    transaction.add(replaceId, fragment, tag);
                }
                else
                {
                    transaction.replace(replaceId, fragment, tag);
                }
            }

            if (keepInHistory)
            {
                transaction.addToBackStack(tag);
            }
            else
            {
                transaction.addToBackStack(null);
            }

            // transaction.commit();
            /*****
             * 修正Bug java.lang.IllegalStateException: Can not perform this action
             * after onSaveInstanceState
             ********/
            transaction.commitAllowingStateLoss();
        }
    }

    public <T extends Fragment> T getHistoryFragment(Class<T> fragment)
    {
        return getHistoryFragment(fragment.getSimpleName());
    }

    public <T extends Fragment> T getHistoryFragment(String tag)
    {
        if (getSupportFragmentManager() != null)
        {
            T current = (T) getSupportFragmentManager().findFragmentByTag(tag);
            if (current != null)
            {
                return current;
            }
        }
        return null;
    }

    public void clearFragment(Class<? extends Fragment> fragment)
    {
        if (getSupportFragmentManager() != null)
        {
            Fragment current = getSupportFragmentManager().findFragmentByTag(fragment.getSimpleName());
            if (current != null)
            {
                JLog.d(TAG + " clearFragment Fragment " + current.getClass().getSimpleName());
                current.onDestroyView();
            }
        }
    }

    public boolean hasSameFragment(Fragment fragment)
    {
        if (getSupportFragmentManager() != null)
        {
            Fragment current = getSupportFragmentManager().findFragmentByTag(fragment.getClass().getSimpleName());
            if (current != null)
            {
                return true;
            }
        }
        return false;
    }

    public boolean hasSameFragment(Class<? extends Fragment> fragment)
    {
        if (getSupportFragmentManager() != null)
        {
            Fragment current = getSupportFragmentManager().findFragmentByTag(fragment.getSimpleName());
            if (current != null)
            {
                return true;
            }
        }
        return false;
    }

    public void removeFragment(Class<? extends Fragment> fragments, boolean clear)
    {
        if (notEmpyFragments())
        {
            Fragment fragment = getSupportFragmentManager().findFragmentByTag(fragments.getSimpleName());
            JLog.d(JLog.JosephWang, TAG + " removeFragment " + (fragment != null));
            if (fragment != null)
            {
                getSupportFragmentManager().beginTransaction().remove(fragment).commit();
            }

            if (clear)
            {
                getSupportFragmentManager().getFragments().clear();
            }
        }
    }

    public void removeFragment(Class<? extends Fragment> fragments)
    {
        removeFragment(fragments, true);
    }

    public void clearAllFragment()
    {
        if (notEmpyFragments())
        {
            getSupportFragmentManager().getFragments().clear();
        }
    }

    public boolean isFragmentVisible(Fragment fragment)
    {
        return isFragmentVisible(fragment.getClass());
    }

    public boolean isFragmentVisible(Class<? extends Fragment> fragment)
    {
        if (notEmpyFragments())
        {
            Fragment current = getHistoryFragment(fragment.getClass().getSimpleName());
            if (current != null && current.isVisible())
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        return false;
    }

    public boolean notEmpyFragments()
    {
        return (getSupportFragmentManager() != null &&
                JUtil.notEmpty(getSupportFragmentManager().getFragments()));
    }

    public <T extends View> T getView(int id)
    {
        return (T) getWindow().findViewById(id);
    }

    public boolean isCurrentFragmentExist(Fragment fragment)
    {
        if (getSupportFragmentManager() != null)
        {
            Fragment current = getSupportFragmentManager().findFragmentByTag(fragment.getClass().getSimpleName());
            if (current != null)
            {
                return true;
            }
        }
        return false;
    }

    public boolean isCurrentFragmentExist(Class<? extends Fragment> fragment)
    {
        if (getSupportFragmentManager() != null)
        {
            Fragment current = getSupportFragmentManager().findFragmentByTag(fragment.getSimpleName());
            if (current != null)
            {
                return true;
            }
        }
        return false;
    }

    public <T extends Fragment> T getCurrentFragment()
    {
        if (getSupportFragmentManager() != null)
        {
            List<Fragment> fragments = getSupportFragmentManager().getFragments();
            Fragment current = null;
            for (int i = fragments.size() - 1; i >= 0; i--)
            {
                current = fragments.get(i);
                if (current.isVisible())
                {
                    return (T) current;
                }
            }
        }
        return null;
    }

    public boolean isAlive()
    {
        return isAlive;
    }

    @SuppressWarnings("deprecation")
    protected void exitApp()
    {
        int pid = android.os.Process.myPid();
        android.os.Process.killProcess(pid);
    }

    public boolean isFrontVisible()
    {
        return isFrontVisible;
    }
}