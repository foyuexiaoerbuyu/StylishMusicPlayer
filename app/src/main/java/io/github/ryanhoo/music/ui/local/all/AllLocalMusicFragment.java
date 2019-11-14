package io.github.ryanhoo.music.ui.local.all;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.ryanhoo.music.R;
import io.github.ryanhoo.music.RxBus;
import io.github.ryanhoo.music.data.model.MenuPopwindowBean;
import io.github.ryanhoo.music.data.model.Song;
import io.github.ryanhoo.music.data.source.AppRepository;
import io.github.ryanhoo.music.event.PlayListUpdatedEvent;
import io.github.ryanhoo.music.event.PlaySongEvent;
import io.github.ryanhoo.music.ui.base.BaseFragment;
import io.github.ryanhoo.music.ui.base.adapter.OnItemClickListener;
import io.github.ryanhoo.music.ui.base.adapter.OnItemLongClickListener;
import io.github.ryanhoo.music.ui.common.DefaultDividerDecoration;
import io.github.ryanhoo.music.ui.common.ItemTouchMoveCallback;
import io.github.ryanhoo.music.ui.widget.MenuPopwindow;
import io.github.ryanhoo.music.ui.widget.RecyclerViewFastScroller;
import io.github.ryanhoo.music.utils.XLog;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created with Android Studio.
 * User: ryan.hoo.j@gmail.com
 * Date: 9/1/16
 * Time: 9:58 PM
 * Desc: LocalFilesFragment
 *
 * @author HP
 */
public class AllLocalMusicFragment extends BaseFragment implements LocalMusicContract.View {

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.fast_scroller)
    RecyclerViewFastScroller fastScroller;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.text_view_empty)
    View emptyView;

    LocalMusicAdapter mAdapter;
    LocalMusicContract.Presenter mPresenter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_all_local_music, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        mAdapter = new LocalMusicAdapter(getActivity(), null);

        new ItemTouchHelper(new ItemTouchMoveCallback(mAdapter, mPresenter)).attachToRecyclerView(recyclerView);

        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                XLog.showStepLogInfo();
                Song song = mAdapter.getItem(position);
                RxBus.getInstance().post(new PlaySongEvent(song));
            }
        });

        mAdapter.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                /*长按置顶*/
                mAdapter.itemMove(position, 0);
                Song song = mAdapter.getItem(position);
                song.setAsId(System.currentTimeMillis());
                mPresenter.updateSong(song);
            }
        });

        getActivity().findViewById(R.id.radio_button_settings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                XLog.showStepLogInfo();
//                onSettingBtnClack(view);
            }
        });

        recyclerView.setAdapter(mAdapter);
        recyclerView.addItemDecoration(new DefaultDividerDecoration());

        fastScroller.setRecyclerView(recyclerView);

        new LocalMusicPresenter(AppRepository.getInstance(), this).subscribe();
    }

    private PopupWindow popupWindow;
    // RxBus Events

    @Override
    protected Subscription subscribeEvents() {
        return RxBus.getInstance().toObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Action1<Object>() {
                    @Override
                    public void call(Object o) {
                        if (o instanceof PlayListUpdatedEvent) {
                            mPresenter.loadLocalMusic();
                        }
                    }
                })
                .subscribe();
    }

    // MVP View

    @Override
    public void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void emptyView(boolean visible) {
        emptyView.setVisibility(visible ? View.VISIBLE : View.GONE);
        fastScroller.setVisibility(visible ? View.GONE : View.VISIBLE);
    }

    @Override
    public void handleError(Throwable error) {
        Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLocalMusicLoaded(List<Song> songs) {
        XLog.showArgsInfo(songs);
        mAdapter.setData(songs);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void setPresenter(LocalMusicContract.Presenter presenter) {
        mPresenter = presenter;
    }


    /**
     * 右上角设置按钮
     *
     * @param view btnView
     */
    public void onSettingBtnClack(View view) {
        int[] icons = {R.mipmap.icon_menu_item_edit, R.mipmap.icon_menu_item_delete};
        String[] texts = {"导出音乐", "删除文件"};
        List<MenuPopwindowBean> list = new ArrayList<>();
        MenuPopwindowBean bean = null;
        for (int i = 0; i < icons.length; i++) {
            bean = new MenuPopwindowBean();
            bean.setIcon(icons[i]);
            bean.setText(texts[i]);
            list.add(bean);
        }
        MenuPopwindow pw = new MenuPopwindow(getActivity(), list);
        pw.setOnItemClick(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                XLog.showArgsInfo(position, l);
                if (position == 0) {
                   /* List<Song> songs = mAdapter.getData();
                    String destFilePath = FileUtils.getDefMusicPath();
                    XLog.showArgsInfo(destFilePath);
                    for (Song song : songs) {
                        String songPath = song.getPath().substring(0, song.getPath().lastIndexOf(File.separator));
                        XLog.showArgsInfo("songPath ", songPath);
                        FileUtils.fileMove(songPath, destFilePath);
                    }
//                    mPresenter.loadLocalMusic();*/
                }

            }
        });
        //点击右上角的那个button
        pw.showPopupWindow(getActivity().findViewById(R.id.radio_button_settings));
    }
}
