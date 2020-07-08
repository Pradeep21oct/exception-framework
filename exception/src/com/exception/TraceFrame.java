package com.exception;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TraceFrame implements Serializable {

    private final TraceNumber traceNumber;
    private final StackTraceElement stackTraceElement;
    private final List<Snapshot> snapshots;

    public TraceFrame(TraceNumber traceNumber, StackTraceElement stackTraceElement) {
        if(traceNumber==null){
            throw new IllegalArgumentException("Trace Number==null");
        }
        if(stackTraceElement==null){
            throw new IllegalArgumentException("Stack trace Element==null");
        }
        this.traceNumber=traceNumber;
        this.stackTraceElement=stackTraceElement;
        this.snapshots=new ArrayList<>();
    }

    public TraceNumber getTraceNumber() {
        return traceNumber;
    }

    public void addSnapshot(Snapshot snapshot) {
        if(snapshot==null) return;
        snapshots.add(snapshot);

    }

    public StackTraceElement getStackTraceElement() {
        return stackTraceElement;
    }

    public List<Snapshot> getSnapshots() {
        return snapshots;
    }
}
