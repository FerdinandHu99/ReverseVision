package com.ferdinand.reversevision;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.util.List;

public class AuxiliaryLineView extends View {
    public static class Point {
        public float x, y;

        public Point(float x, float y) {
            this.x = x;
            this.y = y;
        }
    }


    public enum GuideLineType {
        STATIC,      // 静态引导线
        DYNAMIC,     // 动态引导线
        TOP_VIEW     // 俯视引导线
    }

    public static class GuideLine {
        public List<Point> points;     // 点集
        public GuideLineType type;    // 引导线类型

        public GuideLine(List<Point> points, GuideLineType type) {
            this.points = points;
            this.type = type;
        }
    }


    private Paint paint;
    private GuideLine guideLine;

    private boolean isEditMode = false;

    public void setEditMode(boolean editMode) {
        isEditMode = editMode;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        switch (guideLine.type) {
            case STATIC:
                drawStaticGuideLine(canvas);
                break;
            case DYNAMIC:
                drawDynamicGuideLine(canvas);
                break;
            case TOP_VIEW:
                drawTopViewGuideLine(canvas);
                break;
        }


//        canvas.drawRect(50, 50, 200, 200, paint);
    }

    private void drawStaticGuideLine(Canvas canvas) {

    }

    private void drawDynamicGuideLine(Canvas canvas) {

    }

    private void drawTopViewGuideLine(Canvas canvas) {
        if (guideLine == null || guideLine.points.size() != 8) return;

        for (int i = 0; i < guideLine.points.size() - 1; i += 2) {
            Point startPoint = guideLine.points.get(i);
            Point endPoint = guideLine.points.get(i + 1);

            // 根据点的序号设置线条样式
            if (i == 4) { // 第5、6点绘制虚线
                paint.setPathEffect(new DashPathEffect(new float[]{10, 10}, 0)); // 虚线间隔
            } else { // 其他点绘制实线
                paint.setPathEffect(null); // 实线
            }

            // 绘制线段
            paint.setColor(Color.YELLOW); // 始终保持线条为黄色
            canvas.drawLine(startPoint.x, startPoint.y, endPoint.x, endPoint.y, paint);

            // 在编辑模式下绘制红色点
            if (isEditMode) {
                paint.setColor(Color.RED); // 在编辑模式下使用红色
                paint.setStyle(Paint.Style.FILL);
                canvas.drawCircle(startPoint.x, startPoint.y, 5, paint); // 绘制起点
                canvas.drawCircle(endPoint.x, endPoint.y, 5, paint); // 绘制终点
            }
        }
    }


    public AuxiliaryLineView(Context context) {
        super(context);
        init(context);
    }

    public AuxiliaryLineView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public AuxiliaryLineView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5f);
        paint.setColor(Color.YELLOW);

        setEditMode(false);
        List<Point> testPoints = new java.util.ArrayList<>();
        testPoints.add(new Point(200, 400));
        testPoints.add(new Point(200, 100));
        testPoints.add(new Point(300, 100));
        testPoints.add(new Point(800, 100));
        testPoints.add(new Point(300, 300));
        testPoints.add(new Point(800, 300));
        testPoints.add(new Point(900, 100));
        testPoints.add(new Point(900, 400));
        guideLine = new GuideLine(testPoints, GuideLineType.TOP_VIEW);
    }


}
