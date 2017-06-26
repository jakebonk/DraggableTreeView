package com.allyants.draggabletreeview;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import java.util.ArrayList;

/**
 * Created by jbonk on 6/16/2017.
 */

public class DraggableTreeView extends FrameLayout{

    ScrollView mRootLayout;
    LinearLayout mParentLayout;
    private TreeViewAdapter adapter;

    private BitmapDrawable mHoverCell;
    private Rect mHoverCellCurrentBounds;
    private Rect mHoverCellOriginalBounds;

    private TreeNode mobileNode,lastNode;
    private int sideMargin = 20;
    private enum Drop{above_sibling,below_sibling,child,cancel}
    Drop drop_item;

    private long mPlaceholderCheck = System.currentTimeMillis();
    private long mPlaceholderDelay = new Long(200);
    private int mDownY = -1;
    private int mDownX = -1;
    private int mScrollDownY = -1;
    private int mLastEventX = -1;
    private int mLastEventY = -1;
    private ArrayList<TreeNode> nodeOrder = new ArrayList<>();
    public int maxLevels = -1;
    public boolean makeSiblingAtMaxLevel = true;

    private View mobileView;
    private boolean mCellIsMobile = false;

    public DraggableTreeView(Context context) {
        super(context);
    }

    public DraggableTreeView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DraggableTreeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private DragItemCallback mDragItemCallback = new DragItemCallback(){

        @Override
        public void onStartDrag(View item, TreeNode node){

        }

        @Override
        public void onChangedPosition(View item, TreeNode child, TreeNode parent, int position) {

        }

        @Override
        public void onEndDrag(View item, TreeNode child, TreeNode parent, int position) {

        }

    };

    public void setOnDragItemListener(DragItemCallback dragItemCallback){
        mDragItemCallback = dragItemCallback;
    }

    public interface DragItemCallback{
        void onStartDrag(View item,TreeNode node);
        void onChangedPosition(View item,TreeNode child,TreeNode parent,int position);
        void onEndDrag(View item,TreeNode child,TreeNode parent, int position);
    }

    public void setAdapter(TreeViewAdapter adapter){
        this.adapter = adapter;
        this.adapter.setDraggableTreeView(this);
        adapter.setTreeViews();
        notifyDataSetChanged();
    }

    public void notifyDataSetChanged(){
        if(adapter != null) {
            mParentLayout.removeAllViews();
            nodeOrder = new ArrayList<>();
            inflateViews(adapter.root);
        }
    }

