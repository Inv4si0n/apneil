package charl_kakashi.fr.apneil;

import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private final Handler handler = new Handler();
    private Runnable timer;
    private LineGraphSeries<DataPoint> series;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GraphView graph = findViewById(R.id.graph);

        graph.setTitle("Titre"); // Titre du graph
        graph.setTitleColor(Color.BLUE); // Couleur du titre du graph

        series = new LineGraphSeries<>(generateData());

        series.setColor(Color.GREEN); // Couleur de la courbe
        series.setDrawDataPoints(true); // Tracé les points
        series.setDataPointsRadius(10); // Radius points
        series.setThickness(2); // Epaisseur

        graph.addSeries(series);

        Button button = findViewById(R.id.on);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                System.out.println("ta mere la chauve");
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        timer = new Runnable() {
            @Override
            public void run() {
                series.resetData(generateData());
                handler.postDelayed(this, 3000);
            }
        };
        handler.postDelayed(timer, 3000);

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
            double y = Math.sin(i * f + 2) + rand.nextDouble() * 0.3;
            DataPoint v = new DataPoint(x, y);
            values[i] = v;
        }
        return values;
    }

}
