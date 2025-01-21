package com.ferdinand.reversevision;


import android.util.Log;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AuxiliaryLineManager {
    private static final String TAG = AuxiliaryLineManager.class.getSimpleName();
    public static List<AuxiliaryLineView.GuideLine> readGuidelinesFromCSV(String filePath) {
        List<AuxiliaryLineView.GuideLine> guidelines = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean isFirstLine = true; // 跳过表头
            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                // 去除首尾空格并按逗号分割字段
                line = line.trim();
                String[] parts = line.split(",", 3);

                if (parts.length < 3) {
                    Log.w(TAG, "Skipping invalid line (not enough columns): " + line);
                    continue;
                }

                String type = parts[0].trim();
                AuxiliaryLineView.GuideLineType guidelineType;

                // 根据类型设置 GuideLineType
                switch (type) {
                    case "TOP":
                        guidelineType = AuxiliaryLineView.GuideLineType.TOP;
                        break;
                    case "STATIC":
                        guidelineType = AuxiliaryLineView.GuideLineType.STATIC;
                        break;
                    case "DYNAMIC":
                        guidelineType = AuxiliaryLineView.GuideLineType.DYNAMIC;
                        break;
                    default:
                        Log.w(TAG, "Unknown guideline type: " + type);
                        continue;
                }

                int angle = 0;
                try {
                    angle = Integer.parseInt(parts[1].trim());
                } catch (NumberFormatException e) {
                    Log.e(TAG, "Invalid angle format: " + parts[1], e);
                    continue;
                }

                String pointsString = parts[2].trim();

                // 解析点数据
                List<AuxiliaryLineView.Point> points = new ArrayList<>();
                String[] pointPairs = pointsString.split(";");
                for (String pointPair : pointPairs) {
                    pointPair = pointPair.trim().replace("(", "").replace(")", ""); // 去掉括号并修剪空格
                    String[] coords = pointPair.split(",");
                    if (coords.length == 2) {
                        try {
                            int x = Integer.parseInt(coords[0].trim());
                            int y = Integer.parseInt(coords[1].trim());
                            points.add(new AuxiliaryLineView.Point(x, y));
                        } catch (NumberFormatException e) {
                            Log.e(TAG, "Invalid point format: " + pointPair, e);
                            continue;
                        }
                    } else {
                        Log.w(TAG, "Skipping invalid point pair (not two coordinates): " + pointPair);
                    }
                }

                // 创建引导线对象
                guidelines.add(new AuxiliaryLineView.GuideLine(points, guidelineType, angle));
            }
        } catch (IOException e) {
            Log.e(TAG, "Error reading CSV file: " + filePath, e);
        }

        return guidelines;
    }
}
