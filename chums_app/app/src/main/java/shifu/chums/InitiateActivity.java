package shifu.chums;

import android.app.Activity;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

public class InitiateActivity extends AppCompatActivity {

    Activity context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initiate);
        setTitle("Initiate an activity!");
        context=this;

        final Callback c = new Callback() {
            @Override
            public void onResult(String result) {
                Toast.makeText(context,"Submitted", Toast.LENGTH_LONG).show();
                finish();
            }
        };

        Button submit = (Button) findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView title=(TextView) findViewById(R.id.title);
                TextView venue=(TextView) findViewById(R.id.venue);
                TextView time=(TextView) findViewById(R.id.time);
                TextView desc=(TextView) findViewById(R.id.desc);

                if (title.getText().toString().equals("") || venue.getText().toString().equals("") || time.getText().toString().equals("")){
                    Toast.makeText(context,"Please fill in the required fields.", Toast.LENGTH_LONG).show();
                }
                else {
                    String uid = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
                    HashMap<String, String> hm = new HashMap<>(5);
                    hm.put("creator", uid);
                    hm.put("title", title.getText().toString());
                    hm.put("venue", venue.getText().toString());
                    hm.put("time", time.getText().toString());
                    hm.put("desc", desc.getText().toString());
                    //hm.put("creator_name","John");
                    CallAPI initiate = new CallAPI(c, "http://chums-iojw.rhcloud.com/initiate", hm);
                    initiate.execute();
                }
            }
        });
    }
}
