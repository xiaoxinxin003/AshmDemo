package com.okay.ashm;

import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.os.MemoryFile;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.os.SharedMemory;
import android.system.ErrnoException;
import android.util.Log;

import java.io.FileDescriptor;
import java.lang.reflect.Method;

public class MemoryFetchService extends Service {
    private static final String TAG = "MemoryFetchService";
    private static final String SHM_FILE_NAME = "test_memory";
    @Override
    public IBinder onBind(Intent intent) {
        return new MemoryFetchStub();
    }
    static class MemoryFetchStub extends IMemoryAidlInterface.Stub {
        @Override
        public ParcelFileDescriptor getParcelFileDescriptor() throws RemoteException {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O_MR1) {
                MemoryFile memoryFile = null;
                try {
                    memoryFile = new MemoryFile(SHM_FILE_NAME, 1024);
                    memoryFile.getOutputStream().write(new byte[]{1, 2, 3, 4, 5});
                    Method method = MemoryFile.class.getDeclaredMethod("getFileDescriptor");
                    FileDescriptor des = (FileDescriptor) method.invoke(memoryFile);
                    return ParcelFileDescriptor.dup(des);
                } catch (Exception e) {
                    Log.d(TAG, "getParcelFileDescriptor: exception : " + e.toString());
                }
            }else {
                try {
                    SharedMemory sharedMemory = SharedMemory.create(SHM_FILE_NAME, 1024);

                } catch (ErrnoException e) {
                    Log.d(TAG, "getParcelFileDescriptor: exception : " + e.toString());
                }
            }

            return null;
        }
    }
}
