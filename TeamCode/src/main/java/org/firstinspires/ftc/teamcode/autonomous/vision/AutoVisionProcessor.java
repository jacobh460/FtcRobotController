package org.firstinspires.ftc.teamcode.autonomous.vision;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

import org.firstinspires.ftc.robotcore.internal.camera.calibration.CameraCalibration;
import org.firstinspires.ftc.vision.VisionProcessor;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

public class AutoVisionProcessor implements VisionProcessor {

    private Scalar lowThresh;
    private Scalar highThresh;

    private Rect[] zones;


    private Mat HSVFrame = new Mat();
    private Mat ThresholdMask = new Mat();
    private Mat extraThreshMask = new Mat();

    public int zone = -1;

    private Bitmap previewBitmap;

    private double detectionThreshold;

    public AutoVisionProcessor(double[] targetColorRGBA, Rect[] zones, double hRange, double sMin, double vMin, double sMax, double vMax, double detectionThreshold){
        this.zones = zones;
        this.detectionThreshold = detectionThreshold;

        Mat targetColorRGBAMAT = new Mat(1, 1, CvType.CV_8UC4);
        Mat targetColorHSVMAT = new Mat(1, 1, CvType.CV_8UC3);
        targetColorRGBAMAT.put(0, 0, targetColorRGBA);
        //convert target color from rgba to hsv
        Imgproc.cvtColor(targetColorRGBAMAT, targetColorHSVMAT, Imgproc.COLOR_RGB2HSV);

        //set thresholds using hue
        double[] targetHSV = targetColorHSVMAT.get(0, 0);
        lowThresh = new Scalar(targetHSV[0] - hRange, sMin, vMin);
        highThresh = new Scalar(targetHSV[0] + hRange, sMax, vMax);
    }
    @Override
    public void init(int width, int height, CameraCalibration calibration){
        this.previewBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
    }

    @Override
    public Object processFrame(Mat frame, long captureTimeNanos){
        //convert to hsv
        Imgproc.cvtColor(frame, this.HSVFrame, Imgproc.COLOR_RGB2HSV);
        //color threshold
        Core.inRange(this.HSVFrame, this.lowThresh, this.highThresh, this.ThresholdMask);

        Core.inRange(this.HSVFrame, new Scalar(this.lowThresh.val[0] + 180.0, this.lowThresh.val[1], this.lowThresh.val[2]), new Scalar(this.highThresh.val[0] + 180.0, this.highThresh.val[1], this.highThresh.val[2]), this.extraThreshMask);

        Core.add(this.extraThreshMask, this.ThresholdMask, this.ThresholdMask);

        Core.inRange(this.HSVFrame, new Scalar(this.lowThresh.val[0] - 180.0, this.lowThresh.val[1], this.lowThresh.val[2]), new Scalar(this.highThresh.val[0] - 180.0, this.highThresh.val[1], this.highThresh.val[2]), this.extraThreshMask);

        Core.add(this.extraThreshMask, this.ThresholdMask, this.ThresholdMask);

        int maxZoneIndex = -1;
        int maxCount = -1;
        //iterate over every zone and determine which one has the most pixels that land within the above color threshold
        for (int i = 0; i < this.zones.length; ++i){
            Mat zoneMat = this.ThresholdMask.submat(this.zones[i]);
            int count = Core.countNonZero(zoneMat);
            //if more than detectionThreshold*100 percent of the pixels are not within the threshold, ignore this zone
            if (count > maxCount && (double)count/(double)(zoneMat.rows() * zoneMat.cols()) > this.detectionThreshold){
                maxCount = count;
                maxZoneIndex = i;
            }
        }

        this.zone = maxZoneIndex;
        return null;
    }

    @Override
    public void onDrawFrame(Canvas canvas, int onscreenWidth, int onscreenHeight, float scaleBmpPxToCanvasPx, float scaleCanvasDensity, Object userContext){
        //draw zones
        for (int i = 0; i < this.zones.length; ++i){
            float x = (float)this.zones[i].x * scaleBmpPxToCanvasPx;
            float y = (float)this.zones[i].y * scaleBmpPxToCanvasPx;

            Paint paint = new Paint();
            if (i == this.zone) paint.setARGB(255, 0, 255, 0);//green means that is the detected zone
            else paint.setARGB(255, 255, 0, 0); //red means otherwise
            paint.setStrokeWidth(20);
            paint.setStyle(Paint.Style.STROKE);
            canvas.drawRect(x, y, x + (float)this.zones[i].width * scaleBmpPxToCanvasPx, y + (float)this.zones[i].height * scaleBmpPxToCanvasPx, paint);
        }


        //convert mask to a bitmap so it can be drawn
        Utils.matToBitmap(this.ThresholdMask, this.previewBitmap);
        //draw threshold mask in top left corner of stream
        canvas.drawBitmap(this.previewBitmap,
                null,
                new android.graphics.Rect(0, 0, onscreenWidth/3, onscreenHeight/3),
                null);

    }


}