    private void inflateViews(TreeNode node){
        if(!node.isRoot()) {
            createTreeItem(node.getView(),node);
        }else{
            ((ViewGroup) node.getView()).removeAllViews();
            mParentLayout.addView((LinearLayout)node.getView());
        }
        for (int i = 0; i < node.getChildren().size(); i++) {
            inflateViews(node.getChildren().get(i));
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mRootLayout = new ScrollView(getContext());
        mParentLayout = new LinearLayout(getContext());
        mParentLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        mRootLayout.addView(mParentLayout);
        addView(mRootLayout);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        boolean colValue = handleItemDragEvent(event);
        return colValue || super.onInterceptTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean colValue = handleItemDragEvent(event);
        return colValue || super.onTouchEvent(event);
    }

    public boolean handleItemDragEvent(MotionEvent event){
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                mDownX = (int)event.getRawX();
                mDownY = (int)event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                if(mDownY == -1){
                    mDownY = (int)event.getRawY();

                }
                if(mDownX == -1){
                    mDownX = (int)event.getRawX();
                }
                if(mScrollDownY == -1){
                    mScrollDownY = mRootLayout.getScrollY();
                }

                mLastEventX = (int) event.getRawX();
                mLastEventY = (int) event.getRawY();
                int deltaX = mLastEventX - mDownX;
                int deltaY = mLastEventY - mDownY;

                if (mCellIsMobile) {
                    int location[] = new int[2];
                    mobileView.getLocationOnScreen(location);
                    int root_location[] = new int[2];
                    mRootLayout.getLocationOnScreen(root_location);
                    int offsetX = deltaX-root_location[0];
                    int offsetY = location[1]+deltaY-root_location[1]+mRootLayout.getScrollY()-mScrollDownY;
                    mHoverCellCurrentBounds.offsetTo(offsetX,
                            offsetY);
                    mHoverCell.setBounds(rotatedBounds(mHoverCellCurrentBounds,0.0523599f));
                    invalidate();
                    handleItemDrag();
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
                touchEventsCancelled();
                break;
            case MotionEvent.ACTION_CANCEL:
                touchEventsCancelled();
                break;
            case MotionEvent.ACTION_POINTER_UP:
                break;
            default:
                break;
        }
        return false;
    }

    private void handleItemDrag(){
        LinearLayout layout = ((LinearLayout)adapter.root.getView());
        for(int i =0; i< layout.getChildCount(); i++)
        {
            View view = layout.getChildAt(i);

            int[] location = new int[2];
            view.getLocationInWindow(location);
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) view.getLayoutParams();
            Rect viewRect = new Rect(location[0], location[1], location[0]+view.getWidth(), location[1]+view.getHeight());
            Rect outRect = new Rect(0, location[1]-lp.topMargin, Resources.getSystem().getDisplayMetrics().widthPixels, location[1]+view.getHeight()+lp.bottomMargin);

            if(outRect.contains(mLastEventX, mLastEventY))
            {
                //set last position
                int[] root_location = new int[2];
                mRootLayout.getLocationOnScreen(root_location);
                if(root_location[1] > mLastEventY - dpToPx(25)){
                    mRootLayout.smoothScrollBy(0,-10);
                }
                if(root_location[1]+mRootLayout.getHeight() < mLastEventY + dpToPx(25)){
                    mRootLayout.smoothScrollBy(0,10);
                }
                if(mLastEventY < viewRect.top+view.getHeight()/2 && mLastEventX <= mDownX+dpToPx(40)){
                    //above so make sibling
                    if(mPlaceholderCheck+mPlaceholderDelay < System.currentTimeMillis()) {
                        boolean has_changed = false;
                        if(lastNode != null && (drop_item != Drop.above_sibling || lastNode != nodeOrder.get(i))){
                            //Item has changed
                            has_changed = true;
                        }
                        drop_item = Drop.above_sibling;
                        lastNode = nodeOrder.get(i);
                        if(adapter.placeholder.getParent() != null){
                            ((ViewGroup) adapter.placeholder.getParent()).removeView(adapter.placeholder);
                        }
                        if(adapter.bad_placeholder.getParent() != null){
                            ((ViewGroup) adapter.bad_placeholder.getParent()).removeView(adapter.bad_placeholder);
                        }
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        layoutParams.setMargins(0, 0, 0, 0 );
                        adapter.placeholder.setLayoutParams(layoutParams);
                        adapter.bad_placeholder.setLayoutParams(layoutParams);
                        int pos = ((ViewGroup) lastNode.getView().getParent()).indexOfChild(lastNode.getView());
                        int level = mobileNode.getChildLevel()+lastNode.getLevel();
                        if(maxLevels != -1 && maxLevels < level){
                            ((ViewGroup) lastNode.getView().getParent()).addView(adapter.bad_placeholder, pos);
                            drop_item = Drop.cancel;
                        }else {
                            if(has_changed) {
                                if(i <= nodeOrder.indexOf(mobileNode) || lastNode.getLevel() > mobileNode.getLevel()){
                                    mDragItemCallback.onChangedPosition(mobileNode.getView(), mobileNode, lastNode.getParent(), lastNode.getPosition()+1);
                                }else {
                                    mDragItemCallback.onChangedPosition(mobileNode.getView(), mobileNode, lastNode.getParent(), lastNode.getPosition());
                                }
                            }
                            ((ViewGroup) lastNode.getView().getParent()).addView(adapter.placeholder, pos);
                        }
                        mPlaceholderCheck = System.currentTimeMillis();
                    }
            }else if(mLastEventX >  mDownX+dpToPx(40)) {
                //make child
                if(mPlaceholderCheck+mPlaceholderDelay < System.currentTimeMillis()) {
                    TreeNode temp_node = null;
                    boolean has_changed = false;
                    if(lastNode != null && (drop_item != Drop.child || lastNode != nodeOrder.get(i))){
                        //Item has changed
                        has_changed = true;
                        temp_node = lastNode;
                    }
                    drop_item = Drop.child;
                    lastNode = nodeOrder.get(i);
                    if(adapter.placeholder.getParent() != null){
                        ((ViewGroup) adapter.placeholder.getParent()).removeView(adapter.placeholder);
                    }
                    if(adapter.bad_placeholder.getParent() != null){
                        ((ViewGroup) adapter.bad_placeholder.getParent()).removeView(adapter.bad_placeholder);
                    }
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    layoutParams.setMargins(dpToPx(sideMargin), 0, 0, 0 );
                    adapter.placeholder.setLayoutParams(layoutParams);
                    adapter.bad_placeholder.setLayoutParams(layoutParams);
                    int level = mobileNode.getChildLevel()+lastNode.getLevel()+1;
                    if(maxLevels != -1 && maxLevels < level){
                        ((ViewGroup) lastNode.getView()).addView(adapter.bad_placeholder);
                        drop_item = Drop.cancel;
                    }else {
                        if(has_changed && temp_node != null) {
                            mDragItemCallback.onChangedPosition(mobileNode.getView(), mobileNode, temp_node,0);
                        }
                        ((ViewGroup) lastNode.getView()).addView(adapter.placeholder);
                    }
                    mPlaceholderCheck = System.currentTimeMillis();
                }
            }else if(mLastEventY > viewRect.bottom){
                    //below so make sibling
                    if(mPlaceholderCheck+mPlaceholderDelay < System.currentTimeMillis()) {
                        boolean has_changed = false;
                        if(lastNode != null && (drop_item != Drop.below_sibling || lastNode != nodeOrder.get(i))){
                            //Item has changed
                            has_changed = true;
                        }
                        drop_item = Drop.below_sibling;
                        lastNode = nodeOrder.get(i);
                        if(adapter.placeholder.getParent() != null){
                            ((ViewGroup) adapter.placeholder.getParent()).removeView(adapter.placeholder);
                        }
                        if(adapter.bad_placeholder.getParent() != null){
                            ((ViewGroup) adapter.bad_placeholder.getParent()).removeView(adapter.bad_placeholder);
                        }
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        layoutParams.setMargins(0, 0, 0, 0 );
                        adapter.placeholder.setLayoutParams(layoutParams);
                        adapter.bad_placeholder.setLayoutParams(layoutParams);
                        int pos = ((ViewGroup) lastNode.getView().getParent()).indexOfChild(lastNode.getView());
                        int level = mobileNode.getChildLevel()+lastNode.getLevel();
                        if(maxLevels != -1 && maxLevels < level){
                            ((ViewGroup) lastNode.getView().getParent()).addView(adapter.bad_placeholder, pos + 1);
                            drop_item = Drop.cancel;
                        }else {
                            if(has_changed) {
                                if(i <= nodeOrder.indexOf(mobileNode) || lastNode.getLevel() > mobileNode.getLevel()){
                                    mDragItemCallback.onChangedPosition(mobileNode.getView(), mobileNode, lastNode.getParent(), lastNode.getPosition() + 2);
                                }else{
                                    mDragItemCallback.onChangedPosition(mobileNode.getView(), mobileNode, lastNode.getParent(), lastNode.getPosition() + 1);
                                }
                            }
                            ((ViewGroup) lastNode.getView().getParent()).addView(adapter.placeholder, pos + 1);
                        }
                        mPlaceholderCheck = System.currentTimeMillis();
                    }
                }else{
                    if(mPlaceholderCheck+mPlaceholderDelay < System.currentTimeMillis()) {
                        boolean has_changed = false;
                        if(lastNode != null && (drop_item != Drop.below_sibling || lastNode != nodeOrder.get(i))){
                            //Item has changed
                            has_changed = true;
                        }
                        drop_item = Drop.below_sibling;
                        lastNode = nodeOrder.get(i);
                        if(adapter.placeholder.getParent() != null){
                            ((ViewGroup) adapter.placeholder.getParent()).removeView(adapter.placeholder);
                        }
                        if(adapter.bad_placeholder.getParent() != null){
                            ((ViewGroup) adapter.bad_placeholder.getParent()).removeView(adapter.bad_placeholder);
                        }
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        layoutParams.setMargins(0, 0, 0, 0 );
                        adapter.placeholder.setLayoutParams(layoutParams);
                        adapter.bad_placeholder.setLayoutParams(layoutParams);
                        int pos = ((ViewGroup) lastNode.getView().getParent()).indexOfChild(lastNode.getView());

                        int level = mobileNode.getChildLevel()+lastNode.getLevel();
                        if(maxLevels != -1 && maxLevels < level){
                            ((ViewGroup) lastNode.getView().getParent()).addView(adapter.bad_placeholder, pos + 1);
                            drop_item = Drop.cancel;
                        }else {
                            if(has_changed) {
                                if(i <= nodeOrder.indexOf(mobileNode) || lastNode.getLevel() > mobileNode.getLevel()){
                                    mDragItemCallback.onChangedPosition(mobileNode.getView(), mobileNode, lastNode.getParent(), lastNode.getPosition() + 2);
                                }else{
                                    mDragItemCallback.onChangedPosition(mobileNode.getView(), mobileNode, lastNode.getParent(), lastNode.getPosition() + 1);
                                }
                            }
                            ((ViewGroup) lastNode.getView().getParent()).addView(adapter.placeholder, pos + 1);
                        }
                        mPlaceholderCheck = System.currentTimeMillis();
                    }
                }

            }
        }
    }

