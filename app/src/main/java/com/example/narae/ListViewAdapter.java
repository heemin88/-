package com.example.narae;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ListViewAdapter extends BaseAdapter {
    public ArrayList<ListViewItem> listViewItemList =new ArrayList<ListViewItem>();

    @Override
    public int getCount() {
        return listViewItemList.size();
    }

    @Override
    public Object getItem(int position) {
        return listViewItemList.get(position) ;
    }

    @Override
    public long getItemId(int position) {
        return position ;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.activity_list_view_adapter, parent, false);
        }

        // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
        TextView textView1 = (TextView) convertView.findViewById(R.id.textView7);
        TextView textView2 = (TextView) convertView.findViewById(R.id.textView8);
        TextView textView3 = (TextView) convertView.findViewById(R.id.textView9);
        // Data Set(filteredItemList)에서 position에 위치한 데이터 참조 획득
        ListViewItem listViewItem = listViewItemList.get(position);

        textView1.setText(listViewItem.getplace());
        textView2.setText(listViewItem.getintime());
        textView3.setText(listViewItem.getouttime());

        return convertView;
    }

    public void addItem(String place,String in, String out){
        ListViewItem item = new ListViewItem();
        item.setspace(place);
        item.setintime(in);
        item.setouttime(out);

        listViewItemList.add(item);
    }

    public void clearItem(){
        listViewItemList.clear();
    }
}