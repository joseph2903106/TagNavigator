package com.josephwang.framework;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.josephwang.util.JLog;
import com.josephwang.util.JUtil;

import java.util.ArrayList;

/**
 * Created by josephwang on 2017/3/28.
 */

public abstract class JParentFragment extends JFragment
{
    protected final ArrayList<JFragment> historyList = new ArrayList<JFragment>();
    protected JFragment current;

    public final ArrayList<JFragment> getHistoryList()
    {
        return historyList;
    }

    public final void addHistory(JFragment fragment)
    {
        historyList.add(fragment);
    }

    public final void popUpHistory()
    {
        if (JUtil.notEmpty(historyList))
        {
            historyList.remove(getHistoryListLength() - 1);
        }
    }

    public int getHistoryListLength()
    {
        return historyList.size();
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (!(getActivity() instanceof JTabActivity))
        {
            throw new IllegalArgumentException("Must be combined with JTabActivity!!!");
        }
    }

    public final <T extends JTabActivity> T getJTabActivity()
    {
        return (T) getActivity();
    }

    public final boolean hasChildFragment()
    {
        return historyList.size() > 0;
    }

    public final void removeHistory(JFragment fragment)
    {
        historyList.remove(fragment);
    }

    public final void removeHistory(int idnex)
    {
        historyList.remove(idnex);
    }

    public final void commitJChildFragment(JFragment fragment)
    {
        commitJChildFragment(fragment, true);
    }

    public final boolean containChildFragment(JFragment fragment)
    {
        return historyList.contains(fragment);
    }

    public final void commitJChildFragment(JFragment fragment, boolean isSelfAddToHistory)
    {
        JTabActivity tab = getJTabActivity();
        if (tab != null)
        {
            JLog.d(JLog.TAG + " commitChildFragment historyList.contains fragment  " + historyList.contains(fragment));
            if (!tab.isFinishing() &&
                    tab.getSupportFragmentManager() != null)
            {
                JParentFragment parent = tab.getHistoryFragment(getClass());
                if (parent != null)
                {
                    if (parent.empyHistory())
                    {
                        parent.addHistory(this);
                    }

                    JFragment lastOne = getLastFragment();
                    if (!(parent.getCurrentFragment() instanceof JParentFragment) ||
                            (lastOne != null && !lastOne.TAG.equals(fragment.TAG)))
                    {
                        parent.addHistory(fragment);
                    }
                    if (!isSelfAddToHistory)
                    {
                        removeHistory(getCurrentFragment());
                    }
                    parent.setCurrentFragment(fragment);
                    tab.commitFragment(getJTabActivity().getFragmentId(), fragment, FragmentAnimationType.LeftIn);
                }
            }
        }
    }

    public final JFragment getCurrentFragment()
    {
        return (current != null) ? current : this;
    }

    public final void setCurrentFragment(JFragment fragment)
    {
        current = fragment;
    }

    public final void setCurrentFragment()
    {
        current = this;
    }

    public final void clearHistory()
    {
        historyList.clear();
    }

    public final boolean empyHistory()
    {
        return historyList.isEmpty();
    }

    public final JFragment getLastFragment()
    {
        if (JUtil.notEmpty(historyList))
        {
            return historyList.get(historyList.size() - 1);
        }
        else
        {
            return this;
        }
    }

    public void backToPreviousFragment()
    {
        backToPreviousFragment(null);
    }

    public <T extends Fragment> void backToPreviousFragment(Class<T> fragmentClass)
    {
        JTabActivity tab = getJTabActivity();
        JLog.d(TAG, "backToPreviousFragment JUtil.notEmpty(historyList) " + JUtil.notEmpty(historyList));

        if (JUtil.notEmpty(historyList))
        {
            int lastOne = historyList.size() - 2;
            JLog.d(TAG, "backToPreviousFragment lastOne " + lastOne);
            if (lastOne >= 0 && lastOne < historyList.size())
            {
                JFragment fragment = historyList.get(lastOne);
                JLog.d(TAG, "backToPreviousFragment (fragment != null) " + (fragment != null));
                if (fragment != null)
                {
                    fragment = getJActivity().getHistoryFragment(fragment.getClass());
                    if (fragment.TAG.equals(TAG))
                    {
                        commitSelfFragment();
                    }
                    else
                    {
                        current = fragment;
                        tab.commitFragment(getJTabActivity().getFragmentId(), fragment, FragmentAnimationType.RightIn);
                        historyList.remove(historyList.size() - 1);
                    }
                }
                else
                {
                    commitSelfFragment();
                }
            }
            else
            {
                if (historyList.size() == 1)
                {
                    clearHistory();
                    current = this;
                    tab.finish();
                }
                else
                {
                    commitSelfFragment();
                }
            }
        }
        else
        {
            if (fragmentClass != null)
            {
                JFragment jFragment = (JFragment) getJActivity().getHistoryFragment(fragmentClass);
                getJTabActivity().commitFragmentTransaction(jFragment, FragmentAnimationType.RightIn);
            }
        }
    }

    protected void commitSelfFragment()
    {
        JTabActivity tab = getJTabActivity();
        if (tab != null)
        {
            clearHistory();
            setCurrentFragment();
            JFragment fragment = getJActivity().getHistoryFragment(getClass());
            if (fragment != null)
            {
                tab.commitFragment(getJTabActivity().getFragmentId(), fragment, FragmentAnimationType.RightIn);
            }
            else
            {
                tab.commitFragment(getJTabActivity().getFragmentId(), this, FragmentAnimationType.RightIn);
            }
        }
    }
}
