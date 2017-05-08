package com.josephwang;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.josephwang.fragment.parent.DiscoveryMainFragment;
import com.josephwang.fragment.parent.LoginFragment;
import com.josephwang.fragment.parent.MainPageFragment;
import com.josephwang.fragment.parent.MyListMainFragment;
import com.josephwang.fragment.parent.WineCategoryFragment;
import com.josephwang.framework.JTabActivity;
import com.tabnavigator.R;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends JTabActivity
{
    @BindView(R.id.main_page_count)
    TextView mainPageCount;
    @BindView(R.id.main_page_button)
    RadioButton mainPageButton;
    @BindView(R.id.main_page_container)
    LinearLayout mainPageContainer;
    @BindView(R.id.wine_count)
    TextView wineCount;
    @BindView(R.id.wine_button)
    RadioButton wineButton;
    @BindView(R.id.wine_container)
    LinearLayout wineContainer;
    @BindView(R.id.discover_count)
    TextView discoverCount;
    @BindView(R.id.discover_button)
    RadioButton discoverButton;
    @BindView(R.id.discover_container)
    LinearLayout discoverContainer;
    @BindView(R.id.mylist_count)
    TextView mylistCount;
    @BindView(R.id.mylist_button)
    RadioButton mylistButton;
    @BindView(R.id.mylist_container)
    LinearLayout mylistContainer;
    @BindView(R.id.account_count)
    TextView accountCount;
    @BindView(R.id.account_button)
    RadioButton accountButton;
    @BindView(R.id.account_container)
    LinearLayout accountContainer;
    @BindView(R.id.tool_bar)
    LinearLayout toolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_tab);
        ButterKnife.bind(this);
        initListener();
    }

    private void initListener()
    {
        mainPageButton.setOnClickListener(clickListener);
        wineButton.setOnClickListener(clickListener);
        discoverButton.setOnClickListener(clickListener);
        mylistButton.setOnClickListener(clickListener);
        accountButton.setOnClickListener(clickListener);
    }

    private final View.OnClickListener clickListener = new View.OnClickListener()
    {
        public void onClick(View v)
        {
            if (v instanceof RadioButton)
            {
                FunctionType type = FunctionType.toogleFunction(MainActivity.this, (RadioButton) v);
                switch (type)
                {
                    case MainPage:
                        commitCurrentFragmentByParent(new MainPageFragment());
                        break;
                    case Wine:
                        commitCurrentFragmentByParent(new WineCategoryFragment());
                        break;
                    case Discover:
                        commitCurrentFragmentByParent(new DiscoveryMainFragment());
                        break;
                    case MyList:
                        commitCurrentFragmentByParent(new MyListMainFragment());
                        break;
                    case Account:
                        commitCurrentFragmentByParent(new LoginFragment());
                        break;
                }
            }
        }
    };

    @Override
    public int getFragmentId()
    {
        return R.id.content;
    }
}
