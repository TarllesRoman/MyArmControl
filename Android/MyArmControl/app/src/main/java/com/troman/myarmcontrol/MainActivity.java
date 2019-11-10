package com.troman.myarmcontrol;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    ConnectionThread connect;
    static Context context;

    static int PMOTOR_1 = 10;
    static int PMOTOR_2 = 10;
    static int PMOTOR_3 = 10;
    static int PMOTOR_4 = 5;

    private SharedPreferences settings;
    private SharedPreferences.Editor edit;

    static String MAC_BT = "";

    private Intent enableBtIntent;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;

        settings = getSharedPreferences("MAControl", MODE_PRIVATE);
        edit = settings.edit();

        MAC_BT = settings.getString("MAC_BT","");

        BluetoothAdapter btAdapter =BluetoothAdapter.getDefaultAdapter();
        if(btAdapter == null){
            Toast.makeText(this,"BT não funcionando", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this,"BT FUNFAAA", Toast.LENGTH_SHORT).show();
        }
        //Verifica se o bluetooth está ligado, se não estiver abre um alerta pedindo a ativação.
        enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        if (!btAdapter.isEnabled()) {
            startActivityForResult(enableBtIntent, 19);
        }else{
            onActivityResult(19,RESULT_OK, enableBtIntent);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        connect.cancel();
    }

    @SuppressLint("HandlerLeak")
    public static Handler handler = new Handler() {
        @Override
        public void handleMessage(@org.jetbrains.annotations.NotNull Message msg) {
            /* Esse método é invocado na Activity principal sempre que a thread de
             conexão Bluetooth recebe uma mensagem. */
            Bundle bundle = msg.getData();
            byte[] data = bundle.getByteArray("data");
            String dataString= new String(data);

            /* Aqui ocorre a decisão de ação, baseada na string recebida. Caso a string
             corresponda à uma das mensagens de status de conexão (iniciadas com --),
             atualizamos o status da conexão conforme o código. */
            if(dataString.equals("---N"))
                Toast.makeText(context  , "Ocorreu um erro durante a conexao",
                                                                        Toast.LENGTH_SHORT).show();
            else if(dataString.equals("---S"))
                Toast.makeText(context  , "Conectado", Toast.LENGTH_SHORT).show();
            else {
                /* Se a mensagem não for um código de status, então ela deve ser tratada
                 pelo aplicativo como uma mensagem vinda diretamente do outro lado da conexão.
                 Nesse caso, simplesmente atualizamos o valor contido no TextView do contador.*/
                //counterMessage.setText(dataString);
                Toast.makeText(context  , dataString, Toast.LENGTH_SHORT).show();
                Log.e("MSG", dataString);
            }

        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Encerra o aplicativo caso o usuário não tenha ligado o Bluetooth.
        if(resultCode == RESULT_CANCELED) finish();

        if(MAC_BT.isEmpty()) {
            BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
            //Buscando uma lista de dispositívos pareados com o celular
            Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();
            if (pairedDevices.size() > 0) {
                for (BluetoothDevice device : pairedDevices) {
                    String deviceName = device.getName();
                    String deviceHardwareAddress = device.getAddress(); // MAC address
                    //Para quando encontrar dispositivo com nome HC-06
                    if (deviceName.equals("HC-06")) {
                        MAC_BT = deviceHardwareAddress;
                        break;
                    }
                }
            } else {
                Toast.makeText(this, "Nenhum dispositivo pareado", Toast.LENGTH_SHORT).show();
            }
        }

        if(MAC_BT.isEmpty()){
            Toast.makeText(this,"Pareie seu MyArm e reinicie o aplicativo!",
                                                                        Toast.LENGTH_SHORT).show();
            return;
        }
        connect = new ConnectionThread(MAC_BT);
        connect.start();
        try { Thread.sleep(100); } catch (InterruptedException e) { e.printStackTrace();  }

    }// onActivityResult

    public void onclick(View view){
        try {
            switch (view.getId()) {
                case R.id.btLeftUp:
                    connect.write(String.format("11#%d", PMOTOR_2).getBytes());
                    break;
                case R.id.btLeftRight:
                    connect.write(String.format("12#%d", PMOTOR_1).getBytes());
                    break;
                case R.id.btLeftDown:
                    connect.write(String.format("13#%d", PMOTOR_2).getBytes());
                    break;
                case R.id.btLeftLeft:
                    connect.write(String.format("14#%d", PMOTOR_1).getBytes());
                    break;


                case R.id.btRightUp:
                    connect.write(String.format("21#%d", PMOTOR_3).getBytes());
                    break;
                case R.id.btRightRight:
                    connect.write(String.format("22#%d", PMOTOR_4).getBytes());
                    break;
                case R.id.btRightDown:
                    connect.write(String.format("23#%d", PMOTOR_3).getBytes());
                    break;
                case R.id.btRoghtLeft:
                    connect.write(String.format("24#%d", PMOTOR_4).getBytes());
                    break;

            }
            durma(250);
        }catch (IOException e){
            connect.cancel();
            durma(1000);
            onActivityResult(19,RESULT_OK, enableBtIntent);
        }

    }

    public void onclickConf(View view){
        Intent i = new Intent(this, ActivityConfig.class);
        //i.putExtra("score", jogadas);
        startActivity(i);
    }

    public void onclickReset(View view){
        try {
            connect.write(String.format("30#0").getBytes());
            durma(1000);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void durma(int millis){
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
