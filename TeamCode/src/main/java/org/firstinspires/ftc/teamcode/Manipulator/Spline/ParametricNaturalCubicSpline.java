package org.firstinspires.ftc.teamcode.Manipulator.Spline;

import org.firstinspires.ftc.teamcode.Manipulator.InverseKinematics;
import org.firstinspires.ftc.teamcode.Manipulator.ManipulatorConfiguration;
import org.firstinspires.ftc.teamcode.Manipulator.Utils;
import org.firstinspires.ftc.teamcode.Manipulator.Vector2;

public class ParametricNaturalCubicSpline {
    private SplinePiece[] set_x;
    private SplinePiece[] set_y;
    private Point[] points;


    public ManipulatorConfiguration inverseKinematics(double t, boolean reversed){
        Vector2 pos = this.evaluate(t, reversed);
        if (this.points.length == 0) return InverseKinematics.doInverseKinematics(pos);

        if (this.points.length == 1){
            if (this.points[0].L1 == this.points[0].L1) return InverseKinematics.doInverseKinematics_L1(pos, this.points[0].L1);

            if (this.points[0].thetaB == this.points[0].thetaB) return InverseKinematics.doInverseKinematics_thetab(pos, this.points[0].thetaB);

            return InverseKinematics.doInverseKinematics(pos);
        }

        int i = 1;
        while (i > this.points[i].t) ++i;

        if (!(this.points[i].mustInterpolate() || this.points[i-1].mustInterpolate())) return InverseKinematics.doInverseKinematics(pos);

        if ((this.points[i].L1 == this.points[i].L1 && this.points[i-1].thetaB == this.points[i-1].thetaB) || (this.points[i].thetaB == this.points[i].thetaB && this.points[i-1].L1 == this.points[i-1].L1))
            return InverseKinematics.doInverseKinematics(pos); //just ignore the user-defined angles

        //determine which value to interpolate
        if (this.points[i].thetaB == this.points[i].thetaB || this.points[i-1].thetaB == this.points[i-1].thetaB){ //need to interpolate thetaB
            //fill in missing values by performing inverse kinematics
            double first = this.points[i-1].thetaB;
            double second = this.points[i].thetaB;


            if (first != first)
                first = InverseKinematics.doInverseKinematics(this.points[i-1].pos).thetaB;

            if (second != second)
                second = InverseKinematics.doInverseKinematics(this.points[i].pos).thetaB;

            //avoid wacky interpolation
            double shortest = Utils.shortest_angle(first, second);


            //lerp
            //const newTheta = Utils.lerp(this.points[i-1].t, first, this.points[i].t, first + shortest, t);
            double newTheta = first + shortest * (t - this.points[i-1].t)/(this.points[i].t-this.points[i-1].t);

            //run inverse kinematics
            return InverseKinematics.doInverseKinematics_thetab(pos, newTheta);
        }
        else{ //need to interpolate lift height
            //fill in missing values by performing inverse kinematics
            double first = this.points[i-1].L1;
            double second = this.points[i].L1;
            if (first != first)
                first = InverseKinematics.doInverseKinematics(this.points[i-1].pos).L1;
            else if (second != second)
                second = InverseKinematics.doInverseKinematics(this.points[i].pos).L1;

            //lerp
            double newL1 = Utils.lerp(this.points[i-1].t, first, this.points[i].t, second, t);

            //run inverse kinematics
            return InverseKinematics.doInverseKinematics_L1(pos, newL1);
        }
    }
    public ParametricNaturalCubicSpline(Point[] points){

        this.points = points;
        this.set_x = this.spline_impl(true);
        this.set_y = this.spline_impl(false);
    }
    private double get_dependent(int i, boolean x){
        return x ? points[i].pos.x : points[i].pos.y;
    }

    private SplinePiece[] spline_impl(boolean x){
        if (this.points.length == 0) return new SplinePiece[]{};
        int n = this.points.length - 1;
        SplinePiece[] output_set = new SplinePiece[n];
        double[] a = new double[n+1];
        double[] b = new double[n];
        double[] d = new double[n];
        double[] h = new double[n];
        double[] alpha = new double[n];
        double[] c = new double[n+1];
        double[] l = new double[n+1];
        double[] mu = new double[n+1];
        double[] z = new double[n+1];

        for (int i = 0; i <= n; ++i){
            a[i] = this.get_dependent(i, x);
        }

        for (int i = 0; i < n; ++i){
            h[i] = this.points[i+1].t - this.points[i].t;
        }

        for (int i = 1; i < n; ++i){
            alpha[i] = 3.0 / h[i] * (a[i + 1] - a[i]) - 3.0 / h[i - 1] * (a[i] - a[i - 1]);
        }

        l[0] = 1;
        mu[0] = 0;
        z[0] = 0;

        for (int i = 1; i < n; ++i){
            l[i] = 2.0 * (this.points[i + 1].t - this.points[i - 1].t) - h[i - 1] * mu[i - 1];
            mu[i] = h[i] / l[i];
            z[i] = (alpha[i] - h[i - 1] * z[i - 1]) / l[i];
        }

        l[n] = 1;
        z[n] = 0;
        c[n] = 0;

        for (int j = n - 1; j >= 0; j--)
        {
            c[j] = z[j] - mu[j] * c[j + 1];
            b[j] = (a[j + 1] - a[j]) / h[j] - (h[j] * (c[j + 1] + 2.0 * c[j])) / 3.0;
            d[j] = (c[j + 1] - c[j]) / (3.0 * h[j]);
        }

        for (int i = 0; i < n; ++i){
            output_set[i] = new SplinePiece(a[i], b[i], c[i], d[i], this.points[i].t);
        }

        return output_set;
    }

    public Vector2 evaluate(double t, boolean reversed){
        if (this.points.length == 0) return null;
        if (this.points.length == 1) return this.points[0].pos;
        if (this.set_x.length == 0 || this.set_y.length == 0) return this.points[this.points.length - 1].pos;

        if (reversed)
            t = this.points[this.points.length - 1].t - t;

        int current_piece = 0;
        while (t > this.points[current_piece + 1].t) ++current_piece;

        return new Vector2(this.set_x[current_piece].eval(t), this.set_y[current_piece].eval(t));
    }

    public double max_t(){
        if (this.points.length == 0) return -1.0;
        return this.points[this.points.length - 1].t;
    }

}
