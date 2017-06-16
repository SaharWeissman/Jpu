package com.saharw.jpu.examples;

import com.saharw.jpu.Jpu;
import com.saharw.jpu.core.GpuRunnable;
import com.saharw.jpu.ext.IGpuRunnableCallback;
import com.saharw.jpu.io.JpuInputStream;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Scanner;

public class CmdExample {

    private final static String WELCOME_MSG = "Welcome to Jpu Command line interface!\nplease select one of the options below:\n";
    private final static String[] MENU_OPTIONS_ARRAY = new String[]{"File copy test"};
    private final static String ERR_MSG_INVALID_INPUT_OPTION = "Please enter a valid option!";
    private static Scanner sScanner;
    private static int mOptionIdx;
    private static final int FILE_COPY_INDEX = 0;
    private static final int BUFF_SIZE_BYTES = 1024 * 4;

    private static final int ERR_CODE_OK = 0;

    public static void main(String[] args) {
        printMenu();
        waitForInput();
        onUserOptionSelected(mOptionIdx);
    }

    private static void onUserOptionSelected(int mOptionIdx) {
        switch (mOptionIdx){
            case FILE_COPY_INDEX:{
                handleFileCopy();
                break;
            }
            default: {
                break;
            }
        }
    }

    private static void handleFileCopy() {
        boolean isSourcePathValid = false;
        boolean isTargetPathValid = false;
        BufferedOutputStream bos = null;
        while(!isSourcePathValid){
            System.out.println("Enter full path to source file and press enter: ");
            try{
                final File source = new File(sScanner.nextLine());
                File target = null;
                if(!source.exists() || !source.canRead()){
                    throw new IllegalArgumentException("source file does not exist or is not readable!");
                }
                isSourcePathValid = true;
                while(!isTargetPathValid) {
                    System.out.println("set target file output path and press enter: ");
                    target = new File(sScanner.nextLine());
                    if (target.getParentFile().mkdirs() || target.getParentFile().exists()) {
                        if (target.createNewFile() || target.exists()) {
                            if (target.canWrite()) {
                                System.out.println("preparing for copy from:\"" + source.getAbsolutePath() + "\" to \"" + target.getAbsolutePath() + "\"");
                            } else {
                                throw new IOException("unable to create or write target file(2): " + target.getAbsolutePath());
                            }
                        } else {
                            throw new IOException("unable to create or write target file(0): " + target.getAbsolutePath());
                        }
                    } else {
                        throw new IOException("unable to create or write target file(1): " + target.getAbsolutePath());
                    }
                    bos = new BufferedOutputStream(new FileOutputStream(target));
                    isTargetPathValid = true;
                }
                if(bos != null) {
                    byte[] buff = new byte[BUFF_SIZE_BYTES];
                    final File finalTarget = target;
                    final long[] startTimeMillis = new long[1];
                    final long[] endTimeMillis = new long[1];
                    JpuInputStream jpuCopyRunnable = new JpuInputStream(source, new IGpuRunnableCallback() {
                        @Override
                        public void onExecutionStarted(GpuRunnable r) {
                            startTimeMillis[0] = System.currentTimeMillis();
                            System.out.println("Copy from \"" + source.getAbsolutePath() + "\" started at " + startTimeMillis[0]);
                        }

                        @Override
                        public void onExecutionCompleted(GpuRunnable r) {
                            endTimeMillis[0] = System.currentTimeMillis();
                            System.out.println("Copy to \"" + finalTarget.getAbsolutePath() + "\" ended at " + endTimeMillis[0]);
                            long totalTimeMillis = endTimeMillis[0] - startTimeMillis[0];
                            System.out.println("total time = " + totalTimeMillis + "ms");
                            System.exit(ERR_CODE_OK);
                        }

                        @Override
                        public void onExecutionError(Throwable t) {
                            System.err.println("Copy Failed!\n" + t);
                        }
                    }, bos, buff);
                    Jpu.getInstance().execute(jpuCopyRunnable);
                }
            }catch (NullPointerException e){
                System.err.println("one ore more of the paths is invalid(0)! make sure path is correct and file exists!\n" + e);
            }catch (IllegalArgumentException e){
                System.err.println("source file does not exist!\n" + e);
            }catch (IOException e){
                System.err.println("target file cannot be created or written!\n" + e);
            }catch (Exception e){
                System.err.println("one or more of the paths is invalid(1)! make sure path is correct and file exists!\n" + e);
            }
        }
    }

    private static void waitForInput() {
        sScanner = new Scanner(System.in);
        while(!isInputOptionValid()){
            System.err.println(ERR_MSG_INVALID_INPUT_OPTION);
        }
    }

    private static boolean isInputOptionValid() {
        boolean isValid = false;
        try{
            mOptionIdx = Integer.parseInt(sScanner.nextLine());
            isValid = ((0 <= mOptionIdx) && (mOptionIdx <= MENU_OPTIONS_ARRAY.length - 1));
        }catch (NumberFormatException e){
            isValid = false;
        }catch (Exception e){
            isValid = false;
        }
        return isValid;
    }

    private static void printMenu() {
        System.out.println(WELCOME_MSG);
        for(int i = 0 ; i < MENU_OPTIONS_ARRAY.length; i++){
            System.out.println(i + ". " + MENU_OPTIONS_ARRAY[i]);
        }
    }

    private static class ConsoleGpuRunnableCallback implements IGpuRunnableCallback {

        long startTime, endTime;

        @Override
        public void onExecutionStarted(GpuRunnable r) {
            startTime = System.currentTimeMillis();
            System.out.println("onExecutionStarted: startTime = " + startTime);
        }

        @Override
        public void onExecutionCompleted(GpuRunnable r) {
            endTime = System.currentTimeMillis();
            System.out.println("onExecutionCompleted: startTime = " + endTime);
        }

        @Override
        public void onExecutionError(Throwable tr) {
            System.err.println("onExecutionError: " + tr);
        }
    }
}
