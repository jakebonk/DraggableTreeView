package com.allyants.draggabletreeview;

import android.content.Context;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by jbonk on 6/16/2017.
 */

public abstract class TreeViewAdapter {

    Context context;
    TreeNode root;
    View placeholder,bad_placeholder;

    public TreeViewAdapter(Context context,TreeNode root){
        this.root = root;
        this.context = context;
        this.placeholder = View.inflate(context,R.layout.tree_view_item_placeholder,null);
        this.bad_placeholder = View.inflate(context,R.layout.tree_view_item_bad_placeholder,null);
    }

    public void setBadPlaceholder(View bad_placeholder){
        this.bad_placeholder = bad_placeholder;
    }

    public void setPlaceholder(View placeholder){
        this.placeholder = placeholder;
    }

    private DraggableTreeView draggableTreeView;

    public void setDraggableTreeView(DraggableTreeView draggableTreeView){
        this.draggableTreeView = draggableTreeView;
    }

    public DraggableTreeView getDraggableTreeView(){
        return this.draggableTreeView;
    }


    public void setTreeViews(){
        ArrayList<TreeNode> children = root.getChildren();
        for(int i = 0; i < children.size();i++){
            boolean hasChildren = false;
            if(children.get(i).getChildren().size() != 0){
                hasChildren = true;
            }
            children.get(i).setView(createTreeView(context,children.get(i),children.get(i).getData(),children.get(i).getLevel(),hasChildren));
            setTreeNodeView(children.get(i));
        }
    }

    public void setTreeNodeView(TreeNode node){
        ArrayList<TreeNode> children = node.getChildren();
        for(int i = 0; i < children.size();i++){
            boolean hasChildren = false;
            if(children.get(i).getChildren().size() != 0){
                hasChildren = true;
            }
            children.get(i).setView(createTreeView(context,children.get(i),children.get(i).getData(),children.get(i).getLevel(),hasChildren));
            setTreeNodeView(children.get(i));
        }
    }

    public abstract View createTreeView(Context context,TreeNode node,Object data, int level,boolean hasChildren);

}
