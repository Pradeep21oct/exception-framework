package com.exception;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 *   Runway exception is unchecked  runtime exception raised in situations not
 *   properly handled like application  bug.
 *
 *   It is a runtime exception so there is no need to declare as a buggy throwing runtime exception
 *
 *   We Still can catch it if we want to add some debug information and not to rethrow other exception wrap in other exception
 *
 *
 */
public class RunwayException extends RuntimeException{

    private static final int MAX_STACK_TRACE_SIZE=32;
    private static final int BUFFER_SIZE=512;

   private final String className;
   private final String causeExceptionName;
   private final String getCauseExceptionMessage;

    /**
     * Thread Id for this runway exception \
     * Note: Runway Exception  created on different  threads have different stack trace.
     */

   private long threadId;


   private final List<TraceFrame> stackFrame;

   private final UUID errorGrid=UUID.randomUUID();

   public RunwayException(){
       this.className=this.getClass().getName();
       this.causeExceptionName=null;
       this.getCauseExceptionMessage=null;
       this.threadId=Thread.currentThread().getId();
       this.stackFrame=getStackFrames(this.getStackTrace());
   }


    public RunwayException(String message){
       super(message);
        this.className=this.getClass().getName();

        this.causeExceptionName=null;
        this.getCauseExceptionMessage=null;
        this.threadId=Thread.currentThread().getId();
        this.stackFrame=getStackFrames(this.getStackTrace());
    }

    public RunwayException(Throwable throwable){

        this.className=this.getClass().getName();
        this.threadId=Thread.currentThread().getId();
        if(throwable==null) {
            this.causeExceptionName = null;
            this.getCauseExceptionMessage = null;
            this.stackFrame = Collections.emptyList();
            return;
        }

        this.causeExceptionName = throwable.getClass().getName();
        this.getCauseExceptionMessage = throwable.getMessage();
        this.stackFrame=getStackFrames(throwable.getStackTrace());
    }



    public RunwayException(RunwayException another){

       super("[Cause bug: "+another.toString()+ "]");

        this.className=this.getClass().getName();
        this.threadId=Thread.currentThread().getId();
        this.causeExceptionName = another.causeExceptionName;
        this.getCauseExceptionMessage = another.getCauseExceptionMessage;
        this.stackFrame=getStackFrames(this.getStackTrace());
        TraceNumber appTraceNumer=TraceNumber.determineCurrentFrame(this.className);

        if(!appTraceNumer.isUndefind()){
            while (!stackFrame.isEmpty()){
                if(stackFrame.get(0).getTraceNumber().equals(appTraceNumer)){
                    break;
                }// if
                stackFrame.remove(0);
            }// while
        }// if
    }




    public long getThreadId() {
        return threadId;
    }

    public void setThreadId(long threadId) {
        this.threadId = threadId;
    }

    /**
     *  add one snapshot to current stack trace frame.
     * @param name name of variable
     * @param object
     * @param <T> type of param
     */
    public <T> void  snap(String name, T object){
       if(stackFrame.isEmpty()) return;

       String value=null;
       if(object!=null){
           value=object.toString();
       }
       Snapshot snapshot=new Snapshot(name,value);
       TraceNumber frameNumber=TraceNumber.determineCurrentFrame(this.className);
       if(frameNumber.isUndefind()){
           stackFrame.get(0).addSnapshot(snapshot);
           return;
       }

       boolean found =false;
       for (TraceFrame frame:stackFrame){
           if(frame.getTraceNumber().equals(frameNumber)){
               frame.addSnapshot(snapshot);
               found=true;
               break;
           }
       }

       if(!found){
           stackFrame.get(0).addSnapshot(snapshot);
       }
    }

    /**
     * add one line of debug info
     * @param name
     * @param value
     */
    public  void snap(String name, int value){
        snap(name,Integer.toString(value));
    }

    /**
     * add one line of debug info
     * @param name
     * @param value
     */
    public  void snap(String name, long value){
        snap(name,Long.toString(value));
    }

