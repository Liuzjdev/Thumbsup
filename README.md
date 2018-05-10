### 前言
`属性动画` 和 `贝赛尔曲线` 已经出来很久了，很多前辈写了很多不错的文章，在此不得不感谢这些前辈无私奉献的开源精神，能让我们站在巨人的肩膀上望得更远.如果你对属性动画还不太了解可以看看[郭林](http://blog.csdn.net/guolin_blog/article/details/43536355)的文章，贝塞尔曲线的使用可以参考[Lin_Zero](https://blog.csdn.net/z82367825/article/details/51599245)

### 效果图
![](https://i.imgur.com/W9ZWXS3.gif)

### 实现思路
整体实现思路还是比较简单的，首先要有一个容器来装点出来的赞，然后通过属性动画对赞加一些动画效果，最后通过贝塞尔曲线使其做不规则的运动。

### 代码实现
好了，思路有了，咱们也废话不多说，直接上代码，首先是所有动画的实现
<pre><code>
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
</code></pre>
然后是通过贝塞尔曲线对赞的走向做了控制，代码如下
<pre><code>
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

</code></pre>
这是估值器的实现
<pre><code>
/**
 * 创建时间: 2018/5/10
 *
 * @author Liuzj
 * 功能描述: 贝塞尔曲线估值器
 */
public class BezierEvaluator implements TypeEvaluator<PointF> {
    /**
     * 拐点
     */
    private PointF pointF1;
    private PointF pointF2;

    public BezierEvaluator(PointF pointF1,PointF pointF2) {
        this.pointF1 = pointF1;
        this.pointF2 = pointF2;
    }

    @Override
    public PointF evaluate(float v, PointF pointF, PointF t1) {
        PointF point = new PointF();
        point.x = pointF.x*(1-v)*(1-v)*(1-v)
                +3*pointF1.x*v*(1-v)* (1-v)
                +3*pointF2.x*v*v*(1-v)
                +t1.x*v*v*v;
        point.y = pointF.y*(1-v)*(1-v)*(1-v)
                +3*pointF1.y*v*(1-v)* (1-v)
                +3*pointF2.y*v*v*(1-v)
                +t1.y*v*v*v;

        return point;
    }
}
</code></pre>
可能有的朋友对`evaluate`方法里的实现有所疑惑，其实调用贝塞尔的三阶公式,然后把点跟公式里的对应上就ok了
![](https://i.imgur.com/H7VcD4f.png)

END！

Thanks

### [本项目源码](https://github.com/Liuzjdev/Thumbsup)
