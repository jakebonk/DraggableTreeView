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

    public int getPosition(){
        if(this.getParent() != null) {
            return this.getParent().getChildren().indexOf(this);
        }else{
            return -1;
        }
    }

    public TreeNode setParent(TreeNode parent,int pos){
        if(this.parent!=null){
            if(this.parent == parent){
                if(this.parent.getChildren().indexOf(this) > pos){
                    pos += 1;
                }
            }
            this.parent.removeChild(this);
        }
        this.parent = parent;
        level = parent.level + 1;
        parent.addChild(this,pos);
        return this;
    }

    public TreeNode setParent(TreeNode parent){
        if(this.parent!=null){
            this.parent.removeChild(this);
        }
        this.parent = parent;
        parent.addChild(this);
        return this;
    }

    public void addChild(TreeNode node){
        if(node.parent!=null){
            node.parent.removeChild(node);
        }
        node.parent = this;
        children.add(node);
    }

    public void addChild(TreeNode node,int position){
        if(node.parent!=null){
            node.parent.removeChild(node);
        }
        node.parent = this;
        if(position <= -1){
            position = 0;
        }
        children.add(position,node);
    }

    public int getChildLevel(){
        return getChildLevelRecursive() - 1;
    }

    private int getChildLevelRecursive(){
        int temp = 0;
        for(int i = 0; i < children.size();i++){
            if(temp < children.get(i).getChildLevelRecursive()){
                temp = children.get(i).getChildLevelRecursive();
            }
        }
        return temp + 1;
    }

    public boolean removeChild(TreeNode node){
        return children.remove(node);
    }


}
