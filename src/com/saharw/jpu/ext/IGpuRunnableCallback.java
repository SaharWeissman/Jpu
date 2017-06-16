package com.saharw.jpu.ext;

import com.saharw.jpu.core.GpuRunnable;

/**
 * Created by Sahar on 06/16/2017.
 */
public interface IGpuRunnableCallback {
    void onExecutionStarted(GpuRunnable r);
    void onExecutionCompleted(GpuRunnable r);
    void onExecutionError(Throwable t);
}
