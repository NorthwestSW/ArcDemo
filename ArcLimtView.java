package create.persion.com.testapp;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by Fj on 2018/7/17 0017.
 * 参考博客：https://blog.csdn.net/qq_35114086/article/details/53318978
 *           https://blog.csdn.net/lmj623565791/article/details/43131133
 */
public class ArcLimtView extends ViewGroup {
    private final Context mContext;
    /**
     * 布局时的开始角度
     */
    private double mStartAngle = 90;
    /**
     * defaultRadious 屏幕半径
     */
    private int defaultRadious;
    /**
     * defaultHeightSize 默认显示高度。配合CircleView使用
     */
    private int defaultHeightSize;
    /**
     * 该viewgroup要平移的距离
     */
    private int pointOffsetY;
    /**
     * 该viewgroup的半径
     */
    private int mRadius;
    /**
     *
     */
    private int heightRadius;

    /**
     * 判断是否正在自动滚动
     */
    private boolean isTouchUp = true;

    public static final float DEFAULT_BANNER_WIDTH = 750.0f;        //中间显示的图标大小
    public static final float DEFAULT_BANNER_HEIGTH = 480.0f;       //其余图标大小

    /**
     * 当前中间顶部的那个
     */
    private int mCurrentPosition = 0;

    /**
     * 该容器内child item的默认尺寸
     */
    private static final float RADIO_DEFAULT_CHILD_DIMENSION = 45.0f;

    private static final float RADIO_TOP_CHILD_DIMENSION = 65.0f;
    /**
     * 总共的个数
     */
    private int overCount = 15;

    /**
     * 该容器的内边距,无视padding属性，如需边距请用该变量
     */
    private float mPadding;
    private static final int RADIO_MARGIN_LAYOUT = 0;
    /**
     * 未被选中的item的宽度。
     */
    private int reStartWidth;

    /**
     * 检测按下到抬起时旋转的角度
     */
    private float mTmpAngle;
    /**
     * 根据角度计算限制滚动的总角度
     */
    private int limtCount = 10;
    /**
     * 单个滚动角度
     */
    private double swifAngle = 24;
    /**
     * 菜单项的图标
     */
    private int[] mItemImgs;
    /**
     * 菜单项的文本
     */
    private String[] mItemTexts;
    /**
     * 菜单的个数
     */
    private int mMenuItemCount;
    public ArcLimtView(Context context) {
        this(context,null);
    }