    /**
     * add one line of debug info
     * @param name
     * @param value
     */
    public  void snap(String name, boolean value){
        snap(name,Boolean.toString(value));
    }

    public static RunwayException of(final Throwable throwable){
        if(throwable==null) return  new RunwayException();
        if(throwable instanceof RunwayException){
            long currentThreadId=Thread.currentThread().getId();
            RunwayException sourceBug=(RunwayException)throwable;
            if(currentThreadId==sourceBug.threadId){
                return sourceBug;
            }
            return new RunwayException(sourceBug);
        }
        return new RunwayException(throwable);
    }

    private  static List<TraceFrame> getStackFrames(StackTraceElement[] stackTrace) {
        if(stackTrace==null) throw new IllegalArgumentException("Source Stack is null");
        int stackLength=stackTrace.length;
        if(stackLength==0) return Collections.emptyList();

        List<TraceFrame> stackFrame=new ArrayList<>(stackLength);
        int stackFrameNumber=stackLength;
        for (StackTraceElement traceElement:stackTrace){
            TraceNumber frameNumber=new TraceNumber(--stackFrameNumber);
            TraceFrame frame=new TraceFrame(frameNumber,traceElement);
            stackFrame.add(frame);
        }
       return stackFrame;
    }
@Override
    public String getMessage(){
        StringBuilder builder;
        try{
            builder=new StringBuilder(BUFFER_SIZE);
            builder.append("[ ").append(getTechSupportCode()).append("]");
            builder.append("Thread Id : ").append(threadId).append(".");
            if(super.getMessage()!=null){
                builder.append(super.getMessage()).append(".");
            }
            if(causeExceptionName!=null){
                builder.append("Cause: ").append(causeExceptionName).append(". ");
                builder.append(getCauseExceptionMessage).append(". ");
            }
            return builder.toString();
        }catch (RuntimeException re){
            return "failed to stringify message . Msg "+re.toString();
        }
}


    @Override
    public String toString(){
        StringBuilder builder;
        try{
            builder=new StringBuilder(BUFFER_SIZE);
           builder.append(this.getClass().getName()).append(": ").append(getMessage()).append("\n");
            builder.append(frameToString());

            return builder.toString();
        }catch (RuntimeException re){
            return "failed to stringify exception . Msg "+re.toString();
        }
    }

    private String frameToString() {
        int traceLength=stackFrame.size();
        if(traceLength==0) return "";

        int printLimit=(traceLength<MAX_STACK_TRACE_SIZE)?traceLength:MAX_STACK_TRACE_SIZE;
        List<TraceFrame> printStackFrames=stackFrame.subList(0,printLimit);

        StringBuilder buff=new StringBuilder(BUFFER_SIZE);
        for (TraceFrame frame:printStackFrames){
            //reverse stack frame index
            buff.append("@ ");
            buff.append(frame.getTraceNumber().getValue());
            buff.append(":");

            StackTraceElement trace=frame.getStackTraceElement();
            buff.append(trace.getClassName());
            buff.append(".");
            buff.append(trace.getMethodName());
            buff.append("[");
            buff.append(trace.getLineNumber());
            buff.append("]");

            List<Snapshot> snapshots=frame.getSnapshots();

            if(!snapshots.isEmpty()){
                buff.append(": ");
                for (Snapshot snapshot:snapshots){
                    buff.append("[");
                    buff.append(snapshot.getName());
                    buff.append("=");
                    buff.append(snapshot.getValue());
                    buff.append("]");
                }
            }
            buff.append("\n");
        }
        return buff.toString();
    }

    private int getTechSupportCode() {
        int hashCode=errorGrid.hashCode();
        if(hashCode==Integer.MAX_VALUE){
            hashCode=Integer.MAX_VALUE;
        }
        return Math.abs(hashCode);
    }

    public List<TraceFrame> getStackFrame(){
        return stackFrame;
    }
}
