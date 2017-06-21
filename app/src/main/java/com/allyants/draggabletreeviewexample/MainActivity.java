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
        root.addChild(item2);
        root.addChild(item);
        SimpleTreeViewAdapter adapter = new SimpleTreeViewAdapter(this,root);
        draggableTreeView.setAdapter(adapter);
    }
}
