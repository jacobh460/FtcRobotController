package org.firstinspires.ftc.teamcode.tests;

import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.openftc.easyopencv.OpenCvPipeline;

public class testOpenCVPipeline extends OpenCvPipeline {

    public Mat conversion = new Mat();

    public boolean drawRects = true;

    public int zoneIndex = 0;

    double scaleFactor = 1;
    Rect[] rects;
    testOpenCVPipeline(Rect[] rects){
        this.rects = rects;
    }
    @Override
    public Mat processFrame(Mat input)
    {
        int closestIndex = 0;
        long closestVal = Long.MAX_VALUE;


        Double[] targetColor = new Double[]{0.0, 0.0, 255.0};

        for (int i = 0; i < rects.length; ++i){
            long dist = 0;
            for (int x = rects[i].x; x < rects[i].x + rects[i].width && x < input.width(); ++x){
                for (int y = rects[i].y; y < rects[i].y + rects[i].height && y < input.height(); ++y){
                    double[] color = input.get(y, x);
                    for (int j = 0; j < targetColor.length; ++j){
                        dist += Math.abs(targetColor[j] - color[j]);
                    }
                }
            }
            if (dist < closestVal) {
                closestIndex = i;
                closestVal = dist;
            }
        }

        zoneIndex = closestIndex;

        if (drawRects){
            for (int i = 0; i < rects.length; ++i) {
                Scalar color = (i == closestIndex ? new Scalar(0, 255, 0) : new Scalar(255, 0, 0));
                Imgproc.rectangle(input, rects[i], color, 3);
                Imgproc.putText(input, Integer.toString(i + 1), rects[i].tl(), 1, 5, color, 5);
            }
        }
        return input;
    }
}
