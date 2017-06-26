[![](https://jitpack.io/v/jakebonk/DraggableTreeView.svg)](https://jitpack.io/#jakebonk/DraggableTreeView)

# DraggableTreeView
DraggableTreeView is a custom view that mimics a Tree View directory and also implements drag and drop. The tree view can go to the n-th level by default there is no limit.

## Example

![Basic Example](https://thumbs.gfycat.com/ConfusedPerkyDwarfmongoose-size_restricted.gif)

## Download library with Jitpack.io
Add this to your build.gradle file for your app.

```java
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```

Add this to your dependencies in build.gradle for your project.

```java
	dependencies {
	        compile 'com.github.jakebonk:DraggableTreeView:1.0.1'
	}
```
  
  ## Usage
  
  DraggableTreeView is organized by a custom adapter called TreeViewAdapter, I included a Simple implementation of the adapter class called SimpleTreeViewAdapter. 

```java
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
      SimpleTreeViewAdapter adapter = new SimpleTreeViewAdapter(this,root);
      draggableTreeView.setAdapter(adapter);
```

You can also change both the succesful placeholder as well as the bad placeholder by passing a view through the function 

```java
      adapter.setBadPlaceholder(view);
```
or
```java
      adapter.setPlaceholder(view);      
```

By assigning the variable maxLevel a value you can define how many levels the tree view can drag to.
```java
      draggableTreeView.maxLevels = 4;     
```

## Callback functions

There are three call back functions that can be used, onStartDrag, onChangedPosition, and onEndDrag to use it simply call setOnDragItemListener

Example:
```java
	draggableTreeView.setOnDragItemListener(new DraggableTreeView.DragItemCallback() {
            @Override
            public void onStartDrag(View item, TreeNode node) {
                Log.e("start",(String)node.getData());
            }

            @Override
            public void onChangedPosition(View item, TreeNode child, TreeNode parent, int position) {
                Log.e("changed",(String)parent.getData()+" > "+(String)child.getData()+":"+String.valueOf(position));
            }

            @Override
            public void onEndDrag(View item, TreeNode child, TreeNode parent, int position) {
                Log.e("end",(String)parent.getData()+" > "+(String)child.getData()+":"+String.valueOf(position));
            }
        });
```



  
