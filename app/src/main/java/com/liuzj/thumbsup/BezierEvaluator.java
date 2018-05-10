package com.liuzj.thumbsup;

import android.animation.TypeEvaluator;
import android.graphics.PointF;

/**
 * 创建时间: 2018/5/10
 *
 * @author Liuzj
 * 功能描述: 贝塞尔曲线估值器
 */
public class BezierEvaluator implements TypeEvaluator<PointF> {
    /**
     * 起始点
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
