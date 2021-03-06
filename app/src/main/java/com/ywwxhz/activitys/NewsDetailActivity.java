package com.ywwxhz.activitys;

import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ywwxhz.cnbetareader.R;
import com.ywwxhz.entitys.NewsItem;
import com.ywwxhz.fragments.NewsCommentFragment;
import com.ywwxhz.fragments.NewsDetailFragment;
import com.ywwxhz.lib.kits.PrefKit;
import com.ywwxhz.processers.BaseProcesserImpl;
import com.ywwxhz.widget.FixViewPager;

import java.util.ArrayList;
import java.util.List;

/**
 * cnBetaReader
 * <p/>
 * Created by 远望の无限(ywwxhz) on 2014/11/1 17:52.
 */
public class NewsDetailActivity extends ExtendBaseActivity implements NewsDetailFragment.NewsDetailCallBack {

    private List<Fragment> fragments = new ArrayList<>(2);
    private FixViewPager pager;
    private FragmentAdapter adapter;
    private CharSequence title;
    private ViewGroup contentView;
    private int orientation;
    private int requiredOrientation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        oldSystemUIVisuablity = getRootView().getSystemUiVisibility();
        if (bundle != null && bundle.containsKey(NewsDetailFragment.NEWS_SID_KEY) && bundle.containsKey(NewsDetailFragment.NEWS_TITLE_KEY)) {
            title = bundle.getString(NewsDetailFragment.NEWS_TITLE_KEY);
            if(title!=null && title.length()>0){
                setTitle(title);
            }
            contentView = (ViewGroup) findViewById(R.id.content);
            fragments.add(NewsDetailFragment.getInstance(bundle.getInt(NewsDetailFragment.NEWS_SID_KEY), title.toString()));
                setContentView(R.layout.pager_layout);
                pager = (FixViewPager) findViewById(R.id.pager);
                adapter = new FragmentAdapter(getSupportFragmentManager());
                pager.setAdapter(adapter);
                pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                    }

                    @Override
                    public void onPageSelected(int position) {
                        updateTitle();
                        if (position == 0) {
                            setSwipeBackEnable(PrefKit.getBoolean(NewsDetailActivity.this, R.string.pref_swipeback_key, true));
                        } else {
                            setSwipeBackEnable(false);
                        }
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {

                    }
                });
        } else {
            Toast.makeText(this, "缺少必要参数", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && pager.getCurrentItem() != 0) {
            pager.setCurrentItem(0);
            return true;
        }
        return ((NewsDetailFragment) fragments.get(0)).onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.finish();
    }

    @Override
    public void onNewsLoadFinish(NewsItem item, boolean success) {
        if (success && fragments.size() == 1) {
            fragments.add(NewsCommentFragment.getInstance(item.getSid(), item.getSN())
                    .setMenuCallBack(new BaseProcesserImpl.onOptionMenuSelect() {
                        @Override
                        public boolean onMenuSelect(MenuItem item) {
                            if (item.getItemId() == android.R.id.home) {
                                pager.setCurrentItem(0, true);
                                return true;
                            }
                            return false;
                        }
                    }));
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void CommentAction(int sid, String sn, String title) {
        pager.setCurrentItem(1, true);
    }

    private int oldSystemUIVisuablity;

    @Override
    public void onVideoFullScreen(boolean isFullScreen) {
        if (!isFullScreen) {
            setRequestedOrientation(orientation);
            setRequestedOrientation(requiredOrientation);
            getSupportActionBar().show();
            setSwipeBackEnable(PrefKit.getBoolean(this, R.string.pref_swipeback_key, true));
            helper.setEnable(true);
            if(Build.VERSION_CODES.JELLY_BEAN<Build.VERSION.SDK_INT) {
                getRootView().setSystemUiVisibility(oldSystemUIVisuablity);
            }
        } else {
            requiredOrientation = getRequestedOrientation();
            orientation = getResources().getConfiguration().orientation;
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            getSupportActionBar().hide();
            setSwipeBackEnable(false);
            helper.setEnable(false);
            if(Build.VERSION_CODES.JELLY_BEAN<Build.VERSION.SDK_INT) {
                getRootView().setSystemUiVisibility(oldSystemUIVisuablity
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
            }
        }
    }

    @Override
    public void onShowHtmlVideoView(View html5VideoView) {
        contentView.addView(html5VideoView);
        html5VideoView.bringToFront();
        pager.setVisibility(View.GONE);
    }

    @Override
    public void onHideHtmlVideoView(View html5VideoView) {
        contentView.removeView(html5VideoView);
        pager.setVisibility(View.VISIBLE);
    }


    class FragmentAdapter extends FragmentPagerAdapter {

        public FragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        if(pager!=null) {
            switch (pager.getCurrentItem()) {
                case 0:
                    super.setTitle("详情：" + title);
                    break;
                case 1:
                    super.setTitle("评论：" + title);
                    break;
            }
        }else{
            super.setTitle("详情：" + title);
        }
        this.title = title;
    }

    void updateTitle(){
        setTitle(title);
    }
}
