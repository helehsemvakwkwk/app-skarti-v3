package com.skartivpn.app.adapter;

import android.os.AsyncTask;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.*;
import java.net.UnknownHostException;

public class ServerFinger extends AsyncTask<Void,Void,Boolean> {
    private OnPingListener mListener;
    private String mHost;
    private int mTimeOut;
    private String mPort;
    int latency = 999;

    public interface OnPingListener {
        void accept(boolean isOnline);
        void ms(int isOnline);
		void load();
    }

    public ServerFinger(OnPingListener Listener, String host, String port ,int timeout) {
        mListener = Listener; 
		if (host.toString().equals("")){
			mHost = "127.0.0.1";
		} else {
			mHost = host;
		}
        if (port.equals("")){
			mPort = "2222";
		} else {
			mPort = port;
		}
        mTimeOut = timeout;
        executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        try {
            long date = new Date().getTime();
            Socket sock = new Socket();
            sock.connect(new InetSocketAddress(mHost, Integer.parseInt(mPort)), mTimeOut);
			latency = (int) (new Date().getTime() - date);//this code is base from github 
            sock.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }
	@Override
    public void onPreExecute() { 
        mListener.load();
    }
    @Override
    public void onPostExecute(Boolean internet) { 
        mListener.accept(internet);
        mListener.ms(latency);
    }
}
