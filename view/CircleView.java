package create.persion.com.testapp.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import create.persion.com.testapp.CirclePoint;
import create.persion.com.testapp.DensityUtil;

/**
 * Created by Fj on 2018/7/11 0011.
 */
public class CircleView extends View {
    /**
     * 外部起始角度
     */
    private float startAngle;
    /**
     * 终结的角度
     */
    private float endAngle = 90;
    /**
     * 总共的角度
     */
    private float allAngle;
    /**
     * 单个item角度
     */
    private float itemAngle = 24;
    /**
     * 圆弧半径
     */
    private float mRadious;
    /**
     * 圆弧宽度
     */
    private float strokeWidth = 5;
    private int itemCount = 10;
    private Paint circlerPaint;
    private float defaultHeight;
    private Context mContext;
    private CirclePoint circlePoint;
    private float left;
    private float right;
    private float top;
    private float bottom;
    private float siwpProgress =0;
    private float circleX;
    private float circleY;
    private Paint progressPaint;
    private int  progressItemCount =1;
    private float startProgressAngle;

    public CircleView(Context context) {
        this(context, null);
    }

    public CircleView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        mRadious = (float) ((getResources().getDisplayMetrics().widthPixels *1.5)/ 2);
        initView();
        saveCirclePoint();
    }

    /**
     * 计算保存圆心的位置
     */
    private void saveCirclePoint() {
        //默认高度，只在屏幕显示的高度
        defaultHeight = DensityUtil.dip2px(mContext, 200);
        //圆心位置
        circleX = getResources().getDisplayMetrics().widthPixels / 2;
        circleY = defaultHeight - mRadious;
        circlePoint = new CirclePoint(circleX, circleY);
        caculateRect(circlePoint);
        caculateStartAngle();
    }

    /**
     * 通过圆弧最终角度，计算起始角度。
     */
    private void caculateStartAngle() {
        allAngle = itemAngle * itemCount;
        startAngle = endAngle - allAngle;
        startProgressAngle =endAngle-progressItemCount*(itemAngle);
        endAngle =90;
    }

    /**
     * 计算圆弧rect left ，top ，right，bottom的位置
     *
     * @param circlePoint
     */
    private void caculateRect(CirclePoint circlePoint) {
        //拿到圆心
        float pointX = circlePoint.getX();
        float pointY = circlePoint.getY();

        //计算圆弧的rect的top，left，right，bottom。
        left = pointX - mRadious;
        top = -mRadious + pointY;
        right = pointX + mRadious;
        bottom = pointY + mRadious;

    }

    /**
     * 初始化画笔
     */
    private void initView() {
        //底色圆弧
        circlerPaint = new Paint();
        circlerPaint.setDither(true);
        circlerPaint.setAntiAlias(true);
        circlerPaint.setColor(Color.parseColor("#e7e4e4"));
        circlerPaint.setStyle(Paint.Style.STROKE);
        circlerPaint.setStrokeCap(Paint.Cap.ROUND);
        circlerPaint.setStrokeWidth(DensityUtil.dip2px(mContext, strokeWidth));

        //进度圆弧
        progressPaint = new Paint();
        progressPaint.setDither(true);
        progressPaint.setAntiAlias(true);
        progressPaint.setColor(Color.parseColor("#ffe711"));
        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setStrokeWidth(DensityUtil.dip2px(mContext, strokeWidth));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.rotate(siwpProgress,circleX,circleY);
        RectF rect = new RectF(left, top, right, bottom);
        canvas.drawArc(rect, startAngle, allAngle, false, circlerPaint);
        canvas.drawArc(rect,startProgressAngle,progressItemCount*itemAngle,false,progressPaint);



    }

    public void setRadous(float radous) {
        this.mRadious = radous;
        saveCirclePoint();
        invalidate();
    }

    public void setEndAngle(float end) {
        this.endAngle = end;
        saveCirclePoint();
        invalidate();
    }

    public void setItemAngle(float item) {
        this.itemAngle = item;
        saveCirclePoint();
        invalidate();
    }

    public void setItemCount(int itemCount) {
        this.itemCount = itemCount;
        saveCirclePoint();
        invalidate();
    }

    public void setSwipProgress(double progress) {
        this.siwpProgress = (float) progress;
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //进行测量将圆弧的宽高。
        int width = (int) (2 * mRadious + 2 * strokeWidth);
        int height = (int) (defaultHeight + strokeWidth);
        setMeasuredDimension(width, height);
    }
    public void setProgressItem(int count){
        this.progressItemCount = count;
        saveCirclePoint();
        invalidate();
    }

    public float getVisableHeight() {
        return defaultHeight + strokeWidth / 2;
    }

    public float getVisableWidth() {
        return getResources().getDisplayMetrics().widthPixels / 2;
    }

    public CirclePoint getCirclePoint() {
        return circlePoint;
    }

    public float getItemAngle() {
        return itemAngle;
    }

    public int getItemCount() {
        return itemCount;
    }

    public float getmRadious() {
        return mRadious;
    }
}
