package com.skartivpn.app.ui;

interface IProxyControl {
	boolean start();
	
	boolean stop();
	
	boolean isRunning();
	
	int getPort();
}