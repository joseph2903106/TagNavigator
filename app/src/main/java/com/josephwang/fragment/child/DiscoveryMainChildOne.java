package com.josephwang.fragment.child;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.josephwang.fragment.parent.DiscoveryMainFragment;
import com.josephwang.framework.JChildFragment;
import com.tabnavigator.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by josephwang on 2017/5/8.
 */

public class DiscoveryMainChildOne extends JChildFragment<DiscoveryMainFragment>
{
    @BindView(R.id.back)
    RelativeLayout back;

    @BindView(R.id.status)
    TextView status;
    Unbinder unbinder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View content = inflater.inflate(R.layout.parent_main, container, false);
        unbinder = ButterKnife.bind(this, content);
        status.setText("" + TAG);
        back.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                backToPreviousFragment();
            }
        });

        status.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                commitJChildFragment(new DiscoveryMainChildTwo());
            }
        });
        return content;
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        unbinder.unbind();
    }

}
