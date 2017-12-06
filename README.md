# CollapseLayout
[![License](https://img.shields.io/aur/license/yaourt.svg)](http://www.gnu.org/licenses/gpl-3.0.html)
[![Download](https://api.bintray.com/packages/lovingning/maven/collapselayout/images/download.svg)](https://bintray.com/lovingning/maven/collapselayout/_latestVersion)

`
一款可折叠的、简单的布局；继承LinearLayout，由两个子TextView组成；可用于一般ViewGroup、ListView、RecyclerView中
`
## 准备步骤
在项目**build.gradle**中添加依赖：
```
compile 'com.knowledge.mnlin:collapselayout:0.0.2'{
    exclude module: "support-annotations"
}
```
如果提示找不到依赖文件，可能时jcenter未及时通过，可以依赖私人仓库
```
//Project的build.gradle文件
allprojects {
    repositories {
    
        ... 
        
        maven { url "https://dl.bintray.com/lovingning/maven"}
    }
}
```

## 简单使用
在ViewGroup（如LinearLayout）中添加CollapseLayout布局；
为CollapseLayout添加**CollapseLayout_parent**属性（父布局id），就可以自动展开与折叠；
```
<LinearLayout
    android:id="@+id/parent"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="vertical">

    <com.knowledge.mnlin.frame.view.CollapseLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        app:CollapseLayout_parent="@id/parent"/>

    <com.knowledge.mnlin.frame.view.CollapseLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        app:CollapseLayout_parent="@id/parent"/>

    <com.knowledge.mnlin.frame.view.CollapseLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        app:CollapseLayout_parent="@id/parent"/>
</LinearLayout>
```
## 控制多个展开与缩放的“组”
第一组为LinearLayout；第二组为ListView；第三组为RecyclerView；
设置LinearLayout组中CollapseLayout_parent为“根”父布局；
```
<LinearLayout
    android:id="@+id/root"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical">

    <include layout="@layout/layout_top_bar"/>

    <TextView
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="LinearLayout"/>

    <LinearLayout
        android:id="@+id/parent"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="vertical">

        <com.knowledge.mnlin.frame.view.CollapseLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            app:CollapseLayout_parent="@id/root"/>

        <com.knowledge.mnlin.frame.view.CollapseLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            app:CollapseLayout_parent="@id/root"/>

        <com.knowledge.mnlin.frame.view.CollapseLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            app:CollapseLayout_parent="@id/root"/>
    </LinearLayout>

    <TextView
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginTop="20dp"
        android:text="ListView"/>

    <ListView
        android:id="@+id/lv_help"
        android:layout_width="match_parent"
        android:layout_height="200dp"
        android:divider="?colorAccent"
        android:dividerHeight="1px"/>

    <TextView
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginTop="20dp"
        android:text="RecyclerView"/>

    <android.support.v7.widget.RecyclerView
        android:id="@+id/rv_help"
        android:layout_width="match_parent"
        android:layout_height="200dp"
        android:divider="?colorAccent"
        android:dividerHeight="1px"/>
</LinearLayout>
```

在ListView和RecyclerView的Adapter适配器中，新建CollapseLayout布局;
构造方法中可以传入分组的方式，直接传入**parent**，表示父布局为ListView或RecyclerView；
```
public class Adapter extends BaseAdapter {

    ...
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            CollapseLayout item = new CollapseLayout(parent, context);
            convertView = item;
        }
        
        ...
        
        return convertView;
    }
}
```

```
public class RecAdapter extends RecyclerView.Adapter<RecAdapter.ViewHolder> {

    ...
    
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(new CollapseLayout(parent, context));
    }

    ...
    
}
```

此时第二组与第三组折叠展开互不影响；第一组折叠时则会影响第二第三组数据。

## 填充数据与设置显示效果
在创建CollapseLayout对象时：
 1. 可以控制展开与折叠动画同时进行或者是分开执行。
 1. 可以更改设定“父”布局
 1. 可以设置动画时间（展开与折叠）
 1. 可以更改**标题**与**内容**
 
```
new CollapseLayout(context)
        .setAnimatorTogether(false)
        .setCollapseParent(parent)
        .setContent("content")
        .setTitle("title")
        .setTitleAndContent("title","content")
        .setDuration(EXPAND_TIME,COLLAPSE_TIME);
```

_注意：由于ListView与RecyclerView的子View复用，因此展开内容在滑动到不可见区域后会自动折叠。_


