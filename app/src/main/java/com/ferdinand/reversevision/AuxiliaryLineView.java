package com.ferdinand.reversevision;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class AuxiliaryLineView extends View {
    public static class Point {
        public float x, y;
        public boolean isSelected;

        public Point(float x, float y) {
            this.x = x;
            this.y = y;
            this.isSelected = false;
        }
    }

    public static class Line {
        List<Point> points;
        boolean isDashed; // 是否为虚线
        boolean isSelected;

        public Line(List<Point> points, boolean isDashed) {
            this.points = points;
            this.isDashed = isDashed;
            this.isSelected = false;
        }

        public void addPoint(Point point) {
            points.add(point);
        }
    }

    public enum GuideLineType {
        STATIC,      // 静态引导线
        DYNAMIC,     // 动态引导线
        TOP     // 俯视引导线
    }

    public static class GuideLine {
        public List<Point> points;     // 点集
        public GuideLineType type;    // 引导线类型
        public List<Line> lines;      // 线集

        public GuideLine(List<Point> points, GuideLineType type) {
            this.points = points;
            this.type = type;
            this.lines = new ArrayList<>();
            configureLinesBasedOnType();
        }

        // 根据引导线类型自动配置线
        private void configureLinesBasedOnType() {
            switch (this.type) {
                case STATIC:
                    configureLines(new int[]{5, 5, 2, 2, 2});
                    break;
                case DYNAMIC:
                    configureLines(new int[]{5, 5, 2, 2, 2, 2, 2, 2});
                    break;
                case TOP:
                    configureLines(new int[]{2, 2, 2, 2});
                    break;
            }
        }
        // 静态引导线的分组规则
        private void configureLines(int[] lineSizes) {
            int pointIndex = 0;
            for (int lineIndex = 0; lineIndex < lineSizes.length; lineIndex++) {
                List<Point> linePoints = new ArrayList<>();
                for (int i = 0; i < lineSizes[lineIndex] && pointIndex < points.size(); i++) {
                    linePoints.add(points.get(pointIndex++));
                }
                boolean isDashed = (lineIndex == 2);
                lines.add(new Line(linePoints, isDashed));
            }
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

    private void drawLine(Canvas canvas, Line line) {
        if (line == null || line.points == null || line.points.size() < 2) return;
        if (line.isDashed) {
            paint.setPathEffect(new DashPathEffect(new float[]{10, 10}, 0));
        } else {
            paint.setPathEffect(null);
        }
        Path path = new Path();
        path.moveTo(line.points.get(0).x, line.points.get(0).y);
        if (line.points.size() == 2) {
            path.lineTo(line.points.get(1).x, line.points.get(1).y);
        } else {
            addBezierToPath(path, line.points, 100);
        }
        canvas.drawPath(path, paint);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (guideLine == null) return;
        for (Line line : guideLine.lines) {
            drawLine(canvas, line);
            if (isEditMode) {
                for (Point point : line.points) {
                    drawPoint(canvas, point);
                }
            }
        }
    }

    // 绘制单个点（实心红色圆）
    private void drawPoint(Canvas canvas, Point point) {
        int OriginalColor = paint.getColor(); // 获取原始画笔颜色
        paint.setStyle(Paint.Style.FILL); // 设置画笔为填充模式
        paint.setColor(Color.RED);
        canvas.drawCircle(point.x, point.y, 5, paint);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(OriginalColor); // 恢复线条颜色
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
    }


    /* **************************贝塞尔曲线计算相关函数******************************** */
    private void addBezierToPath(Path path, List<Point> controlPoints, int steps) {
        List<Point> curvePoints = calculateCurve(controlPoints, steps);

        for (int i = 1; i < curvePoints.size(); i++) {
            path.lineTo(curvePoints.get(i).x, curvePoints.get(i).y);
        }
    }

    private List<Point> calculateCurve(List<Point> controlPoints, int steps) {
        List<Point> curvePoints = new ArrayList<>();

        for (int i = 0; i <= steps; i++) {
            double t = (double) i / steps;  // t 在 [0, 1] 之间变化
            curvePoints.add(calculatePoint(controlPoints, t));  // 计算曲线上的点
        }

        return curvePoints;
    }

    private Point calculatePoint(List<Point> controlPoints, double t) {
        int n = controlPoints.size() - 1;  // n次贝塞尔曲线
        Point point = new Point(0, 0);

        for (int i = 0; i <= n; i++) {
            int comb = combination(n, i);  // 组合数 C(n, i)
            double bernstein = comb * Math.pow(1 - t, n - i) * Math.pow(t, i);  // 伯恩斯坦基函数
            point.x += (float) (controlPoints.get(i).x * bernstein);
            point.y += (float) (controlPoints.get(i).y * bernstein);
        }

        return point;
    }

    private int combination(int n, int k) {
        if (k == 0 || k == n) {
            return 1;
        }
        return combination(n - 1, k - 1) + combination(n - 1, k);
    }

    /* **************************贝塞尔曲线计算******************************** */

}
