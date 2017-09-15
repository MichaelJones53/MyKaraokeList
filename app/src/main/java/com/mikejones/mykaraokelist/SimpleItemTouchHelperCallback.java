package com.mikejones.mykaraokelist;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;


public class SimpleItemTouchHelperCallback extends ItemTouchHelper.Callback {

    private final ItemTouchHelperAdapter mAdapter;
    public static final String TAG = "SimpleItemTouchHelper";
    private float selectedAlpha = .9f;
    private float idleAlpha = 1;
    private Paint p = new Paint();
    private Context context;
    private FragmentManager fm;


    public SimpleItemTouchHelperCallback(ItemTouchHelperAdapter adapter, Context c) {
        mAdapter = adapter;
        context = c;


    }

    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState){

        Log.d(TAG, "onSelectedChanged called actionState: "+ actionState);

        if(actionState == ItemTouchHelper.ACTION_STATE_DRAG){
            Log.d(TAG, "onSelectedChanged drag: "+ actionState);

            viewHolder.itemView.setAlpha(selectedAlpha);

        }
    }

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder){
        super.clearView(recyclerView, viewHolder);
        viewHolder.itemView.setAlpha(idleAlpha);
    }



    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                          RecyclerView.ViewHolder target) {
        mAdapter.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        int position = viewHolder.getAdapterPosition();

        if (direction == ItemTouchHelper.START){

            Log.d(TAG, "onSwiped LEFT called");
            mAdapter.onItemDismiss(position);



        } else {
            Log.d(TAG, "onSwiped RIGHT called");
            //TODO: edit logic
            fm = ((ListActivity) context).getSupportFragmentManager();
            UpdateSongDialog updateSong = UpdateSongDialog.newInstance(SongRecyclerViewAdapter.getListKey(position));
            updateSong.show(fm, "update_song_dialog");

        }



    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {



        Log.d(TAG, "onChildDraw Called");
        Bitmap icon;
        if(actionState == ItemTouchHelper.ACTION_STATE_SWIPE){

            View itemView = viewHolder.itemView;
            float height = (float) itemView.getBottom() - (float) itemView.getTop();
            float width = height / 3;

            itemView.setTranslationX(dX);
            if(dX > 0){
                p.setColor(Color.parseColor("#388E3C"));

                RectF background = new RectF((float) itemView.getLeft(), (float) itemView.getTop()+15, dX,(float) itemView.getBottom()-15);
                c.drawRect(background,p);

                icon = BitmapFactory.decodeResource(context.getResources(), android.R.drawable.ic_menu_edit);
                RectF icon_dest = new RectF((float) itemView.getLeft() + width ,(float) itemView.getTop() + width,(float) itemView.getLeft()+ (2*width),(float)itemView.getBottom() - width);
                c.drawBitmap(icon,null,icon_dest,p);
            } else {
                p.setColor(Color.parseColor("#D32F2F"));
                RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop()+15,(float) itemView.getRight(), (float) itemView.getBottom()-15);
                c.drawRect(background,p);
                icon = BitmapFactory.decodeResource(context.getResources(), android.R.drawable.ic_menu_delete);
                RectF icon_dest = new RectF((float) itemView.getRight() - 2*width ,(float) itemView.getTop() + width,(float) itemView.getRight() - width,(float)itemView.getBottom() - width);
                c.drawBitmap(icon,null,icon_dest,p);
            }
        }else{
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }


    }

}
