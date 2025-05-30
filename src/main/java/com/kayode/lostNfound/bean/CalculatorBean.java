package com.kayode.lostNfound.bean;

import java.io.Serializable;

import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.inject.Inject;
import com.kayode.lostNfound.model.Calculator;;
@ViewScoped
@Named("cb")
public class CalculatorBean implements Serializable{
    private static final long serialVersionUID = 1L;
	@Inject
    Calculator calculator;
    private double x;
    private double y;
    private double result;
    public void test() {
    	System.out.println("here");
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getResult() {
        return result;
    }

    public void setResult(double result) {
        this.result = result;
    }

    public String add() {
    	System.out.println(x+y);
        result = calculator.add(x, y);
        return "success";
    }
}