package com.inwaiders.plames.integration.minecraft.accessor.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class HttpUtils {

	public static String readString(InputStream is) {
		
		try {
			
			StringBuilder rawData = new StringBuilder();
			
			BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
	
				int b;
				
				while((b = br.read()) != -1) {
						
					rawData.append((char) b);
				}
		
			br.close();
			
			return rawData.toString();
			
		}
		catch(IOException e) {
		
			e.printStackTrace();
		}
		
		return "";
	}
}
