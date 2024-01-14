package org.firstinspires.ftc.teamcode.Manipulator.tests;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Manipulator.ManipulatorController;
import org.firstinspires.ftc.teamcode.Manipulator.Spline.ParametricNaturalCubicSpline;
import org.firstinspires.ftc.teamcode.Manipulator.Spline.Point;
import org.firstinspires.ftc.teamcode.Manipulator.Spline.SplineFollower;

@TeleOp(group = "test")
public class ArmSplineTest extends LinearOpMode {

    @Override
    public void runOpMode(){
        ParametricNaturalCubicSpline spline = new ParametricNaturalCubicSpline(new Point[]{
                new Point(5, 0, 0, Double.NaN, Double.NaN),
                new Point(5.362750771604938, 8.71818201303155, 2, Double.NaN, Double.NaN),
                new Point(8.718335619570187, 9.254686785550984, 4, Double.NaN, Double.NaN),
        });

        ManipulatorController controller = new ManipulatorController(this.hardwareMap);
        SplineFollower follower = new SplineFollower(controller);

        waitForStart();
        follower.followSpline(spline, false, 0.25, 0.25);
        while (opModeIsActive()){
            follower.tick();
        }

    }
}
