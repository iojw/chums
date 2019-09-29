package shifu.chums;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import shifu.chums.CallAPI;

import java.util.HashMap;

public class newUser extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Create a new account");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.submit) {
            //i.putExtra
            TextView name = (TextView) findViewById(R.id.name);
            TextView class_ = (TextView) findViewById(R.id.class_);
            TextView school = (TextView) findViewById(R.id.school);
            String uid = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
            HashMap<String, String> hm = new HashMap<>(4);
            hm.put("uid", uid);
            hm.put("name",name.getText().toString());
            hm.put("class",class_.getText().toString());
            hm.put("school",school.getText().toString());
            CallAPI addUser = new CallAPI(add_c,"http://chums-iojw.rhcloud.com/adduser",hm);
            addUser.execute();
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_newuser, menu);
        return true;
    }

    final Callback add_c = new Callback() {
        @Override
        public void onResult(String result) {
            finish();
        }
    };
}
