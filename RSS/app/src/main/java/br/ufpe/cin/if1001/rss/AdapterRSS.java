package br.ufpe.cin.if1001.rss;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

//Criação de um novo adapter usando o layout 'itemlista.xml' (para mostrar título e data)
public class AdapterRSS extends BaseAdapter {

    private final List<ItemRSS> itemRSSList;
    private final Activity activity;

    public AdapterRSS(Activity activity, List<ItemRSS> itemRSSList) {
        this.activity = activity;
        this.itemRSSList = itemRSSList;
    }

    @Override
    public int getCount() {
        return itemRSSList.size();
    }

    @Override
    public Object getItem(int i) {
        return itemRSSList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View newView = activity.getLayoutInflater().inflate(R.layout.itemlista, viewGroup, false);
        ItemRSS itemRSS = itemRSSList.get(i);

        //Recupera os elementos do layout
        TextView titulo = (TextView) newView.findViewById(R.id.item_titulo);
        TextView data = (TextView) newView.findViewById(R.id.item_data);

        //Atribui as informações do itemRSS
        titulo.setText(itemRSS.getTitle());
        data.setText(itemRSS.getPubDate());

        return newView;
    }
}
