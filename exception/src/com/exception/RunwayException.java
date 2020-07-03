package com.exception;

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

    private List<TraceFrame> getStackFrames(StackTraceElement[] stackTrace) {
       return null;
    }

}
