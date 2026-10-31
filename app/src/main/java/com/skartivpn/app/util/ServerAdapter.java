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
import com.skartivpn.app.adapter.ServerFinger;
import android.app.Activity;
import com.skartivpn.inject.tunnel.TunnelUtils;
import android.text.Html;
import com.skartivpn.inject.config.Settings;
import java.net.PortUnreachableException;
public class ServerAdapter extends BaseAdapter {

    public static ImageView im;
    Context context; 
	Activity c;
    ArrayList<ServerModel> arrayList;
	int nethie00;

    public ServerAdapter(Activity c, Context context, ArrayList<ServerModel> arrayList, int nethie00) {
        this.context = context;
		this.c = c;
        this.arrayList = arrayList;
		this.nethie00 = nethie00;
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
    public  View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView ==  null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.spinner_item, parent, false);
        }
        TextView name,info,stat,ping;
		CardView mainCardz = convertView.findViewById(R.id.mainCardz);
        name = (TextView) convertView.findViewById(R.id.itemNamea);
        info = (TextView) convertView.findViewById(R.id.sinf);
		stat = (TextView) convertView.findViewById(R.id.statusping);
		ping = (TextView) convertView.findViewById(R.id.pinglatency);
  
        name.setText(arrayList.get(position).getServerName());
        info.setText(arrayList.get(position).getServerInfo());
	    mainCardz.startAnimation(AnimationUtils.loadAnimation(context, R.anim.grow));
		getPing(c, ping, stat, arrayList.get(position).getServerHost(), arrayList.get(position).getServerPort());
        try {
            getServerIcon(position, im);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return convertView;
    }
	private void getPing(final Activity c, final TextView ping, final TextView stat, final String Host, final String Port){

		new ServerFinger(new ServerFinger.OnPingListener(){
				@Override
				public void ms(final int i) {
					c.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								String clr0;
								if(i<100){
									clr0 = "#04D000";
								} else {
									clr0 = "#FD1C0D";
								}

								if (TunnelUtils.isNetworkOnline(c)){
									ping.setText(Html.fromHtml("<b><font color=\""+ clr0 + "\">"+ String.valueOf(i) +"ms</font></b>"));
								} else {
									ping.setText(" ");
								}
							}
						});
				}
				@Override
				public void accept(final boolean isOnline) {
					c.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								String clr;
								if(isOnline){
									clr = "#04D000";
									stat.setText(Html.fromHtml("<font color=\""+ clr + "\">"+"<b>Online</b></font>"));							
								} else {
									clr = "#FD1C0D";
									if (TunnelUtils.isNetworkOnline(c)){
										stat.setText(Html.fromHtml("<font color=\""+ clr + "\">"+"<b>Offline</b></font>"));
									} else {
										stat.setText(" ");
									}
								}
							}
						});
				}
				@Override
				public void load() {
					ping.setText(Html.fromHtml(" "));
					stat.setText(Html.fromHtml("Checking"));
				}
			}, Host, Port, 1500);
	}
    private void getServerIcon(int position, ImageView im) throws Exception {
		Glide.with(context).load("https://raw.githubusercontent.com/darkneth07/myflags_simcard/jlyneth/" + arrayList.get(position).getServerflag() + ".png").placeholder(R.drawable.icon).into(im);
    }
}

