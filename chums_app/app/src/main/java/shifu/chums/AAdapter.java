package shifu.chums;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Isaac on 8/9/2016.
 */
public class AAdapter extends ArrayAdapter<ActivityTemplate> {
    Activity activity;
    public AAdapter(Activity activity, ArrayList<ActivityTemplate> trans){
        super(activity,0,trans);
        this.activity=activity;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        ActivityTemplate t=getItem(position);
        if(convertView==null)
            convertView=activity.getLayoutInflater().inflate(R.layout.item_activity, parent, false);
        TextView title=(TextView)convertView.findViewById(R.id.title);
        TextView venue = (TextView) convertView.findViewById(R.id.venue);
        TextView time = (TextView) convertView.findViewById(R.id.time);
        TextView going = (TextView) convertView.findViewById(R.id.going);
        if (t.going==-1){
            title.setText("No activities currently.");
            venue.setText("");
            time.setText("");
            going.setText("");
        }
        else {
            if (t.is_creator)
                title.setText("You are looking for people to "+t.title);
            else
                title.setText(t.creator_name + " is looking for people to " + t.title);
            venue.setText(t.venue);
            time.setText(t.time);
            switch (t.going){
                case 0:going.setText("No one is currently attending");
                    break;

                case 1:going.setText("1 person is attending");
                    break;

                case -1:
                    break;

                default:going.setText(t.going+" people are attending");
                    break;
            }
        }
        return convertView;
    }

}
