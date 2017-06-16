package com.saharw.jpu.core;

import com.aparapi.Kernel;
import com.saharw.jpu.ext.IGpuRunnableCallback;

import java.util.concurrent.ExecutionException;

/**
 * Created by Sahar on 06/16/2017.
 */

public abstract class GpuRunnable extends Kernel implements Runnable{
    private final IGpuRunnableCallback mCallback;

    public abstract void runOnGpu() throws Throwable;

    public GpuRunnable(IGpuRunnableCallback callback){
        if(callback == null){throw new IllegalArgumentException("callback cannot be null!");}
        this.mCallback = callback;
    }

    @Override
    public void run() {
        this.mCallback.onExecutionStarted(this);
        try {
            runOnGpu();
            this.mCallback.onExecutionCompleted(this);
        }catch (Throwable t){
            this.mCallback.onExecutionError(t);
        }
    }
}
