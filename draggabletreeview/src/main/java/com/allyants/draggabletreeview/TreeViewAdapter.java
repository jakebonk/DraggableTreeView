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
    View placeholder;

    public TreeViewAdapter(Context context,TreeNode root){
        this.root = root;
        this.context = context;
        this.placeholder = View.inflate(context,R.layout.tree_view_item_placeholder,null);
    }

    public TreeViewAdapter(Context context,TreeNode root, View placeholder){
        this.context = context;
        this.root = root;
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