    public ArcLimtView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public ArcLimtView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        // 无视padding
        setPadding(0, 0, 0, 0);
        defaultHeightSize = DensityUtil.dip2px(context, 200);
        defaultRadious = getResources().getDisplayMetrics().widthPixels / 2;
    }
    /**
     * 设置布局的宽高，并策略menu item宽高
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int resWidth = 0;
        int resHeight = 0;
        double startAngle = mStartAngle;

        double angle = 360 / overCount;
        /**
         * 根据传入的参数，分别获取测量模式和测量值
         */
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);

        int height = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        /**
         * 如果宽或者高的测量模式非精确值
         */
        if (widthMode != MeasureSpec.EXACTLY
                || heightMode != MeasureSpec.EXACTLY) {
            // 主要设置为背景图的高度

            resWidth = getDefaultWidth();

            resHeight = (int) (resWidth * DEFAULT_BANNER_HEIGTH /
                    DEFAULT_BANNER_WIDTH);

        } else {
            // 如果都设置为精确值，则直接取小值；
            resHeight = resWidth =getDefaultWidth();
        }
        setMeasuredDimension(resWidth, resHeight);

        // 获得直径
        mRadius = Math.max(getMeasuredWidth(), getMeasuredHeight());
        heightRadius = Math.min(getMeasuredWidth(), getMeasuredHeight());

        // menu item数量
        final int count = getChildCount();
        // menu item尺寸
        int childSize;

        // menu item测量模式
        int childMode = MeasureSpec.EXACTLY;
        // 迭代测量：根据孩子的数量进行遍历，为每一个孩子测量大小，设置监听回调。
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            startAngle = startAngle % 360;
            if (startAngle > 89 && startAngle < 91 && isTouchUp) {
                if (mOnMenuItemClickListener != null) {
                    mOnMenuItemClickListener.itemClick(i);
                }
                //设置监听回调。
                mCurrentPosition = i;                       //本次使用mCurrentPosition，只是把他作为一个temp变量。可以有更多的使用，比如动态设置每个孩子相隔的角度
                childSize = DensityUtil.dip2px(getContext(), RADIO_TOP_CHILD_DIMENSION);            //设置大小
            } else {
                childSize = DensityUtil.dip2px(getContext(), RADIO_DEFAULT_CHILD_DIMENSION);
            }
            if (child.getVisibility() == GONE) {
                continue;
            }
            // 计算menu item的尺寸；以及和设置好的模式，去对item进行测量
            int makeMeasureSpec = -1;

            makeMeasureSpec = MeasureSpec.makeMeasureSpec(childSize,
                    childMode);
            child.measure(makeMeasureSpec, makeMeasureSpec);
            startAngle += angle;
        }
        //item容器内边距
        mPadding = DensityUtil.dip2px(getContext(), RADIO_MARGIN_LAYOUT);



    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        //布置圆的半径

        int layoutRadius = mRadius;
        pointOffsetY = layoutRadius - defaultHeightSize;
        // Laying out the child views
        final int childCount = getChildCount();

        int left, top;
        // menu item 的尺寸
        int cWidth;

        // 根据menu item的个数，计算角度
        float angleDelay = 360 / overCount;
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            //根据孩子遍历，设置中间顶部那个的大小以及其他图片大小。
            //处于中间位置并且是手指up的时候
            if (mStartAngle > 89.9 && mStartAngle < 90.1 && isTouchUp) {
                cWidth = DensityUtil.dip2px(getContext(), RADIO_TOP_CHILD_DIMENSION);
                bitItem(child);
                child.setSelected(true);
            }else{
                cWidth = DensityUtil.dip2px(getContext(), RADIO_DEFAULT_CHILD_DIMENSION);
                smartItem(child);
                child.setSelected(false);
                reStartWidth = cWidth;
            }
            if (child.getVisibility() == GONE) {
                continue;
            }
            //大于360就取余归于小于360度
            mStartAngle = mStartAngle % 360;

            float tmp = 0;
            //计算图片布置的中心点的圆半径。就是tmp
            // tmp cosa 即menu item中心点的横坐标。计算的是item的位置，是计算位置！！！
            if (mStartAngle > 89.9 && mStartAngle < 90.1 && isTouchUp) {
                tmp = layoutRadius / 2f - cWidth / 2 - mPadding;
                // left = caulaterLeft(layoutRadius,mStartAngle,cWidth,tmp);
                left = defaultRadious
                        + (int) Math.round(tmp
                        * Math.cos(Math.toRadians(mStartAngle)) - 1 / 2f
                        * cWidth) + DensityUtil
                        .dip2px(getContext(), 0);
                // tmp sina 即menu item的纵坐标
                top = (int) ((layoutRadius - 1 / 2f * cWidth) + DensityUtil
                        .dip2px(getContext(), 8) - pointOffsetY);

                //接着当然是布置孩子的位置啦，就是根据小圆的来布置的
                child.layout(left, top, left + cWidth, top + cWidth);
            } else {
                tmp = layoutRadius / 2f - reStartWidth / 2 - mPadding;
                if ((Math.cos(Math.toRadians(mStartAngle)) <= 0)) {
                    left = defaultRadious
                            + (int) Math.round(tmp
                            * Math.cos(Math.toRadians(mStartAngle)) - 2 / 2f
                            * reStartWidth) + DensityUtil
                            .dip2px(getContext(), 5);
                } else {
                    left = defaultRadious +
                            +(int) Math.round(tmp
                                    * Math.cos(Math.toRadians(mStartAngle))
                            ) + DensityUtil
                            .dip2px(getContext(), -5);
                }
                // tmp sina 即menu item的纵坐标
                top = layoutRadius
                        / 2
                        + (int) Math.round(tmp
                        * Math.sin(Math.toRadians(mStartAngle)) - 1 / 2f * reStartWidth) + DensityUtil
                        .dip2px(getContext(), 25) - pointOffsetY;

                //接着当然是布置孩子的位置啦，就是根据小圆的来布置的
                child.layout(left, top, left + cWidth, top + reStartWidth);
            }
            // 叠加尺寸
            mStartAngle += angleDelay;
        }

    }
    /**
     * 选中之后的图片和文字大小
     *
     * @param child
     */
    private void bitItem(View child) {
        RelativeLayout relativeLayout = (RelativeLayout) child;
        ImageView iv = (ImageView) relativeLayout.getChildAt(0);
        TextView tv = (TextView) relativeLayout.getChildAt(1);
        final AnimatorSet bigAnimatorSet = new AnimatorSet();//组合动画
        final ObjectAnimator scaleX = ObjectAnimator.ofFloat(iv, "scaleX", 1f, 1.3f);
        final ObjectAnimator scaleY = ObjectAnimator.ofFloat(iv, "scaleY", 1, 1.3f);
        final ObjectAnimator scaleTvX = ObjectAnimator.ofFloat(tv, "scaleX", 1f, 1.3f);
        final ObjectAnimator scaleTvY = ObjectAnimator.ofFloat(tv, "scaleY", 1, 1.3f);
        bigAnimatorSet.setDuration(50);
        bigAnimatorSet.play(scaleX).with(scaleY).with(scaleTvX).with(scaleTvY);//两个动画同时开始
        bigAnimatorSet.start();
    }

    /**
     * 未选中的item的图片文字大小
     *
     * @param child
     */
    private void smartItem(View child) {
        RelativeLayout relativeLayout = (RelativeLayout) child;
        ImageView iv = (ImageView) relativeLayout.getChildAt(0);
        TextView tv = (TextView) relativeLayout.getChildAt(1);
        final AnimatorSet smartAnimatorSet = new AnimatorSet();//组合动画
        final ObjectAnimator scaleX = ObjectAnimator.ofFloat(iv, "scaleX", 1f, 1f);
        final ObjectAnimator scaleY = ObjectAnimator.ofFloat(iv, "scaleY", 1, 1f);
        final ObjectAnimator scaleTvX = ObjectAnimator.ofFloat(tv, "scaleX", 1f, 1f);
        final ObjectAnimator scaleTvY = ObjectAnimator.ofFloat(tv, "scaleY", 1, 1f);
        smartAnimatorSet.setDuration(50);
        smartAnimatorSet.play(scaleX).with(scaleY).with(scaleTvX).with(scaleTvY);//两个动画同时开始
        smartAnimatorSet.start();
    }
    /**
     * 获得默认该layout的尺寸
     *
     * @return
     */
    private int getDefaultWidth() {
        WindowManager wm = (WindowManager) getContext().getSystemService(
                Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return (int) Math.min(outMetrics.widthPixels * 1.5, outMetrics.heightPixels * 1.5);
    }


    /**
     * MenuItem的点击事件接口
     *
     * @author zhy
     */
    public interface OnMenuItemClickListener {
        void itemClick(int pos);

    }
    /**
     * MenuItem的点击事件接口
     */
    private OnMenuItemClickListener mOnMenuItemClickListener;

    /**
     * 设置MenuItem的点击事件接口
     *
     * @param mOnMenuItemClickListener
     */
    public void setOnMenuItemClickListener(
            OnMenuItemClickListener mOnMenuItemClickListener) {
        this.mOnMenuItemClickListener = mOnMenuItemClickListener;
    }
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }
    /**
     * 记录上一次的x，y坐标
     */
    private float mLastX;
    private float mLastY;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastX = x;
                mLastY = y;
                mTmpAngle = 0;
                Log.d("", "dispatchTouchEvent: ");
                break;
            case MotionEvent.ACTION_MOVE:
                isTouchUp = false;          //注意isTouchUp 这个标记量！！！
                /**
                 * 获得开始的角度
                 */
                float start = getAngle(mLastX, mLastY);
                /**
                 * 获得当前的角度
                 */
                float end = getAngle(x, y);
                // 如果是一、四象限，则直接end-start，角度值都是正值
                //限制旋转角度。
                if (mStartAngle <= limtCount * swifAngle + 90 && mStartAngle >= 90) {
                    if (getQuadrant(x, y) == 1 || getQuadrant(x, y) == 4) {
                        mStartAngle += end - start;
                        mTmpAngle += end - start;
                    } else
                    // 二、三象限，色角度值是付值
                    {
                        mStartAngle += start - end;
                        mTmpAngle += start - end;
                    }

                }

                // 重新布局
                if (mTmpAngle != 0) {
                    refreshLayout(mStartAngle);
                }

                mLastX = x;
                mLastY = y;

                break;
            case MotionEvent.ACTION_UP:
                backOrPre();
                break;
        }
        return true;
    }
    /**
     * 根据触摸的位置，计算角度
     *
     * @param xTouch
     * @param yTouch
     * @return
     */
    private float getAngle(float xTouch, float yTouch) {
        double x = xTouch - (mRadius / 2d);
        double y = yTouch - ((heightRadius / 2) / 2d);
        return (float) (Math.asin(y / Math.hypot(x, y)) * 180 / Math.PI);
    }

    /**
     * 根据当前位置计算象限
     *
     * @param x
     * @param y
     * @return
     */
    private int getQuadrant(float x, float y) {
        int tmpX = (int) (x - mRadius / 2);
        int tmpY = (int) (y - heightRadius / 2);
        if (tmpX >= 0) {
            return tmpY >= 0 ? 4 : 1;
        } else {
            return tmpY >= 0 ? 3 : 2;
        }

    }

    private void refreshLayout(double mStartAngle) {
        if (mStartAngle <= limtCount *swifAngle  + 90 && mStartAngle >= 90) {
            requestLayout();
            if (swipProgressListener != null) {
                swipProgressListener.progress(mStartAngle - 90);
            }
        }

    }
    private void backOrPre() {     //缓冲的角度。即我们将要固定几个位置，而不是任意位置。我们要设计一个可能的角度去自动帮他选择。
        isTouchUp = true;
        float angleDelay = 360 / overCount;              //这个是每个图形相隔的角度
        if ((mStartAngle - 90) % angleDelay == 0) {
            return;
        }
        float angle = (float) ((mStartAngle - 90) % swifAngle);                 //angle就是那个不是90度开始布局，然后是swifAngle度的整数的多出来的部分角度
        if (angleDelay / 2 > angle) {
            mStartAngle -= angle;
        } else if (angleDelay / 2 < angle) {
            mStartAngle = mStartAngle - angle + angleDelay;         //mStartAngle就是当前角度啦，取余swifAngle度就是多出来的角度，拿这个多出来的角度去数据处理。
        }
        refreshLayout(mStartAngle);
    }

    /**
     * 关联CircleView的接口
     */
    public interface SwipProgressListener {
        void progress(double swipProgress);
    }

    private SwipProgressListener swipProgressListener;

    public void setSwipListener(SwipProgressListener swipListener) {
        this.swipProgressListener = swipListener;
    }
    /**
     * 设置菜单条目的图标和文本
     *
     * @param resIds
     */
    public void setMenuItemIconsAndTexts(int[] resIds) {
        mItemImgs = resIds;
        // 参数检查
        if (resIds == null) {
            throw new IllegalArgumentException("菜单项文本和图片至少设置其一");
        }
        // 初始化mMenuCount
        mMenuItemCount = resIds == null ? 0 : resIds.length;
        addMenuItems();

    }

    /**
     * 添加菜单项
     */
    private void addMenuItems() {

        /**
         * 根据用户设置的参数，初始化view
         */
        for (int i = 0; i < overCount; i++) {
            final int j = i;
            View inflate = LayoutInflater.from(mContext).inflate(R.layout.layout_vip_view, null);
            RelativeLayout layout = (RelativeLayout) inflate;
            ImageView iv = (ImageView) layout.findViewById(R.id.vip);
            layout.setVisibility(View.INVISIBLE);
            if (iv != null) {
                if (i == 0){
                    layout.setVisibility(View.VISIBLE);
                    iv.setImageResource(mItemImgs[i]);
                }
                if (i >= 5){
                    layout.setVisibility(View.VISIBLE);
                    iv.setImageResource(mItemImgs[i-5]);
                }
                iv.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                });
            }
            // 添加view到容器中
            addView(inflate);
        }
    }
}
