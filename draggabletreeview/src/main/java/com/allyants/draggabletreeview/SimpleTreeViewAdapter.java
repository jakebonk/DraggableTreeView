package com.allyants.draggabletreeview;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by jbonk on 6/16/2017.
 */

public class SimpleTreeViewAdapter extends TreeViewAdapter{

    public SimpleTreeViewAdapter(Context context, TreeNode root) {
        super(context, root);
    }

    @Override
    public View createTreeView(Context context, final TreeNode node, Object data, int level, boolean hasChildren) {
        View view = View.inflate(context,R.layout.tree_view_item,null);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);


        view.setLayoutParams(layoutParams);
        TextView textView = (TextView)view.findViewById(R.id.textView);
        textView.setText(((String)data));

        return view;
    }

}
