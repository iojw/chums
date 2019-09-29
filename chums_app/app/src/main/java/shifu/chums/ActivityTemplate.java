package shifu.chums;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Isaac on 8/9/2016.
 */
public class ActivityTemplate {
    public String title;
    public String desc;
    public String venue;
    public String time;
    public String creator_name;
    public String creator;
    public String obj_id;
    public int going;
    public boolean is_creator=false;

    public ActivityTemplate(String title,String time,String venue,String desc,String creator_name,String creator,String obj_id,int going){
        this.title=title;
        this.time=time;
        this.venue=venue;
        this.desc=desc;
        this.creator_name=creator_name;
        this.creator=creator;
        this.obj_id=obj_id;
        this.going=going;
    }

    public void isCreator(){
        this.is_creator=true;
    }
}
