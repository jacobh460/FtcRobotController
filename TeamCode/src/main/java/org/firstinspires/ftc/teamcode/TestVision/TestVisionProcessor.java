package org.firstinspires.ftc.teamcode.TestVision;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

import org.firstinspires.ftc.robotcore.internal.camera.calibration.CameraCalibration;
import org.firstinspires.ftc.vision.VisionProcessor;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

public class TestVisionProcessor implements VisionProcessor {
    Mat hsvMat = new Mat();
    Mat thresholdMat = new Mat();
    Bitmap testBitmap = Bitmap.createBitmap(640, 480, Bitmap.Config.ARGB_8888);

    Scalar low;
    Scalar high;

    public int detectedIndex = 0;

    @Override
    public void init(int width, int height, CameraCalibration calibration) {
        Mat blue = new Mat(1, 1, CvType.CV_8UC4);
        Mat out = new Mat(1, 1, CvType.CV_8UC3);
        double[] blueColor = {0, 0, 230, 255};
        blue.put(0, 0, blueColor);
        Imgproc.cvtColor(blue, out, Imgproc.COLOR_RGB2HSV);

        double[] hsvColor = out.get(0, 0);
        low = new Scalar(hsvColor[0] - 20, 100, 100);
        high = new Scalar(hsvColor[0] + 20, 255, 255);
    }

    @Override
    public Object processFrame(Mat frame, long captureTimeNanos) {

        Imgproc.cvtColor(frame, hsvMat, Imgproc.COLOR_RGB2HSV);
        Core.inRange(hsvMat, low, high, thresholdMat);

        return null;
    }

    @Override
    public void onDrawFrame(Canvas canvas, int onscreenWidth, int onscreenHeight, float scaleBmpPxToCanvasPx, float scaleCanvasDensity, Object userContext) {
        Utils.matToBitmap(thresholdMat, testBitmap);
        canvas.drawBitmap(testBitmap, null, new Rect(0, 0, onscreenWidth/3, onscreenHeight/3), null);
    }
}
