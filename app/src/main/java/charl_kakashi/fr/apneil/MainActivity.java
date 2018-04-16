package charl_kakashi.fr.apneil;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_ENABLE_BLUETOOTH = 0;
    private final Handler handler = new Handler();
    private Runnable timer;
    private LineGraphSeries<DataPoint> series;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Connexion bluetooth

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null)
            Toast.makeText(MainActivity.this, "Votre appareil ne possède pas le bluetooth",
                    Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(MainActivity.this, "Bluetooth présent sur votre appareil",
                    Toast.LENGTH_SHORT).show();


        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBlueTooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBlueTooth, REQUEST_CODE_ENABLE_BLUETOOTH);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != REQUEST_CODE_ENABLE_BLUETOOTH)
            return;
        if (resultCode == RESULT_OK) {
            // L'utilisateur a activé le bluetooth
        } else {
            // L'utilisateur n'a pas activé le bluetooth
        }
    }


}
