package com.songwie.util.ip;

import java.net.InetAddress;

public class IPUtil {
	
	public static String getLocalIp(){
		String localname = "";
		String localip = "";

        try {
            InetAddress.getLocalHost();
             
            localname = InetAddress.getLocalHost().getHostName();
            localip = InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {  }
        
        return localip;
	} 
	
	public static void main(String[] args) {
		System.err.println(getLocalIp());;
    }
}
