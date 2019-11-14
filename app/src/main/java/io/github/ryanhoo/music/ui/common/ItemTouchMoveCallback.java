package io.github.ryanhoo.music.ui.common;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;

import io.github.ryanhoo.music.data.model.Song;
import io.github.ryanhoo.music.ui.constant.Constant;
import io.github.ryanhoo.music.ui.local.all.LocalMusicAdapter;
import io.github.ryanhoo.music.ui.local.all.LocalMusicContract;

/**
 * AllLocalMusicFragment左右侧滑:置顶/置底实现类
 * @author HP
 */
public class ItemTouchMoveCallback extends ItemTouchHelper.Callback {

    LocalMusicAdapter mAdapter;
    LocalMusicContract.Presenter mPresenter;

    public ItemTouchMoveCallback(LocalMusicAdapter adapter, LocalMusicContract.Presenter presenter) {
        mAdapter = adapter;
        mPresenter = presenter;
    }

    /**
     * 拖动标识
     */
    private int dragFlags;
    /**
     * 删除滑动标识
     */
    private int swipeFlags;

    /**
     * 移动标识
     */
    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        dragFlags = 0;
        swipeFlags = 0;
        if (recyclerView.getLayoutManager() instanceof GridLayoutManager
                || recyclerView.getLayoutManager() instanceof StaggeredGridLayoutManager) {
            dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN
                    | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
        } else {
            dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
            if (viewHolder.getAdapterPosition() != 0) {
                swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
            }
        }
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    /**
     * 上下移动
     */
    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
//            int fromPosition = viewHolder.getAdapterPosition();
//            int toPosition = target.getAdapterPosition();
//            if (toPosition != 0) {
//                if (fromPosition < toPosition)
//                    //向下拖动
//                    for (int i = fromPosition; i < toPosition; i++) {
//                        Collections.swap(mAdapter.getData(), i, i + 1);
//                    }
//                else {
//                    //向上拖动
//                    for (int i = fromPosition; i > toPosition; i--) {
//                        Collections.swap(mAdapter.getData(), i, i - 1);
//                    }
//                }
//                recyclerView.getAdapter().notifyItemMoved(fromPosition, toPosition);
//            }
        return true;
    }

    /**
     * @param viewHolder adapterViewHolder
     * @param direction  滑动方向
     */
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        int startPosition = viewHolder.getAdapterPosition();
        Song itemSong = mAdapter.getItem(startPosition);
        if (direction == Constant.SCROLL_LEFT) {
            itemSong.setAsId(System.currentTimeMillis());
            mAdapter.itemMove(startPosition, 0);
        } else {
            int toPosition = mAdapter.getData().size() - 1;
            itemSong.setAsId(mAdapter.getItem(toPosition).getAsId() - 123456789123L);
            mAdapter.itemMove(startPosition, toPosition);
        }
        mPresenter.updateSong(itemSong);
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return false;
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            viewHolder.itemView.setPressed(true);
        }
        super.onSelectedChanged(viewHolder, actionState);
    }

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        viewHolder.itemView.setPressed(false);
    }
}
