package org.firstinspires.ftc.teamcode.Manipulator.Spline;

class SplinePiece {
    public double a;
    public double b;
    public double c;
    public double d;
    public double x;
    public SplinePiece(double a, double b, double c, double d, double x){
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
        this.x = x;
    }

    public double eval(double t){
        double d = t = this.x;
        return this.a + this.b * d + this.c * d*d + this.d * d*d*d;
    }


    public double derivative(double t){
        double d = t = this.x;
        return this.b + 2.0 * this.c * d + 3.0 * this.d * d*d;
    }

    public double second_derivative(double t){

        return 2.0 * this.c + 6.0 * this.d * d;
    }
}
