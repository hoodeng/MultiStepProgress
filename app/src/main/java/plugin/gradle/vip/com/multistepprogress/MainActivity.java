package plugin.gradle.vip.com.multistepprogress;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.vip.multi.progress.MultiStepProgress;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    MultiStepProgress progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progress = (MultiStepProgress) findViewById(R.id.progress);
        findViewById(R.id.next).setOnClickListener(this);
        findViewById(R.id.add).setOnClickListener(this);
        findViewById(R.id.remove).setOnClickListener(this);
        findViewById(R.id.pre).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.add) {
            progress.add();
        } else if (v.getId() == R.id.next) {
            progress.next();
        } else if (v.getId() == R.id.pre) {
            progress.pre();
        } else if (v.getId() == R.id.remove) {
            progress.remove();
        }
    }
}
