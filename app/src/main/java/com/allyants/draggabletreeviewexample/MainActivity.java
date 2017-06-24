package com.allyants.draggabletreeviewexample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.allyants.draggabletreeview.DraggableTreeView;
import com.allyants.draggabletreeview.SimpleTreeViewAdapter;
import com.allyants.draggabletreeview.TreeNode;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DraggableTreeView draggableTreeView = (DraggableTreeView)findViewById(R.id.dtv);
        TreeNode root = new TreeNode(this);
        TreeNode item = new TreeNode("Item 1");
        TreeNode item2 = new TreeNode("Item 2");

        TreeNode subitem = new TreeNode("Sub Item 2");
        subitem.addChild(new TreeNode("Sub Sub Item 1"));
        item.addChild(subitem);
        item.addChild(new TreeNode("Sub Item 1"));
        root.addChild(new TreeNode("Item 3"));
        root.addChild(new TreeNode("Item 4"));
        root.addChild(new TreeNode("Item 5"));
        root.addChild(new TreeNode("Item 6"));
        root.addChild(new TreeNode("Item 7"));
        root.addChild(new TreeNode("Item 8"));
        root.addChild(new TreeNode("Item 9"));
        root.addChild(new TreeNode("Item 10"));
        root.addChild(new TreeNode("Item 11"));
        root.addChild(new TreeNode("Item 12"));
        root.addChild(item2);
        root.addChild(item);
//        draggableTreeView.maxLevels = 2;
        SimpleTreeViewAdapter adapter = new SimpleTreeViewAdapter(this,root);
        draggableTreeView.setAdapter(adapter);
    }
}
