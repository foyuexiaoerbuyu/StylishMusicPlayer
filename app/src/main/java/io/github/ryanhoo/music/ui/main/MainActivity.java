package io.github.ryanhoo.music.ui.main;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.RadioButton;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import io.github.ryanhoo.music.R;
import io.github.ryanhoo.music.data.model.MenuPopwindowBean;
import io.github.ryanhoo.music.ui.base.BaseActivity;
import io.github.ryanhoo.music.ui.base.BaseFragment;
import io.github.ryanhoo.music.ui.local.LocalFilesFragment;
import io.github.ryanhoo.music.ui.music.MusicPlayerFragment;
import io.github.ryanhoo.music.ui.playlist.PlayListFragment;
import io.github.ryanhoo.music.ui.settings.SettingsFragment;
import io.github.ryanhoo.music.ui.widget.MenuPopwindow;
import io.github.ryanhoo.music.utils.XLog;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {

    static final int DEFAULT_PAGE_INDEX = 2;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    @BindViews({R.id.radio_button_play_list, R.id.radio_button_music, R.id.radio_button_local_files})
    List<RadioButton> radioButtons;

    String[] mTitles;

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        // Main Controls' Titles
        mTitles = getResources().getStringArray(R.array.mp_main_titles);

        // Fragments
        BaseFragment[] fragments = new BaseFragment[mTitles.length];
        fragments[0] = new PlayListFragment();
        fragments[1] = new MusicPlayerFragment();
        fragments[2] = new LocalFilesFragment();
//        fragments[3] = new SettingsFragment();

        // Inflate ViewPager
        MainPagerAdapter adapter = new MainPagerAdapter(getSupportFragmentManager(), mTitles, fragments);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(adapter.getCount() - 1);
        viewPager.setPageMargin(getResources().getDimensionPixelSize(R.dimen.mp_margin_large));
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // Empty
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                // Empty
            }

            @Override
            public void onPageSelected(int position) {
                radioButtons.get(position).setChecked(true);
            }
        });

        radioButtons.get(DEFAULT_PAGE_INDEX).setChecked(true);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @OnCheckedChanged({R.id.radio_button_play_list, R.id.radio_button_music, R.id.radio_button_local_files})
    public void onRadioButtonChecked(RadioButton button, boolean isChecked) {
        if (isChecked) {
            onItemChecked(radioButtons.indexOf(button));
        }
    }

    private void onItemChecked(int position) {
        viewPager.setCurrentItem(position);
        toolbar.setTitle(mTitles[position]);
    }

   /* @OnClick(R.id.radio_button_settings)
    public void onSettingBtnClack(View view) {
        int[] icons = {R.mipmap.icon_menu_item_edit, R.mipmap.icon_menu_item_delete};
        String[] texts = {"编辑", "删除"};
        List<MenuPopwindowBean> list = new ArrayList<>();
        MenuPopwindowBean bean = null;
        for (int i = 0; i < icons.length; i++) {
            bean = new MenuPopwindowBean();
            bean.setIcon(icons[i]);
            bean.setText(texts[i]);
            list.add(bean);
        }
        MenuPopwindow pw = new MenuPopwindow(MainActivity.this, list);
        pw.setOnItemClick(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                XLog.showArgsInfo(i, l);
                viewPager.getCurrentItem()
            }
        });
        //点击右上角的那个button
        pw.showPopupWindow(findViewById(R.id.radio_button_settings));
    }*/
}
