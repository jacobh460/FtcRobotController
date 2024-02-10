package org.firstinspires.ftc.teamcode.tests;


import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;

import org.firstinspires.ftc.teamcode.modules.ActionScheduler;
import org.firstinspires.ftc.teamcode.modules.AirplaneHolder;

@TeleOp
public class ActionSchedulerTest extends LinearOpMode {


    boolean isLaunching = false;
    ActionScheduler scheduler;

    AirplaneHolder holder;

    CRServo launcher;
    @Override
    public void runOpMode(){
        holder = new AirplaneHolder(this.hardwareMap, "airplaneHolder");
        scheduler = new ActionScheduler();

        this.launcher = hardwareMap.get(CRServo.class, "launcher");
        this.launcher.setPower(0);


        waitForStart();

        this.holder.close();

        while (opModeIsActive()){
            if (this.gamepad1.right_bumper && !this.isLaunching){
                this.holder.open();
                this.isLaunching = true;
                this.scheduler.addAction(
                        () -> {
                            this.launcher.setPower(-.1);
                            this.scheduler.addAction(() -> {
                                this.launcher.setPower(0);
                                this.scheduler.addAction(() -> {
                                    this.holder.close();
                                    this.isLaunching = false;
                                }, 200);
                            }, 500);
                        },
                        1000
                );
            }
            scheduler.tick();
        }
    }
}
