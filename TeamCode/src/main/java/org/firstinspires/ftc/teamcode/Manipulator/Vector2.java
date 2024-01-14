package org.firstinspires.ftc.teamcode.Manipulator;

import java.util.Locale;

public class Vector2 {
    public double x;
    public double y;
    public Vector2(double x, double y){
        this.x = x;
        this.y = y;
    }

    public Vector2(Vector2 toCopy){
        this.x = toCopy.x;
        this.y = toCopy.y;
    }

    @Override
    public String toString(){
        return String.format(Locale.ENGLISH, "(%f, %f)", this.x, this.y);
    }

    public Vector2 add(Vector2 other){
        return new Vector2(this.x + other.x, this.y + other.y);
    }

    public Vector2 add(double other){
        return new Vector2(this.x + other, this.y + other);
    }

    public Vector2 sub(Vector2 other){
        return new Vector2(this.x - other.x, this.y - other.y);
    }

    public Vector2 sub(double other){
        return new Vector2(this.x - other, this.y - other);
    }

    public Vector2 mult(double scalar){
        return new Vector2(this.x * scalar, this.y * scalar);
    }

    public Vector2 div(double scalar){
        return new Vector2(this.x / scalar, this.y / scalar);
    }

    public double dot(Vector2 other){
        return this.x * other.x + this.y * other.y;
    }

    public Vector2 copy(){
        return new Vector2(this);
    }

    public double dist(Vector2 other){
        return Math.sqrt(this.dist_nosqrt(other));
    }

    public double dist_nosqrt(Vector2 other){
        return Math.pow(this.x - other.x, 2) + Math.pow(this.y - other.y, 2);
    }

    public Vector2 rotate(double theta){
        return new Vector2(
                this.x * Math.cos(theta) - this.y * Math.sin(theta),
                this.x * Math.sin(theta) + this.y * Math.cos(theta)
        );
    }
}
