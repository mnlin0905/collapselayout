package com.knowledge.mnlin.collapselayout;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.annotation.IdRes;
import android.support.annotation.Keep;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 功能---- 可折叠的布局
 * <p>
 * Created by MNLIN on 2017/11/20.
 */

public class CollapseLayout extends LinearLayout implements View.OnClickListener {
    private Context context;
    private AttributeSet attrs;
    private TextView tv_title, tv_content;

    //展开状态应当的高度/折叠状态应当具有的高度
    private int unfoldHeight;
    private int foldHeight;
    private float currentHeight;

    //是否处于折叠状态
    private boolean isCollapsed = true;

    //是否是第一次初始化视图
    private boolean isFirstLayout = true;

    //设置动画时间
    private long collapseTime = 400;
    private long expandTime = 400;

    //父布局的id
    private ViewGroup parent;

    //动画是否同时开始
    private boolean together;

    public CollapseLayout(Context context) {
        this(context, null);
    }

    public CollapseLayout(ViewGroup parent, Context context) {
        this(context);
        this.parent = parent;
    }

    public CollapseLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CollapseLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public CollapseLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        this.attrs = attrs;
        init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        //设定自身高度为标题的高度
        if (unfoldHeight == 0 && foldHeight == 0) {
            unfoldHeight = getMeasuredHeight();
            foldHeight = tv_title.getMeasuredHeight();
        }
        setMeasuredDimension(getMeasuredWidth(), foldHeight + (int) (currentHeight * (unfoldHeight - foldHeight)));
    }

    /**
     * 初始化视图
     */
    private void init() {
        inflate(context, R.layout.layout_collapse, this);

        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        setLayoutParams(params);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setBackground(getResources().getDrawable(R.drawable.selector_background_white_pressed_little_gray, null));
        } else {
            setBackground(getResources().getDrawable(R.drawable.selector_background_white_pressed_little_gray));
        }
        setOrientation(VERTICAL);

        tv_title = findViewById(R.id.tv_title);
        tv_content = findViewById(R.id.tv_content);

        /*
        * 获取布局的框架父管理布局
        * */
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.CollapseLayout);
        @IdRes final int collapseLayoutParent = array.getResourceId(R.styleable
                .CollapseLayout_CollapseLayout_parent, -1);
        if (collapseLayoutParent != -1) {
            post(() -> parent = CollapseLayout.this.getRootView().findViewById(collapseLayoutParent));
        }
        array.recycle();

        setOnClickListener(this);
    }

    @Override
    public final void onClick(View v) {
        if (isCollapsed) {
            if (together) {
                expand(null);
                if (parent != null) {
                    collapseChild(parent);
                }
            } else {
                expand(() -> {
                    if (parent != null) {
                        collapseChild(parent);
                    }
                });
            }
        } else {
            collapse(this);
        }
    }

    /**
     * 进行折叠
     */
    private void collapse(CollapseLayout layout) {
        collapse(layout, collapseTime);
    }

    /**
     * 进行折叠
     */
    private void collapse(CollapseLayout layout, long animatorTime) {
        layout.isCollapsed = true;
        Animator animator = ObjectAnimator.ofFloat(layout, "heights", 1, 0);
        animator.setDuration(animatorTime);
        animator.start();
    }

    /**
     * 打开折叠内容
     */
    private void expand(Callback onFinish) {
        isCollapsed = false;
        Animator animator = ObjectAnimator.ofFloat(this, "heights", 0, 1);
        animator.setDuration(expandTime);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                animate().setListener(null);
                if (onFinish != null) {
                    onFinish.run();
                }
            }
        });
        animator.start();
    }

    /**
     * 将子View中CollapseLayout类型的进行折叠
     */
    private void collapseChild(ViewGroup parent) {
        int childAmount = parent.getChildCount();
        CollapseLayout item;
        for (int i = 0; i < childAmount; i++) {
            View view = parent.getChildAt(i);
            if (view instanceof CollapseLayout) {
                item = (CollapseLayout) view;
                if (!item.isCollapsed && item != this) {
                    collapse(item);
                }
            } else if (view instanceof ViewGroup) {
                collapseChild((ViewGroup) view);
            }
        }
    }

    /**
     * 设置折叠式同组的“父布局”
     */
    public CollapseLayout setCollapseParent(ViewGroup parent) {
        this.parent = parent;
        return this;
    }

    /**
     * 设置动画分两步完成
     */
    public CollapseLayout setAnimatorTogether(boolean together) {
        this.together = together;
        return this;
    }

    /**
     * 设置标题和内容
     */
    public CollapseLayout setTitleAndContent(CharSequence title, CharSequence content) {
        tv_title.setText(title);
        tv_content.setText(content);
        return this;
    }

    /**
     * 设置标题和内容
     */
    public CollapseLayout setTitleAndContent(@StringRes int title, @StringRes int content) {
        tv_title.setText(getContext().getResources().getString(title));
        tv_content.setText(getContext().getResources().getString(content));
        return this;
    }

    /**
     * 设置标题
     */
    public CollapseLayout setTitle(CharSequence title) {
        tv_title.setText(title);
        return this;
    }

    /**
     * 设置内容
     */
    public CollapseLayout setContent(CharSequence content) {
        tv_content.setText(content);
        return this;
    }

    /**
     * 设置动画持续时间
     */
    public CollapseLayout setDuration(long expandTime, long collapseTime) {
        this.expandTime = expandTime;
        this.collapseTime = collapseTime;
        return this;
    }


    /**
     * 获取标题view
     */
    public TextView getTitleView() {
        return tv_title;
    }

    /**
     * 获取内容view
     */
    public TextView getContentView() {
        return tv_title;
    }


    /**
     * 动态改变布局的高度处理
     */
    @Keep
    private void setHeights(float height) {
        currentHeight = height;
        requestLayout();
    }

    /**
     * 获取布局的高度
     */
    @Keep
    private float getHeights() {
        return currentHeight;
    }

    private interface Callback {
        void run();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (!isCollapsed) {
            collapse(this, 0);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }
}