    private void touchEventsCancelled() {
        if(mCellIsMobile && mobileNode != null){
            mobileView.setVisibility(VISIBLE);
            mHoverCell = null;
            if(adapter != null) {
                if(adapter.placeholder.getParent() != null) {
                    ((ViewGroup) adapter.placeholder.getParent()).removeView(adapter.placeholder);
                }
                if(adapter.bad_placeholder.getParent() != null) {
                    ((ViewGroup) adapter.bad_placeholder.getParent()).removeView(adapter.bad_placeholder);
                }
                //Make sure it didn't drop on itself
                if(lastNode != mobileNode || drop_item != Drop.cancel) {
                    if (drop_item == Drop.above_sibling) {
                        int pos = lastNode.getPosition();
                        mobileNode.setParent(lastNode.getParent(), pos - 1);
                        mDragItemCallback.onEndDrag(mobileNode.getView(),mobileNode,lastNode,mobileNode.getPosition()+1);
                    } else if (drop_item == Drop.below_sibling) {
                        int pos = lastNode.getPosition();
                        //if it came from below we need to add
                        mobileNode.setParent(lastNode.getParent(), pos);
                        mDragItemCallback.onEndDrag(mobileNode.getView(),mobileNode,lastNode,mobileNode.getPosition()+1);
                    } else if (drop_item == Drop.child) {
                        mobileNode.setParent(lastNode,0);
                        mDragItemCallback.onEndDrag(mobileNode.getView(),mobileNode,lastNode,mobileNode.getPosition()+1);
                    }
                }

                notifyDataSetChanged();
            }
            invalidate();

        }

        mDownX = -1;
        mDownY = -1;
        mScrollDownY = -1;
        mCellIsMobile = false;
    }

