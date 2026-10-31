package com.skartivpn.app.util;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.skarti.premium.v2.R;
import java.io.InputStream;
import java.util.ArrayList;
import androidx.cardview.widget.CardView;
import android.view.animation.AnimationUtils;
import com.bumptech.glide.Glide;
public class PayloadAdapter extends BaseAdapter {

    public static ImageView im;
    Context context; 
    ArrayList<PayloadModel> arrayList;
    int nethie01;
	
    public PayloadAdapter(Context context, ArrayList<PayloadModel> arrayList, int nethie01) {
        this.context = context;
        this.arrayList = arrayList;
		this.nethie01 = nethie01;
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayList.get(position);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public  View getView(int position, View convertView, ViewGroup parent) {
        if (convertView ==  null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.spinner_item, parent, false);
        }
        TextView name, info;
		CardView mainCardz = convertView.findViewById(R.id.mainCardz);
        name = (TextView) convertView.findViewById(R.id.itemNamea);
        info = (TextView) convertView.findViewById(R.id.sinf);

        name.setText(arrayList.get(position).getServerName());
        info.setText(arrayList.get(position).getServerInfo());
		mainCardz.startAnimation(AnimationUtils.loadAnimation(context, R.anim.grow));
        try {
            getServerIcon(position, im);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return convertView;
    }
    private void getServerIcon(int position, ImageView im) throws Exception {
        String name = arrayList.get(position).getServerName().toLowerCase();
        if (name.contains("globe")) {
            neth(im, "globe");
        } else if (name.contains("smart")) {
            neth(im, "smart");
        } else if (name.contains("gtm")) {
            neth(im, "gtm");
        } else if (name.contains("tm")) {
            neth(im, "tm");
        } else if (name.contains("tnt")) {
            neth(im, "tnt");
        }else if(name.contains("sun")) {
            neth(im, "sun");
        }else if(name.contains("dito")) {
            neth(im, "dito");
        }else if(name.contains("gomo")) {
            neth(im, "gomo");
        }else if(name.contains("piso")) {
            neth(im, "pisowifi");
        }else if(name.contains("sts")) {
            neth(im, "sts");
        } else if(name.contains("")){
            neth(im, "nothing");
        }
    }
    public void neth(ImageView im, String pogiko) throws Exception {
        Glide.with(context).load("https://raw.githubusercontent.com/darkneth07/myflags_simcard/jlyneth/sim_" + pogiko + ".png").placeholder(R.drawable.icon).into(im);
    }
}

