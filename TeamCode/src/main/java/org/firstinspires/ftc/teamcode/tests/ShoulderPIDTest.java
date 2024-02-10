package org.firstinspires.ftc.teamcode.tests;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.modules.ServoWrapper;
import org.firstinspires.ftc.teamcode.modules.ShoulderController;


@TeleOp(group = "test")
@Config
public class ShoulderPIDTest extends LinearOpMode {

    public static double target = 180.0;
    @Override
    public void runOpMode(){

        ShoulderController controller = new ShoulderController(this.hardwareMap);
        ServoWrapper wrist = new ServoWrapper(hardwareMap, "wrist", Math.toRadians(300.0), Math.toRadians(155.56536759), 98.142681/90.0);
        wrist.setAngle(0.0);

        this.telemetry = new MultipleTelemetry(this.telemetry, FtcDashboard.getInstance().getTelemetry());
        waitForStart();

        double lastTime = getRuntime();
        while (opModeIsActive()){
            double newTime = getRuntime();
            double deltaTime = newTime - lastTime;
            lastTime = newTime;


            this.telemetry.addData("output power", controller.doPID(Math.toRadians(target), deltaTime));

            this.telemetry.addData("Target", this.target);
            this.telemetry.addData("Position", controller.readAngle());
            this.telemetry.addData("Error", target - controller.readAngle());
            this.telemetry.update();
        }
    }
}
