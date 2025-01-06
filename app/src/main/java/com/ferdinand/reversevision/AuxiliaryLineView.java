package com.ferdinand.reversevision;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
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

    public void setGuideLine(GuideLine guideLine) {
        this.guideLine = guideLine;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (guideLine == null) return;
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

    // 绘制单个点（实心红色圆）
    private void drawPoint(Canvas canvas, Point point) {
        int OriginalColor = paint.getColor(); // 获取原始画笔颜色
        paint.setStyle(Paint.Style.FILL); // 设置画笔为填充模式
        paint.setColor(Color.RED); // 点的颜色为红色
        canvas.drawCircle(point.x, point.y, 5, paint); // 绘制实心圆，半径为 8
        paint.setStyle(Paint.Style.STROKE); // 恢复画笔为描边模式
        paint.setColor(OriginalColor); // 恢复线条颜色为黄色
    }

    private void drawStaticGuideLine(Canvas canvas) {
        // 假设 guideLine.points 存储了所有的点
        if (guideLine == null || guideLine.points.size() != 20) return;

        // 设置画笔的基本样式
        paint.setStyle(Paint.Style.STROKE); // 设置画笔为描边模式
        paint.setStrokeWidth(5); // 设置线条宽度

        // 绘制曲线：1-4
        paint.setColor(Color.YELLOW); // 默认黄色
        for (int i = 0; i < 4; i += 4) {
            Point startPoint = guideLine.points.get(i);
            Point controlPoint1 = guideLine.points.get(i + 1);
            Point controlPoint2 = guideLine.points.get(i + 2);
            Point endPoint = guideLine.points.get(i + 3);

            // 使用贝塞尔曲线绘制
            Path path = new Path();
            path.moveTo(startPoint.x, startPoint.y);
            path.cubicTo(controlPoint1.x, controlPoint1.y, controlPoint2.x, controlPoint2.y, endPoint.x, endPoint.y);
            canvas.drawPath(path, paint);

            // 如果是 Edit 模式，绘制点
            if (isEditMode) {
                drawPoint(canvas, startPoint);
                drawPoint(canvas, controlPoint1);
                drawPoint(canvas, controlPoint2);
                drawPoint(canvas, endPoint);
            }
        }

        // 绘制直线：5-6
        paint.setColor(Color.YELLOW); // 默认黄色
        Point startPoint = guideLine.points.get(4);
        Point endPoint = guideLine.points.get(5);
        canvas.drawLine(startPoint.x, startPoint.y, endPoint.x, endPoint.y, paint);

        // 在 Edit 模式下绘制点
        if (isEditMode) {
            drawPoint(canvas, startPoint);
            drawPoint(canvas, endPoint);
        }

        // 绘制直线：7-8
        startPoint = guideLine.points.get(6);
        endPoint = guideLine.points.get(7);
        canvas.drawLine(startPoint.x, startPoint.y, endPoint.x, endPoint.y, paint);

        // 在 Edit 模式下绘制点
        if (isEditMode) {
            drawPoint(canvas, startPoint);
            drawPoint(canvas, endPoint);
        }

        // 绘制曲线：9-12
        paint.setColor(Color.YELLOW); // 默认黄色
        for (int i = 8; i < 12; i += 4) {
            Point startPoint1 = guideLine.points.get(i);
            Point controlPoint1 = guideLine.points.get(i + 1);
            Point controlPoint2 = guideLine.points.get(i + 2);
            Point endPoint1 = guideLine.points.get(i + 3);

            // 使用贝塞尔曲线绘制
            Path path = new Path();
            path.moveTo(startPoint1.x, startPoint1.y);
            path.cubicTo(controlPoint1.x, controlPoint1.y, controlPoint2.x, controlPoint2.y, endPoint1.x, endPoint1.y);
            canvas.drawPath(path, paint);

            // 如果是 Edit 模式，绘制点
            if (isEditMode) {
                drawPoint(canvas, startPoint1);
                drawPoint(canvas, controlPoint1);
                drawPoint(canvas, controlPoint2);
                drawPoint(canvas, endPoint1);
            }
        }

        // 绘制虚线曲线：13-16
        paint.setColor(Color.YELLOW); // 继续使用黄色
        paint.setPathEffect(new DashPathEffect(new float[]{10, 10}, 0)); // 设置虚线效果
        for (int i = 12; i < 16; i += 4) {
            Point startPoint1 = guideLine.points.get(i);
            Point controlPoint1 = guideLine.points.get(i + 1);
            Point controlPoint2 = guideLine.points.get(i + 2);
            Point endPoint1 = guideLine.points.get(i + 3);

            // 使用贝塞尔曲线绘制
            Path path = new Path();
            path.moveTo(startPoint1.x, startPoint1.y);
            path.cubicTo(controlPoint1.x, controlPoint1.y, controlPoint2.x, controlPoint2.y, endPoint1.x, endPoint1.y);
            canvas.drawPath(path, paint);

            // 如果是 Edit 模式，绘制点
            if (isEditMode) {
                drawPoint(canvas, startPoint1);
                drawPoint(canvas, controlPoint1);
                drawPoint(canvas, controlPoint2);
                drawPoint(canvas, endPoint1);
            }
        }

        // 绘制曲线：17-20
        paint.setColor(Color.YELLOW); // 继续使用黄色
        paint.setPathEffect(null); // 恢复为实线
        for (int i = 16; i < 20; i += 4) {
            Point startPoint1 = guideLine.points.get(i);
            Point controlPoint1 = guideLine.points.get(i + 1);
            Point controlPoint2 = guideLine.points.get(i + 2);
            Point endPoint1 = guideLine.points.get(i + 3);

            // 使用贝塞尔曲线绘制
            Path path = new Path();
            path.moveTo(startPoint1.x, startPoint1.y);
            path.cubicTo(controlPoint1.x, controlPoint1.y, controlPoint2.x, controlPoint2.y, endPoint1.x, endPoint1.y);
            canvas.drawPath(path, paint);

            // 如果是 Edit 模式，绘制点
            if (isEditMode) {
                drawPoint(canvas, startPoint1);
                drawPoint(canvas, controlPoint1);
                drawPoint(canvas, controlPoint2);
                drawPoint(canvas, endPoint1);
            }
        }
    }

    private void drawDynamicGuideLine(Canvas canvas) {
        if (guideLine == null || guideLine.points.size() != 20) return;

        // 设置画笔的基本样式
        paint.setStyle(Paint.Style.STROKE); // 设置画笔为描边模式
        paint.setStrokeWidth(5); // 设置线条宽度
        paint.setColor(Color.parseColor("#FF9600"));

        // 绘制曲线：1-4
        for (int i = 0; i < 4; i += 4) {
            Point startPoint = guideLine.points.get(i);
            Point controlPoint1 = guideLine.points.get(i + 1);
            Point controlPoint2 = guideLine.points.get(i + 2);
            Point endPoint = guideLine.points.get(i + 3);

            Path path = new Path();
            path.moveTo(startPoint.x, startPoint.y);
            path.cubicTo(controlPoint1.x, controlPoint1.y, controlPoint2.x, controlPoint2.y, endPoint.x, endPoint.y);
            canvas.drawPath(path, paint);

            if (isEditMode) {
                drawPoint(canvas, startPoint);
                drawPoint(canvas, controlPoint1);
                drawPoint(canvas, controlPoint2);
                drawPoint(canvas, endPoint);
            }
        }

        // 绘制直线：5-6
        Point startPoint = guideLine.points.get(4);
        Point endPoint = guideLine.points.get(5);
        canvas.drawLine(startPoint.x, startPoint.y, endPoint.x, endPoint.y, paint);

        if (isEditMode) {
            drawPoint(canvas, startPoint);
            drawPoint(canvas, endPoint);
        }

        // 绘制直线：7-8
        startPoint = guideLine.points.get(6);
        endPoint = guideLine.points.get(7);
        canvas.drawLine(startPoint.x, startPoint.y, endPoint.x, endPoint.y, paint);

        if (isEditMode) {
            drawPoint(canvas, startPoint);
            drawPoint(canvas, endPoint);
        }

        // 绘制直线：9-10
        startPoint = guideLine.points.get(8);
        endPoint = guideLine.points.get(9);
        canvas.drawLine(startPoint.x, startPoint.y, endPoint.x, endPoint.y, paint);

        if (isEditMode) {
            drawPoint(canvas, startPoint);
            drawPoint(canvas, endPoint);
        }

        // 绘制虚线：11-12
        startPoint = guideLine.points.get(10);
        endPoint = guideLine.points.get(11);
        paint.setPathEffect(new DashPathEffect(new float[]{10, 10}, 0)); // 设置虚线效果
        canvas.drawLine(startPoint.x, startPoint.y, endPoint.x, endPoint.y, paint);

        if (isEditMode) {
            drawPoint(canvas, startPoint);
            drawPoint(canvas, endPoint);
        }
        paint.setPathEffect(null); // 恢复为实线效果

        // 绘制直线：13-14
        startPoint = guideLine.points.get(12);
        endPoint = guideLine.points.get(13);
        canvas.drawLine(startPoint.x, startPoint.y, endPoint.x, endPoint.y, paint);

        if (isEditMode) {
            drawPoint(canvas, startPoint);
            drawPoint(canvas, endPoint);
        }

        // 绘制直线：15-16
        startPoint = guideLine.points.get(14);
        endPoint = guideLine.points.get(15);
        canvas.drawLine(startPoint.x, startPoint.y, endPoint.x, endPoint.y, paint);

        if (isEditMode) {
            drawPoint(canvas, startPoint);
            drawPoint(canvas, endPoint);
        }

        // 绘制曲线：17-20
        for (int i = 16; i < 20; i += 4) {
            Point startPoint1 = guideLine.points.get(i);
            Point controlPoint1 = guideLine.points.get(i + 1);
            Point controlPoint2 = guideLine.points.get(i + 2);
            Point endPoint1 = guideLine.points.get(i + 3);

            Path path = new Path();
            path.moveTo(startPoint1.x, startPoint1.y);
            path.cubicTo(controlPoint1.x, controlPoint1.y, controlPoint2.x, controlPoint2.y, endPoint1.x, endPoint1.y);
            canvas.drawPath(path, paint);

            if (isEditMode) {
                drawPoint(canvas, startPoint1);
                drawPoint(canvas, controlPoint1);
                drawPoint(canvas, controlPoint2);
                drawPoint(canvas, endPoint1);
            }
        }
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
//                paint.setColor(Color.RED); // 在编辑模式下使用红色
//                paint.setStyle(Paint.Style.FILL);
//                canvas.drawCircle(startPoint.x, startPoint.y, 5, paint); // 绘制起点
//                canvas.drawCircle(endPoint.x, endPoint.y, 5, paint); // 绘制终点
                drawPoint(canvas, startPoint);
                drawPoint(canvas, endPoint);
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

//        testTopView();
    }

    private void testTopView() {
        setEditMode(true);
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

    private void testStatic() {
        setEditMode(true);
        List<Point> testPoints = new java.util.ArrayList<>();
        testPoints.add(new Point(200, 400));
        testPoints.add(new Point(220, 300));
        testPoints.add(new Point(240, 200));
        testPoints.add(new Point(260, 100));

        testPoints.add(new Point(280, 100));
        testPoints.add(new Point(880, 100));

        testPoints.add(new Point(260, 200));
        testPoints.add(new Point(900, 200));

        testPoints.add(new Point(240, 300));
        testPoints.add(new Point(480, 300));
        testPoints.add(new Point(680, 300));
        testPoints.add(new Point(920, 300));

        testPoints.add(new Point(230, 350));
        testPoints.add(new Point(480, 380));
        testPoints.add(new Point(680, 380));
        testPoints.add(new Point(930, 350));

        testPoints.add(new Point(990, 400));
        testPoints.add(new Point(970, 300));
        testPoints.add(new Point(950, 200));
        testPoints.add(new Point(930, 100));
        guideLine = new GuideLine(testPoints, GuideLineType.STATIC);
    }

    private void testDynamic() {
        setEditMode(true);
        List<Point> testPoints = new java.util.ArrayList<>();
        testPoints.add(new Point(200, 400));
        testPoints.add(new Point(220, 300));
        testPoints.add(new Point(240, 200));
        testPoints.add(new Point(260, 100));

        testPoints.add(new Point(300, 100));
        testPoints.add(new Point(900, 100));

        testPoints.add(new Point(260, 150));
        testPoints.add(new Point(300, 150));

        testPoints.add(new Point(240, 250));
        testPoints.add(new Point(280, 250));

        testPoints.add(new Point(280, 300));
        testPoints.add(new Point(920, 300));

        testPoints.add(new Point(900, 250));
        testPoints.add(new Point(940, 250));

        testPoints.add(new Point(880, 150));
        testPoints.add(new Point(920, 150));

        testPoints.add(new Point(990, 400));
        testPoints.add(new Point(970, 300));
        testPoints.add(new Point(950, 200));
        testPoints.add(new Point(930, 100));
        guideLine = new GuideLine(testPoints, GuideLineType.DYNAMIC);
    }
}
