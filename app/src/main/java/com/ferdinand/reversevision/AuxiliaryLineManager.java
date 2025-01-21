package com.ferdinand.reversevision;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AuxiliaryLineManager {
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

                // 按逗号分割字段
                String[] parts = line.split(",", 3);
                if (parts.length < 3) {
                    continue;
                }

                String type = parts[0];
                AuxiliaryLineView.GuideLineType guidelineType = AuxiliaryLineView.GuideLineType.STATIC;
                switch (type) {
                    case "TOP": guidelineType = AuxiliaryLineView.GuideLineType.TOP;break;
                    case "STATIC": guidelineType = AuxiliaryLineView.GuideLineType.STATIC;break;
                    case "DYNAMIC": guidelineType = AuxiliaryLineView.GuideLineType.DYNAMIC;break;
                }
                int angle = Integer.parseInt(parts[1]);
                String pointsString = parts[2];

                // 解析点数据
                List<AuxiliaryLineView.Point> points = new ArrayList<>();
                String[] pointPairs = pointsString.split(";");
                for (String pointPair : pointPairs) {
                    pointPair = pointPair.replace("(", "").replace(")", ""); // 去掉括号
                    String[] coords = pointPair.split(",");
                    if (coords.length == 2) {
                        int x = Integer.parseInt(coords[0].trim());
                        int y = Integer.parseInt(coords[1].trim());
                        points.add(new AuxiliaryLineView.Point(x, y));
                    }
                }

                // 创建引导线对象
                guidelines.add(new AuxiliaryLineView.GuideLine(points, guidelineType, angle));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return guidelines;
    }
}
