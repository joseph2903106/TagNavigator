package com.josephwang.framework;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.josephwang.util.JLog;
import com.josephwang.util.JUtil;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by josephwang on 2017/3/28.
 */

public abstract class JChildFragment<Parent extends JParentFragment> extends JFragment
{
    public final <T extends JTabActivity> T getJTabActivity()
    {
        return (T) getActivity();
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (!(getActivity() instanceof JTabActivity))
        {
            throw new IllegalArgumentException("Must be combined with JTabActivity!!!");
        }
//        checkParent();
    }

    public Parent getJParentFragment()
    {
        JTabActivity tab = getJTabActivity();
        Type sooper = getClass().getGenericSuperclass();
        if (!(sooper instanceof ParameterizedType))
        {
            throw new IllegalArgumentException("ChildFragment must have been assigned to JParentFragment with Generic name !!!");
        }
        else
        {
            if (JUtil.notEmpty(((ParameterizedType) sooper).getActualTypeArguments()))
            {
                Type parentType = ((ParameterizedType) sooper).getActualTypeArguments()[0];
                String name = parentType.toString();
                String className = name.substring(name.lastIndexOf(".") + 1);
                return tab.getHistoryFragment(className);
            }
            else
            {
                throw new IllegalArgumentException("ChildFragment must have been assigned to JParentFragment with Generic name !!!");
            }
        }
    }

    public void checkParent()
    {
        Type sooper = getClass().getGenericSuperclass();
        if (!(sooper instanceof ParameterizedType))
        {
            throw new IllegalArgumentException("ChildFragment must have been assigned to JParentFragment with Genric name !!!");
        }
    }

    public final boolean hasChildFragment()
    {
        return (getJParentFragment() != null &&
                getJParentFragment().hasChildFragment());
    }

    public final void commitJChildFragment(JFragment fragment)
    {
        commitJChildFragment(fragment, true);
    }

    public final void commitJChildFragment(JFragment fragment, boolean isSelfAddToHistory)
    {
        if (getJTabActivity() != null)
        {
            JParentFragment parent = getJParentFragment();
            if (parent != null)
            {
                parent.commitJChildFragment(fragment, isSelfAddToHistory);
            }
        }
    }

    public final JFragment getCurrentFragment()
    {
        if (getJTabActivity() != null)
        {
            JParentFragment parent = getJParentFragment();
            return parent.getCurrentFragment();
        }
        return this;
    }

    public final void removeHistory(JFragment fragment)
    {
        if (getJTabActivity() != null)
        {
            JParentFragment parent = getJParentFragment();
            parent.removeHistory(fragment);
        }
    }

    public final void removeHistory(int index)
    {
        if (getJTabActivity() != null)
        {
            JParentFragment parent = getJParentFragment();
            parent.removeHistory(index);
        }
    }

    public final void clearHistory()
    {
        if (getJTabActivity() != null)
        {
            JParentFragment parent = getJParentFragment();
            parent.clearHistory();
        }
    }

    public int getHistoryListLength()
    {
        if (getJTabActivity() != null)
        {
            JParentFragment parent = getJParentFragment();
            return parent.getHistoryListLength();
        }
        return 0;
    }

    public final void popUpHistory()
    {
        if (getJTabActivity() != null)
        {
            JParentFragment parent = getJParentFragment();
            parent.popUpHistory();
        }
    }

    public final void backToPreviousFragment()
    {
        if (getJTabActivity() != null)
        {
            JParentFragment parent = getJParentFragment();
            JLog.d(TAG, "JParentFragment (parent != null) " + (parent != null));
            if (parent != null)
            {
                parent.backToPreviousFragment();
            }
        }
    }

    public final <T extends Fragment> void backToPreviousFragment(Class<T> fragmentClass)
    {
        if (getJTabActivity() != null)
        {
            JParentFragment parent = getJParentFragment();
            JLog.d(TAG, "JParentFragment (parent != null) " + (parent != null));
            if (parent != null)
            {
                parent.backToPreviousFragment(fragmentClass);
            }
        }
    }
}
