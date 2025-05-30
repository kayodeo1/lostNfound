package com.kayode.lostNfound.model;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;

@Stateless
public class Calculator{
	
	@PostConstruct
	public void init()
	{
		System.out.println("invoked ");
	}
    public double add(double x, double y) {
    	System.out.println(x+y);
        return x + y;
    }
}