    public void createTreeItem(View view, final TreeNode node){
        if(view != null) {
            nodeOrder.add(node);
            final LinearLayout mItem = new LinearLayout(getContext());
            mItem.setOrientation(LinearLayout.VERTICAL);
            if(view.getParent() != null) {
                ViewGroup parent = (ViewGroup) view.getParent();
                parent.removeView(view);
            }
            view.setOnLongClickListener(new OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mobileNode = node;
                    addToView(mItem,node);
                    mobileView = mItem;
                    mDragItemCallback.onStartDrag(mobileNode.getView(),mobileNode);
                    mItem.post(new Runnable() {
                        @Override
                        public void run() {
                            mCellIsMobile = true;
                            mHoverCell = getAndAddHoverView(mobileView,1f);
                            mobileView.setVisibility(INVISIBLE);
                        }
                    });
                    return false;
                }
            });
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(dpToPx(sideMargin*node.getLevel() ), 0, 0, 0 );
            mItem.setLayoutParams(layoutParams);
            mItem.addView(view);
            ((LinearLayout)adapter.root.getView()).addView(mItem);
        }
    }

    private void addToView(LinearLayout linearLayout,TreeNode node){
        for(int i = 0;i < node.getChildren().size();i++) {
            View child = node.getChildren().get(i).getView();
            if(child.getParent().getParent() != null) {
                ((ViewGroup)child.getParent().getParent()).removeView((View) child.getParent());
            }
            linearLayout.addView(((View)child.getParent()));
            addToView(linearLayout,node.getChildren().get(i));
        }
    }

    public int dpToPx(int dp){
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if(mHoverCell != null){
            mHoverCell.draw(canvas);
        }
    }

    private BitmapDrawable getAndAddHoverView(View v, float scale){
        int w = v.getWidth();
        int h = v.getHeight();
        int top = v.getTop();
        int left = v.getLeft();

        Bitmap b = getBitmapWithBorder(v,scale);
        BitmapDrawable drawable = new BitmapDrawable(getResources(),b);
        mHoverCellOriginalBounds = new Rect(left,top,left+w,top+h);
        mHoverCellCurrentBounds = new Rect(mHoverCellOriginalBounds);
        drawable.setBounds(mHoverCellCurrentBounds);
        return drawable;
    }

    private Bitmap getBitmapWithBorder(View v, float scale) {
        Bitmap bitmap = getBitmapFromView(v,0);
        Bitmap b = getBitmapFromView(v,1);
        Canvas can = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setAlpha(150);
        can.scale(scale,scale,mDownX,mDownY);
        can.rotate(3);
        can.drawBitmap(b,0,0,paint);
        return bitmap;
    }

    private Bitmap getBitmapFromView(View v, float scale){
        double radians = 0.0523599f;
        double s = Math.abs(Math.sin(radians));
        double c = Math.abs(Math.cos(radians));
        int width = (int)(v.getHeight()*s + v.getWidth()*c);
        int height = (int)(v.getWidth()*s + v.getHeight()*c);
        Bitmap bitmap = Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.scale(scale,scale);
        v.draw(canvas);
        return bitmap;
    }

    private Rect rotatedBounds(Rect tmp,double radians){
        double s = Math.abs(Math.sin(radians));
        double c = Math.abs(Math.cos(radians));
        int width = (int)(tmp.height()*s + tmp.width()*c);
        int height = (int)(tmp.width()*s + tmp.height()*c);

        return new Rect(tmp.left,tmp.top,tmp.left+width,tmp.top+height);
    }

}