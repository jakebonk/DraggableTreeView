package com.allyants.draggabletreeview;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;

/**
 * Created by jbonk on 6/16/2017.
 */

public class TreeNode {

    private int level = 0;
    private boolean isRoot = false;
    private TreeNode parent;
    private boolean isCollapsed = false;
    private ArrayList<TreeNode> children = new ArrayList<>();
    private Object data;
    private View view;

    public TreeNode(Context context){
        isRoot = true;
        view = new LinearLayout(context);
        view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        ((LinearLayout)view).setOrientation(LinearLayout.VERTICAL);
    }

    public TreeNode(Object data){
        this.data = data;
    }

    public TreeNode(Object data,TreeNode parent){
        this.data = data;
        this.parent = parent;
        level = parent.level+1;
    }

    public void setView(View view){
        this.view = view;
    }

    public View getView(){
        return view;
    }

    public TreeNode getParent(){
        return parent;
    }

    public boolean isRoot(){
        return isRoot;
    }

    public Object getData(){
        return data;
    }

    public boolean isCollapsed(){
        return isCollapsed;
    }

    public void setExpanded(boolean expanded){
        isCollapsed = !expanded;
    }



    public int getLevel(){

        return countParent();
    }

    private int countParent(){
        if(parent != null) {
            return parent.countParent()+1;
        }else{
            return 0;
        }
    }

    public ArrayList<TreeNode> getChildren(){
        return children;
    }

    public TreeNode setParent(TreeNode parent,int pos){
        this.parent = parent;
        level = parent.level + 1;
        parent.addChild(this,pos);
        return this;
    }

    public TreeNode setParent(TreeNode parent){
        this.parent = parent;
        parent.addChild(this);
        return this;
    }

    public void addChild(TreeNode node){
        node.parent = this;
        children.add(node);
    }

    public void addChild(TreeNode node,int position){
        node.parent = this;
        if(position <= -1){
            position = 0;
        }
        children.add(position,node);
    }

    public void addChildren(ArrayList<TreeNode> nodes){
        for(int i = nodes.size()-1;i >= 0;i++){
            nodes.get(i).setParent(this);
            children.add(nodes.get(i));
        }
    }

    public void removeChild(TreeNode node){
        children.remove(node);
    }

    public void removeChildren(ArrayList<TreeNode> nodes){
        children.removeAll(nodes);
    }

}
