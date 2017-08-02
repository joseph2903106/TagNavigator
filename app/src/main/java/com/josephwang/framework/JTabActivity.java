package com.josephwang.framework;

import android.support.v4.app.Fragment;
import android.view.KeyEvent;

import com.josephwang.util.JLog;


/**
 * Created by josephwang on 2017/3/28.
 */

public abstract class JTabActivity extends JActivity implements ITabHostTransaction
{
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK ||
                event.getAction() == KeyEvent.KEYCODE_BACK)
        {
            Fragment fragment = getCurrentFragment();

            JLog.d("Tab onKeyDown fragment != null " + (fragment != null));
            if (fragment != null)
            {
                JLog.d("Tab onKeyDown fragment getSimpleName " + (fragment.getClass().getSimpleName()));
            }

            if (fragment != null &&
                    fragment instanceof JChildFragment)
            {
                JChildFragment child = (JChildFragment) fragment;
                JParentFragment parent = child.getJParentFragment();
                if (child.hasChildFragment())
                {
                    if (parent.getHistoryList().size() == 1)
                    {
                        showExitReminid();
                    }
                    else
                    {
                        child.backToPreviousFragment();
                    }
                    return true;
                }
                else
                {
                    child.backToPreviousFragment(parent.getClass());
                    return true;
                }
            }
            else
            {
                showExitReminid();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    public void showExitReminid()
    {
    }

    public void commitCurrentFragmentByParent(JParentFragment fragment)
    {
        commitCurrentFragmentByParent(fragment, FragmentAnimationType.None);
    }

    public void commitCurrentFragmentByParent(JParentFragment fragment, FragmentAnimationType type)
    {
        if (getHistoryFragment(fragment.getClass()) != null)
        {
            JParentFragment parent = getHistoryFragment(fragment.getClass());
            commitFragmentTransaction(parent.getCurrentFragment(), type);
        }
        else
        {
            commitFragmentTransaction(fragment, type);
        }
    }

    public void commitParentFragment(JParentFragment fragment)
    {
        commitParentFragment(fragment, FragmentAnimationType.None);
    }

    public void commitParentFragment(JParentFragment fragment, FragmentAnimationType type)
    {
        if (getHistoryFragment(fragment.getClass()) != null)
        {
            JParentFragment parent = getHistoryFragment(fragment.getClass());
            parent.clearHistory();
            parent.setCurrentFragment(parent);
            commitFragmentTransaction(parent, type);
        }
        else
        {
            commitFragmentTransaction(fragment, type);
        }
    }

    public abstract int getFragmentId();

    @Override
    public void commitFragmentTransaction(JFragment fragment, FragmentAnimationType type)
    {
        commitFragment(getFragmentId(), fragment, type);
    }
}
