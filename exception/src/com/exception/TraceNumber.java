package com.exception;

import java.io.Serializable;
import java.util.Objects;

public class TraceNumber implements Serializable {

private final int value;
private static final int VALUE_IUNDEFIND=-1;
private static final TraceNumber UNDEFIND=new TraceNumber(VALUE_IUNDEFIND);
    public TraceNumber(int value) {
        this.value=value;
    }

    public static TraceNumber determineCurrentFrame(String className) {
        StackTraceElement[] stack=Thread.currentThread().getStackTrace();
        int traceLength=stack.length;

        if(traceLength==0){
            return TraceNumber.UNDEFIND;
        }

        int currentFrame =traceLength;
         boolean foundClass=false;
         for (StackTraceElement frame:stack){
             --currentFrame;
             if(frame.getClassName().endsWith(className)){
                 foundClass=true;
                 continue;
             }
             if (!foundClass){
                 continue;
             }
             return new TraceNumber(currentFrame);
         }
        return TraceNumber.UNDEFIND;
    }

    public boolean isUndefind() {
        return this.value==VALUE_IUNDEFIND;
    }

    public int getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TraceNumber that = (TraceNumber) o;
        return value == that.value;
    }

    @Override
    public int hashCode() { return this.value; }
}
