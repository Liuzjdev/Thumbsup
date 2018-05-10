package com.liuzj.thumbsup;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.Random;

/**
 * 创建时间: 2018/5/9
 *
 * @author Liuzj
 * 功能描述: 点赞布局
 */
public class ThumbsUpLayout extends RelativeLayout {
    /**
     * 图片宽高
     * */
    private int dWidth;
    private int dHeight;

    /**
     * 当前控件的宽高
     * */
    private int mWidth;
    private int mHeight;

    private LayoutParams params;

    /**
     * 随机数生成
     */
    private Random random = new Random();

    /**
     * 加速，减速，先加速在减速
     */
    private Interpolator acc = new AccelerateInterpolator();
    private Interpolator dec = new DecelerateInterpolator();
    private Interpolator accdec = new AccelerateDecelerateInterpolator();
    private Interpolator[] interpolators;

    private Drawable drawable = getResources().getDrawable(R.mipmap.ic_launcher);

    public ThumbsUpLayout(Context context) {
        super(context);
        init();
    }

    public ThumbsUpLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ThumbsUpLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
    }

    private void init() {
        interpolators = new Interpolator[3];
        interpolators[0] = acc;
        interpolators[1] = dec;
        interpolators[2] = accdec;

        //获取图片原始宽高
        dWidth = drawable.getIntrinsicWidth();
        dHeight = drawable.getIntrinsicHeight();

        params = new LayoutParams(dWidth,dHeight);

    }

    public void addThumbs() {
        ImageView imageView = new ImageView(getContext());
        imageView.setImageDrawable(drawable);
        params.addRule(CENTER_HORIZONTAL);
        params.addRule(ALIGN_PARENT_BOTTOM);
        addView(imageView,params);

        //实现点赞效果 平移 缩放 渐变
        AnimatorSet animatorSet = getAnimator(imageView);
        animatorSet.start();

    }

    /**
     *  实现点赞效果 平移 缩放 渐变
     * @param imageView
     * @return 所有动画的集合
     */
    private AnimatorSet getAnimator(final ImageView imageView) {

        //缩放
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(imageView,"scaleX",0.4f,1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(imageView,"scaleY",0.4f,1f);
        //alpha
        ObjectAnimator alpha = ObjectAnimator.ofFloat(imageView,"alpha",0.4f,1f);

        //执行三个动画
        AnimatorSet enterSet = new AnimatorSet();
        enterSet.setDuration(500);
        enterSet.playTogether(scaleX,scaleY,alpha);

        //用贝塞尔曲线控制点赞的走向
        ValueAnimator bezierAnimator = getBezierAnimator(imageView);

        //综合所有动画
        AnimatorSet set = new AnimatorSet();
        //按顺序执行
        set.playSequentially(enterSet,bezierAnimator);
        //添加插值器
        set.setInterpolator(interpolators[random.nextInt(3)]);
        set.setTarget(imageView);

        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                removeView(imageView);
            }
        });
        return set;
    }

    /**
     * 通过贝塞尔曲线对赞走向做控制
     * @param imageView
     * @return
     */
    private ValueAnimator getBezierAnimator(final ImageView imageView) {

        //准备控制贝塞尔曲线的四个点（起始点p0,拐点p1,拐点p2,终点p3）
        PointF pointF0 = new PointF((mWidth-dWidth)/2,mHeight-dHeight);
        PointF pointF1 = getTogglePointF(1);
        PointF pointF2 = getTogglePointF(2);
        PointF pointF3 = new PointF(random.nextInt(mWidth),0);
        BezierEvaluator bezierEvaluator = new BezierEvaluator(pointF1,pointF2);

        final ValueAnimator animator = ValueAnimator.ofObject(bezierEvaluator,pointF0,pointF3);
        animator.setDuration(3000);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                PointF pointF = (PointF) valueAnimator.getAnimatedValue();
                //控制属性变化
                imageView.setX(pointF.x);
                imageView.setY(pointF.y);
                imageView.setAlpha(1 - valueAnimator.getAnimatedFraction());//从可见到不可见
            }
        });

        return animator;
    }

    public PointF getTogglePointF(int i) {
        PointF pointF = new PointF();
        pointF.x = random.nextInt(mWidth);

        if (i == 1) {
            //当赞在上半部分时 h/2 - h
            pointF.y = random.nextInt(mHeight/2)+mHeight/2;
        }else{
            //当赞在上半部分时 h/2 - h
            pointF.y = random.nextInt(mHeight/2);
        }

        return pointF;
    }
}
