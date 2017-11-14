package aroundme.mcproject.com.safetyapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by papa on 14-11-2017.
 */

public class MyListAdapter extends ArrayAdapter<SOSMessage> {

    private Context context;
    private ArrayList<SOSMessage> allMessages;

    private LayoutInflater mInflater;
    private boolean mNotifyOnChange = true;

    public MyListAdapter(Context context, ArrayList<SOSMessage> mMessages) {
        super(context, R.layout.listview_item);
        this.context = context;
        this.allMessages = new ArrayList<SOSMessage>(mMessages);
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return 1;//allPersons .size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        int type = getItemViewType(position);
        if (convertView == null) {
            holder = new ViewHolder();
            switch (type) {
                case 1:
                    convertView = mInflater.inflate(R.layout.listview_item,parent, false);
                    holder.contact = (TextView) convertView.findViewById(R.id.contact);
                    holder.message = (TextView) convertView.findViewById(R.id.message);
                    break;
            }
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.contact.setText(allMessages.get(position).username);
        holder.message.setText(allMessages.get(position).message);
        holder.pos = position;
        return convertView;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        mNotifyOnChange = true;
    }

    public void setNotifyOnChange(boolean notifyOnChange) {
        mNotifyOnChange = notifyOnChange;
    }


    //---------------static views for each row-----------//
    static class ViewHolder {

        TextView contact;
        TextView message;
        int pos; //to store the position of the item within the list
    }
}