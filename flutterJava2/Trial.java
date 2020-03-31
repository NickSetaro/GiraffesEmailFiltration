package com.example.mailapp;

public class Trial
{
    String test = "This is a test.";

    public Trial()
    {

    }

    public String getTest() {
        return test;
    }
    public String getTest(String par)
    {
        return test + " The parameter is: " + par;
    }
    public String parTest(String par1, String par2)
    {
        return "par1: " + par1 + " \npar2: " + par2;
    }
}
