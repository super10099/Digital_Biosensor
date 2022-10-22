package com.example.myapplication.datasystem;

import static org.junit.Assert.assertEquals;

import android.graphics.Color;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class DataProcessorTest {

    @Test
    public void calculateRPointsTest() {
        ArrayList<Integer> sampleColors = new ArrayList<>();
        sampleColors.add(Color.rgb(255, 255, 255));
        sampleColors.add(Color.rgb(0, 0, 0));
        sampleColors.add(Color.rgb(50, 210, 192));
        sampleColors.add(Color.rgb(120, 90, 52));

        List<Double> rPointsAnswers = new ArrayList<Double>();
        rPointsAnswers.add((255d + 255d) / 255d);
        rPointsAnswers.add((0d + 0d) / 0d);
        rPointsAnswers.add((50d + 210d) / 192d);
        rPointsAnswers.add((120d + 90d) / 52d);

        DataProcessor dp = new DataProcessor();
        dp.addSampleColor(sampleColors.get(0));
        dp.addSampleColor(sampleColors.get(1));
        dp.addSampleColor(sampleColors.get(2));
        dp.addSampleColor(sampleColors.get(3));
        List<Double> rPoints = dp.calculateRPoints();
        assertEquals(rPointsAnswers.get(0), rPoints.get(0),0.01);
        assertEquals(rPointsAnswers.get(1), rPoints.get(1),0.01);
        assertEquals(rPointsAnswers.get(2), rPoints.get(2),0.01);
        assertEquals(rPointsAnswers.get(3), rPoints.get(3),0.01);
    }

    @Test
    public void getAvgRValueTest() {
        ArrayList<Integer> sampleColors = new ArrayList<>();
        sampleColors.add(Color.rgb(255, 255, 255));
        sampleColors.add(Color.rgb(0, 0, 0));
        sampleColors.add(Color.rgb(50, 210, 192));
        sampleColors.add(Color.rgb(120, 90, 52));

        double ans = (255 + 0 + 50 + 120) / 4d;

        DataProcessor dp = new DataProcessor();
        dp.addSampleColor(sampleColors.get(0));
        dp.addSampleColor(sampleColors.get(1));
        dp.addSampleColor(sampleColors.get(2));
        dp.addSampleColor(sampleColors.get(3));
        dp.start();
        assertEquals(ans, dp.getAvgRValue(),0.01);
    }

    @Test
    public void getAvgGValue() {
        ArrayList<Integer> sampleColors = new ArrayList<>();
        sampleColors.add(Color.rgb(255, 255, 255));
        sampleColors.add(Color.rgb(0, 0, 0));
        sampleColors.add(Color.rgb(50, 210, 192));
        sampleColors.add(Color.rgb(120, 90, 52));

        double ans = (255 + 0 + 210 + 90) / 4d;

        DataProcessor dp = new DataProcessor();
        dp.addSampleColor(sampleColors.get(0));
        dp.addSampleColor(sampleColors.get(1));
        dp.addSampleColor(sampleColors.get(2));
        dp.addSampleColor(sampleColors.get(3));
        dp.start();
        assertEquals(ans, dp.getAvgGValue(),0.01);
    }

    @Test
    public void getAvgBValueTest()
    {
        ArrayList<Integer> sampleColors = new ArrayList<>();
        sampleColors.add(Color.rgb(255, 255, 255));
        sampleColors.add(Color.rgb(0, 0, 0));
        sampleColors.add(Color.rgb(50, 210, 192));
        sampleColors.add(Color.rgb(120, 90, 52));

        double ans = (255 + 0 + 192 + 52) / 4d;

        DataProcessor dp = new DataProcessor();
        dp.addSampleColor(sampleColors.get(0));
        dp.addSampleColor(sampleColors.get(1));
        dp.addSampleColor(sampleColors.get(2));
        dp.addSampleColor(sampleColors.get(3));
        dp.start();
        assertEquals(ans, dp.getAvgBValue(),0.01);
    }

    @Test
    public void getAvgRPointTest()
    {
        ArrayList<Integer> sampleColors = new ArrayList<>();
        sampleColors.add(Color.rgb(255, 255, 255));
        sampleColors.add(Color.rgb(0, 0, 0));
        sampleColors.add(Color.rgb(50, 210, 192));
        sampleColors.add(Color.rgb(120, 90, 52));

        double c0 = (255 + 255) / 255d;
        double c1 = 0;
        double c2 = (50 + 210) / 192d;
        double c3 = (120 + 90) / 52d;
        double ans = (c0 + c1 + c2 + c3) / 4d;

        DataProcessor dp = new DataProcessor();
        dp.addSampleColor(sampleColors.get(0));
        dp.addSampleColor(sampleColors.get(1));
        dp.addSampleColor(sampleColors.get(2));
        dp.addSampleColor(sampleColors.get(3));
        dp.start();
        assertEquals(ans, dp.getAvgRPoint(),0.01);
    }

    @Test
    public void getRPointSTDTest()
    {

    }

    @Test
    public void getComparativeValueTest()
    {

    }
}
