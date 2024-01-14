package org.firstinspires.ftc.teamcode.TestVision;


import android.util.Size;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.vision.VisionPortal;


@Autonomous(group = "test")
public class TestVisionOpMode extends LinearOpMode {

    private VisionPortal portal;
    private TestVisionProcessor processor;

    @Override
    public void runOpMode() throws InterruptedException {
        processor = new TestVisionProcessor();

        portal = new VisionPortal.Builder()
                .setCamera(hardwareMap.get(WebcamName.class, "Webcam 1"))
                .setCameraResolution(new Size(640, 480))
                .setStreamFormat(VisionPortal.StreamFormat.YUY2)
                .addProcessor(processor)
                .build();

        waitForStart();


    }
}
