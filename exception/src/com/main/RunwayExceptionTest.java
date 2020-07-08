package com.main;

import com.exception.RunwayException;

public class RunwayExceptionTest {

    public static void main(String[] args) {
        try {
            String tst=null;
           System.out.println("hello"+tst.length());
        }catch (RuntimeException e){
            RunwayException rw=RunwayException.of(e);
            rw.snap("Hellp","word");
            System.out.println("Error: "+rw);
        }
    }
}
