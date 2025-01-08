package com.ferdinand.reversevision;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
        List<Integer> pointIndices;
        boolean isDashed;
        boolean isSelected;

        public Line(List<Integer> pointIndices, boolean isDashed) {
            this.pointIndices = pointIndices;
            this.isDashed = isDashed;
            this.isSelected = false;
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
                List<Integer> linePoints = new ArrayList<>();
                for (int i = 0; i < lineSizes[lineIndex] && pointIndex < points.size(); i++) {
                    linePoints.add(pointIndex++);
                }
                lines.add(new Line(linePoints, lineIndex == 2));
            }
        }
    }


    private Paint paint;
    private GuideLine guideLine;

    private boolean isEditMode = false;

    private Set<Integer> selectedPointsSet = new HashSet<>(); // 被选中的点的索引集合

    public void setEditMode(boolean editMode) {
        isEditMode = editMode;
    }

    public void setGuideLine(GuideLine guideLine) {
        this.guideLine = guideLine;
        invalidate();
    }

    private void drawLine(Canvas canvas, Line line) {
        if (line == null || line.pointIndices == null || line.pointIndices.size() < 2) return;
        if (line.isDashed) {
            paint.setPathEffect(new DashPathEffect(new float[]{10, 10}, 0));
        } else {
            paint.setPathEffect(null);
        }
        Path path = new Path();
        path.moveTo(guideLine.points.get(line.pointIndices.get(0)).x, guideLine.points.get(line.pointIndices.get(0)).y);
        if (line.pointIndices.size() == 2) {
            path.lineTo(guideLine.points.get(line.pointIndices.get(1)).x, guideLine.points.get(line.pointIndices.get(1)).y);
        } else {
            List<Point> controlPoints = new ArrayList<>();
            for (Integer index : line.pointIndices) {
                controlPoints.add(guideLine.points.get(index));
            }
            addBezierToPath(path, controlPoints, 100);
        }
        canvas.drawPath(path, paint);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (guideLine == null) return;
        if (this.guideLine.type == GuideLineType.DYNAMIC) {
            paint.setColor(Color.parseColor("#FF9600"));
        } else {
            paint.setColor(Color.YELLOW);
        }
        for (Line line : guideLine.lines) {
            drawLine(canvas, line);
            if (isEditMode) {
                for (int pointIndex : line.pointIndices) {
                    drawPoint(canvas, pointIndex);
                }
            }
        }
    }

    private void drawPoint(Canvas canvas, int pointIndex) {
        int OriginalColor = paint.getColor();
        paint.setStyle(Paint.Style.FILL);
        if (selectedPointsSet.contains(pointIndex)) paint.setColor(Color.RED);
        canvas.drawCircle(guideLine.points.get(pointIndex).x, guideLine.points.get(pointIndex).y, 5, paint);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(OriginalColor);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEditMode) return false;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                int selectedIndex = findTouchedPoint(event.getX(), event.getY());
                if (selectedIndex != -1) {
                    selectedPointsSet.add(selectedIndex);
                    invalidate();
                    return true; // 消费事件，因为点中了拖拽点
                }
                invalidate();
                break;

            case MotionEvent.ACTION_MOVE:
                if (!selectedPointsSet.isEmpty()) {
                    for (int index : selectedPointsSet) {
                        Point point = guideLine.points.get(index);
                        point.x = event.getX();
                        point.y = event.getY();
                        Log.d("TouchEvent", "ACTION_MOVE: point after move: (" + point.x + ", " + point.y + ")");
                    }
                    invalidate();
                    return true; // 消费事件
                }
                break;

            case MotionEvent.ACTION_UP:
                selectedPointsSet.clear();
                performClick();
                return true; // 消费事件
        }

        // 如果触摸点不在拖拽区域，返回 false，让事件继续传递
        return false;
    }


    @Override
    public boolean performClick() {
        return super.performClick();
    }

    private int findTouchedPoint(float touchX, float touchY) {
        final float touchRadius = 5; // 可触摸点的半径范围，增加触摸范围以便更容易选择点
        for (int i = 0; i < guideLine.points.size(); i++) {
            Point point = guideLine.points.get(i);
            float dx = touchX - point.x;
            float dy = touchY - point.y;
            if (Math.sqrt(dx * dx + dy * dy) <= touchRadius) {
                return i; // 返回点的索引
            }
        }
        return -1; // 没有触摸到任何点，返回 -1
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
        paint.setStrokeWidth(4f);
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
