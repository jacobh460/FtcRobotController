package org.firstinspires.ftc.teamcode.Manipulator;

import org.firstinspires.ftc.teamcode.modules.Lift;

public class InverseKinematics {

    private final static double L2 = 7.5;
    private final static double L3 = 6.1;
    private final static double r1 = L2 + L3;
    private final static double liftAngle = Math.toRadians(122.3);//measured using phone angle app

    public static ManipulatorConfiguration doInverseKinematics(Vector2 position){
        double r0 = Math.sqrt(position.x * position.x + position.y * position.y);
        double theta0 = Math.atan2(position.y, position.x);
        double theta2 = InverseKinematics.liftAngle - theta0;
        double theta3 = Math.PI - Math.asin(r0 * (Math.sin(theta2) / InverseKinematics.r1));
        double L1 = Utils.clamp(InverseKinematics.r1 * Math.sin(Math.PI - theta2 - theta3) / Math.sin(theta2), Lift.minLength, Lift.maxLength);

        if (L1 != L1){
            L1 = Utils.clamp(position.rotate(-InverseKinematics.liftAngle + Math.PI/2).y, Lift.minLength, Lift.maxLength);
        }

        return InverseKinematics.doInverseKinematics_L1(position, L1);
    }

    public static ManipulatorConfiguration doInverseKinematics_L1(Vector2 position, double L1){
        ManipulatorConfiguration config = new ManipulatorConfiguration(L1, 0, 0);

        Vector2 liftEnd = new Vector2(
                Math.cos(InverseKinematics.liftAngle) * L1,
                Math.sin(InverseKinematics.liftAngle) * L1
        );

        double d = Utils.clamp(position.dist(liftEnd), InverseKinematics.L2 - InverseKinematics.L3, InverseKinematics.r1);
        double mult = position.x < liftEnd.x ? -1 : 1;
        config.thetaA = mult * Utils.acos((Math.pow(InverseKinematics.L2, 2) + Math.pow(d, 2) - Math.pow(InverseKinematics.L3, 2)) / (2 * InverseKinematics.L2 * d)) + Math.atan2(position.y - liftEnd.y, position.x - liftEnd.x);
        double theta3 = mult * Utils.acos((Math.pow(InverseKinematics.L3, 2) + Math.pow(InverseKinematics.L2, 2) - Math.pow(d, 2)) / (2 * InverseKinematics.L3 * InverseKinematics.L2));
        config.thetaB = - Math.PI + theta3;

        return config;
    }


    public static ManipulatorConfiguration doInverseKinematics_thetab(Vector2 position, double thetaB){
        ManipulatorConfiguration config = new ManipulatorConfiguration(0, 0, thetaB);

        Vector2 A = position.sub(
                new Vector2(
                        InverseKinematics.L3 * Math.cos(thetaB),
                        InverseKinematics.L3 * Math.sin(thetaB)
                )
        );

        double theta2 = Math.atan2(A.y, A.x);
        double theta3 = InverseKinematics.liftAngle - theta2;
        double sin_theta3 = Math.sin(theta3);
        double d = Math.sqrt(A.x*A.x + A.y*A.y);
        double theta1 = Math.PI - Math.asin(d * sin_theta3 / InverseKinematics.L2);
        config.thetaA = theta1 - Math.PI + theta3 + theta2;
        config.L1 = Utils.clamp(InverseKinematics.L2 * Math.sin(Math.PI - theta3 - theta1) / sin_theta3, Lift.minLength, Lift.maxLength);


        config.thetaB -= config.thetaA;//thetaB is relative to thetaA because it is at the end of joint a's arm
        return config;
    }

}
