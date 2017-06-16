package com.saharw.jpu;

import com.saharw.jpu.core.GpuExecutor;
import com.saharw.jpu.core.GpuRunnable;
import com.saharw.jpu.ext.IGpuRunnableCallback;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by Sahar on 06/16/2017.
 */
public class Jpu {
    private static volatile Jpu sInstance = null;
    private final GpuExecutor mExecutor;
    private final int CORE_POOL_SIZE = 2;
    private final int MAX_POOL_SIZE = 3;
    private final long KEEP_ALIVE_TIME = 100;
    private final TimeUnit KEEP_ALIVE_TIME_UNIT = TimeUnit.MILLISECONDS;
    private final LinkedBlockingQueue<Runnable> mWorkQueue;


    private Jpu(){
        mWorkQueue = new LinkedBlockingQueue<Runnable>();
        this.mExecutor = new GpuExecutor(CORE_POOL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE_TIME, KEEP_ALIVE_TIME_UNIT, mWorkQueue);
    }

    public static Jpu getInstance(){
        if(sInstance == null){
            synchronized (Jpu.class){
                if(sInstance == null){
                    sInstance = new Jpu();
                }
            }
        }
        return sInstance;
    }

    public void execute(GpuRunnable runnable){
        this.mExecutor.execute(runnable);
    }
}
