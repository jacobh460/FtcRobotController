package org.firstinspires.ftc.teamcode.modules;

public class InverseKinematics {
    final static double liftAngle = Math.toRadians(60.0);


    //TODO set these to more accurate values
    final static double L2 = 7;
    final static double L3 = 4;

    final static double r1 = L2 + L3;

    final static double L1min = 5;
    final static double L1max = 13;


    public static KinematicsParameters doInverseKinematics(double targetX, double targetY) {
        KinematicsParameters ret = new KinematicsParameters();
        double L1;
        double thetaA;
        double thetaB;

        //calculate lift length
        {
            final double r0 = Math.sqrt(targetX * targetX + targetY * targetY);

            final double theta0 = Math.atan2(targetY, targetX);
            final double theta2 = Math.PI - liftAngle - theta0;
            final double theta3 = Math.PI - Math.asin(r0 * (Math.sin(theta2) / r1));
            L1 = Math.max(Math.min(r1 * Math.sin(Math.PI - theta2 - theta3) / Math.sin(theta2), L1max), L1min);
        }

        final double liftEndX = -(Math.cos(liftAngle) * L1);
        final double liftEndY = Math.sin(liftAngle) * L1;


        //calculate joint angles
        final double d = Math.max(Math.min(Math.sqrt(Math.pow(targetX - liftEndX, 2) + Math.pow(targetY - liftEndY, 2)), r1), L2 - L3);


        thetaA = (targetX < liftEndX ? -1 : 1) * Math.acos((Math.pow(L2, 2) + Math.pow(d, 2) - Math.pow(L3, 2)) / (2 * L2 * d)) + Math.atan2(targetY - liftEndY, targetX - liftEndX);
        final double theta3 = (targetX < liftEndX ? -1 : 1) * Math.acos((Math.pow(L3, 2) + Math.pow(L2, 2) - Math.pow(d, 2)) / (2 * L3 * L2));
        thetaB = -Math.PI + thetaA + theta3;

        ret.thetaA = thetaA;
        ret.thetaB = thetaB;
        ret.liftHeight = L1;
        return ret;
    }
}
