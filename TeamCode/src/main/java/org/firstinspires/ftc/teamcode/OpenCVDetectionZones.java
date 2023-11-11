package org.firstinspires.ftc.teamcode;

import org.opencv.core.Rect;

public class OpenCVDetectionZones {
    public static class Data{
        public static Rect[] zones = new Rect[]{
                new Rect(10, 10, 50, 100),
                new Rect(60, 10, 50, 100),
                new Rect(110, 10, 50, 100)
        };
    }
}
