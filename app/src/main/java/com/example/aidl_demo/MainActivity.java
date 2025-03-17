package com.example.aidl_demo;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.aidl_demo.src.CalculatorService;

public class MainActivity extends AppCompatActivity {

    private IMyAidlInterface mService; // Interface AIDL
    private boolean mBound = false; // Flag para verificar se o serviço está vinculado

    private EditText editTextNumber1, editTextNumber2;
    private Button buttonCalculate;
    private TextView textViewResult;

    // Conexão com o serviço
    private ServiceConnection mConnection = new ServiceConnection(){
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // Converte o IBinder para a interface AIDL
            mService = IMyAidlInterface.Stub.asInterface(service);
            mBound = true;

            // Teste: chama o método add do serviço
            try {
                int result = mService.add(2, 3);
                Log.d("AIDL_DEMO", "Resultado da soma: " + result);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Inicializa os componentes da UI
        editTextNumber1 = findViewById(R.id.editTextNumber1);
        editTextNumber2 = findViewById(R.id.editTextNumber2);
        buttonCalculate = findViewById(R.id.buttonCalculate);
        textViewResult = findViewById(R.id.textViewResult);

        // Configura o clique do botão
        buttonCalculate.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                if (mBound && mService != null) {
                    try {
                        // Pega os valores dos EditText
                        int number1 = Integer.parseInt(editTextNumber1.getText().toString());
                        int number2 = Integer.parseInt(editTextNumber2.getText().toString());

                        // Chama o método add do serviço
                        int result = mService.add(number1, number2);

                        // Exibe o resultado no TextView
                        textViewResult.setText("Resultado: " + result);
                    } catch (RemoteException e) {
                        //throw new RuntimeException(e);
                        e.printStackTrace();
                        Toast.makeText(MainActivity.this, "Erro ao chamar o serviço", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Serviço não vinculado", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Inicia o bind ao serviço
        Intent intent = new Intent(this, CalculatorService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Desvincula o serviço para evitar vazamentos de memória
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }
}