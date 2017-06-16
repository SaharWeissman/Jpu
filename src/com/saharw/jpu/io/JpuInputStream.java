package com.saharw.jpu.io;

import com.saharw.jpu.core.GpuRunnable;
import com.saharw.jpu.ext.IGpuRunnableCallback;

import java.io.*;

/**
 * Created by Sahar on 06/16/2017.
 */
public class JpuInputStream extends GpuRunnable {

    private final OutputStream mOutputStream;
    private final byte[] mInBuff;
    private final File mSource;

    public JpuInputStream(File source, IGpuRunnableCallback callback, OutputStream os, byte[] inBuff) {
        super(callback);
        this.mSource = source;
        this.mOutputStream = os;
        this.mInBuff= inBuff;
    }

    @Override
    public void runOnGpu() throws Throwable {
        try(BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(mSource))){
            int byteRead = 0;
            long totalBytes = mSource.length();
            int actualBytesRead = 0;
            while((byteRead = (inputStream.read(mInBuff, 0, actualBytesRead = (int) Math.min(mInBuff.length, (totalBytes - byteRead))))) != -1){
                mOutputStream.write(mInBuff, 0 ,actualBytesRead);
            }
        }
    }
}
