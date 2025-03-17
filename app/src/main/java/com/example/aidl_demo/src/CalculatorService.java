package com.example.aidl_demo.src;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

import androidx.annotation.Nullable;

import com.example.aidl_demo.IMyAidlInterface;

public class CalculatorService extends Service {

    // Implementação do stub, o esqueleto gerado pela compilação do AIDL
    private final IMyAidlInterface.Stub mBinder = new IMyAidlInterface.Stub(){
        @Override
        public int add(int a, int b) throws RemoteException {
            return a + b;
        }
    };

    // Este método é chamado quando um cliente tenta conectar-se ao serviço
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
}
