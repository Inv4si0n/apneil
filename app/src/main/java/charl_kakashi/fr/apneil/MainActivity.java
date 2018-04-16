package charl_kakashi.fr.apneil;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int REQUEST_CODE_ENABLE_BLUETOOTH = 0;
    private final Handler handler = new Handler();
    private Runnable timer;
    private LineGraphSeries<DataPoint> series;

    private static Button btnConnect, btnDisconnect;

    public static BluetoothAdapter bluetoothAdapter;
    private ListView listView;
    private ArrayList<String> mDeviceList = new ArrayList<>();

    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private static MainActivity instance;

    public static MainActivity getInstance() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        instance = this;

/*        listView = findViewById(R.id.listView);

        listView.setClickable(true);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                try {
                    String selectedItem = listView.getItemAtPosition(position).toString();

                    String address = selectedItem.split("\n")[1];

                    Toast.makeText(MainActivity.this, "Appareil address : " + address,
                            Toast.LENGTH_SHORT).show();

                    devices = bluetoothAdapter.getBondedDevices();
                    for (BluetoothDevice device : devices) {
                        if (device.getAddress().equals(address)) {


                            boolean connect = new ConnectThread().connect(device, myUUID);
                            Toast.makeText(MainActivity.this, "State: " + connect,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }


            }
        });*/


        btnConnect = findViewById(R.id.connect);
        btnDisconnect = findViewById(R.id.disconnect);

        btnConnect.setOnClickListener(this);
        btnDisconnect.setOnClickListener(this);


        // Connexion bluetooth
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (bluetoothAdapter == null)
            Toast.makeText(MainActivity.this, "Votre appareil ne possède pas le bluetooth",
                    Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(MainActivity.this, "Bluetooth présent sur votre appareil",
                    Toast.LENGTH_SHORT).show();


        if (!bluetoothAdapter.isEnabled()) {
            Intent bluetoothReceiver = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(bluetoothReceiver, REQUEST_CODE_ENABLE_BLUETOOTH);
        }

        // Graphique

        GraphView graph = findViewById(R.id.graph);

        graph.setTitle("Apnée par nuit"); // Titre du graph
        graph.setTitleColor(Color.BLUE); // Couleur du titre du graph

        series = new LineGraphSeries<>(generateData());

        series.setColor(Color.RED); // Couleur de la courbe
        series.setDrawDataPoints(true); // Tracé les points
        series.setDataPointsRadius(10); // Radius points
        series.setThickness(2); // Epaisseur

        graph.addSeries(series);

    }


    String address = null, name = null;
    BluetoothSocket btSocket = null;
    Set<BluetoothDevice> pairedDevices;

    private void bluetooth_connect_device() throws IOException {
        try {
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            address = bluetoothAdapter.getAddress();
            pairedDevices = bluetoothAdapter.getBondedDevices();
            if (pairedDevices.size() > 0) {
                for (BluetoothDevice bt : pairedDevices) {
                    address = bt.getAddress().toString();
                    name = bt.getName().toString();
                    Toast.makeText(getApplicationContext(), "Connected", Toast.LENGTH_SHORT).show();

                }
            }

        } catch (Exception we) {
            Toast.makeText(MainActivity.this, "1: " + we.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();//get the mobile bluetooth device
        BluetoothDevice dispositivo = bluetoothAdapter.getRemoteDevice(address);//connects to the device's address and checks if it's available
        btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);//create a RFCOMM (SPP) connection
        btSocket.connect();
        try {
            Toast.makeText(MainActivity.this, "BT Name: " + name + "\nBT Address: " + address,
                    Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(MainActivity.this, "2: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        timer = new Runnable() {
            @Override
            public void run() {
                series.resetData(generateData());
                handler.postDelayed(this, 1500);
            }
        };
        handler.postDelayed(timer, 1500);

    }

    @Override
    public void onPause() {
        handler.removeCallbacks(timer);
        super.onPause();
    }

    Random rand = new Random();

    private DataPoint[] generateData() {
        int count = 30;
        DataPoint[] values = new DataPoint[count];
        for (int i = 0; i < count; i++) {
            double x = i;
            double f = rand.nextDouble() * 0.15 + 0.3;
            double y = rand.nextInt();
            DataPoint v = new DataPoint(x, y);
            values[i] = v;
        }
        return values;
    }

    private Set<BluetoothDevice> devices;

/*    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != REQUEST_CODE_ENABLE_BLUETOOTH)
            return;
        if (resultCode == RESULT_OK) {
            // L'utilisateur a activé le bluetooth

            devices = bluetoothAdapter.getBondedDevices();
            for (BluetoothDevice blueDevice : devices) {
                Toast.makeText(MainActivity.this, "Device = " + blueDevice.getName(), Toast.LENGTH_SHORT).show();
            }

        } else {
            // L'utilisateur n'a pas activé le bluetooth
        }
    }*/


    private final BroadcastReceiver bluetoothReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent
                        .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                mDeviceList.add(device.getName() + "\n" + device.getAddress());
                Log.i("BT", device.getName() + "\n" + device.getAddress());
                listView.setAdapter(new ArrayAdapter<>(context,
                        android.R.layout.simple_list_item_1, mDeviceList));
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bluetoothAdapter.cancelDiscovery();
        unregisterReceiver(bluetoothReceiver);
    }


    @SuppressLint("WrongConstant")
    @Override
    public void onClick(View v) {


        try {


            switch (v.getId()) {

                case R.id.connect:

                    try {
                        bluetooth_connect_device();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    break;

                case R.id.disconnect:

                    Toast toast2 = Toast.makeText(MainActivity.this, "Déconnexion",
                            Toast.LENGTH_SHORT);
                    toast2.show();

                    bluetoothAdapter.cancelDiscovery();

                    break;

                default:
                    break;
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


}
