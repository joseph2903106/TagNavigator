package com.josephwang;

import android.widget.RadioButton;

import com.josephwang.framework.JActivity;
import com.tabnavigator.R;

/**
 * Created by josephwang on 2017/5/8.
 */

public enum FunctionType
{
    MainPage, Wine, Discover, MyList, Account;

    public static FunctionType LastFunctionType = FunctionType.MainPage;

    public static FunctionType getStatus(int index)
    {
        return FunctionType.values()[index];
    }

    public static FunctionType getTypeById(int id)
    {
        FunctionType type = null;
        if (id == R.id.main_page_button ||
                id == R.id.main_page_container)
        {
            type = MainPage;
        }
        else if (id == R.id.wine_button ||
                id == R.id.wine_container)
        {
            type = Wine;
        }
        else if (id == R.id.discover_button ||
                id == R.id.discover_container)
        {
            type = Discover;
        }
        else if (id == R.id.mylist_button ||
                id == R.id.mylist_container)
        {
            type = MyList;
        }
        else if (id == R.id.account_button ||
                id == R.id.account_container)
        {
            type = Account;
        }
        return type;
    }

    public static RadioButton getLastedChecker(JActivity act)
    {
        return getCheckerByType(act, LastFunctionType);
    }

    public static RadioButton getCheckerByType(JActivity act, FunctionType type)
    {
        switch (type)
        {
            case MainPage:
                return act.getView(R.id.main_page_button);
            case Wine:
                return act.getView(R.id.wine_button);
            case Discover:
                return act.getView(R.id.discover_button);
            case MyList:
                return act.getView(R.id.mylist_button);
            case Account:
                return act.getView(R.id.account_button);
            default:
                throw new IllegalArgumentException("You have to use one of Enum");
        }
    }

    public static FunctionType toogleFunction(JActivity act, RadioButton checker)
    {
        RadioButton lastChecker = getCheckerByType(act, LastFunctionType);
        FunctionType currentType = getTypeById(checker.getId());

        if (LastFunctionType != currentType)
        {
            checker.setChecked(true);
            lastChecker.setChecked(false);
        }
        else
        {
            lastChecker.setChecked(true);
        }
        LastFunctionType = currentType;
        return currentType;
    }
}

