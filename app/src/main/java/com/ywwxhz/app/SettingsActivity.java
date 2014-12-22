package com.ywwxhz.app;

import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.ywwxhz.cnbetareader.BuildConfig;
import com.ywwxhz.cnbetareader.R;
import com.ywwxhz.lib.kits.UIKit;

/**
 * Created by ywwxhz on 2014/11/3.
 */
public class SettingsActivity extends ExtendBaseActivity {
    @Override
    protected void createView(Bundle savedInstanceState) {
    }

    @Override
    protected void onViewCreated(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction().replace(android.R.id.content, new GeneralPreference()).commit();
        }
    }

    @Override
    protected View getInsertView() {
        return null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public static class GeneralPreference extends PreferenceFragment {
        private ListView mListView;
        private int paddings[];

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);
            findPreference(getString(R.string.pref_version_key)).setSummary(getVersionName());
        }

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            mListView = UIKit.getHideListView(this);
            paddings = new int[]{mListView.getPaddingLeft(), mListView.getPaddingTop(), mListView.getPaddingRight()
                    , mListView.getPaddingBottom()};
            UIKit.fixTranslucentStatusPadding(getActivity(), mListView, UIKit.PaddingMode.SET_ALL, paddings);
        }

        private String getVersionName() {
            return "Ver. " + BuildConfig.VERSION_NAME;
        }

        @Override
        public void onConfigurationChanged(Configuration newConfig) {
            super.onConfigurationChanged(newConfig);
            UIKit.fixTranslucentStatusPadding(getActivity(), mListView, UIKit.PaddingMode.SET_ALL, paddings);
        }
    }
}
