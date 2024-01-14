package org.firstinspires.ftc.teamcode.Manipulator;

import java.util.Locale;

public class ManipulatorConfiguration {
    public double L1;
    public double thetaA;
    public double thetaB;
    public ManipulatorConfiguration(double L1, double thetaA, double thetaB){
        this.L1 = L1;
        this.thetaA = thetaA;
        this.thetaB = thetaB;
    }

    @Override
    public String toString(){
        return String.format(Locale.ENGLISH, "L1: %f, thA: %f, thB: %f", this.L1, Math.toDegrees(this.thetaA), Math.toDegrees(this.thetaB));
    }


    /**
     *
     * @return false if any property is NaN, otherwise true
     */
    public boolean isValid(){
        return this.L1 == this.L1 && this.thetaA == this.thetaA && this.thetaB == this.thetaB;
    }
}
