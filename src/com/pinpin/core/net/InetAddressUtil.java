/**
 * 
 */
package com.pinpin.core.net;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author tony
 * 
 */
public class InetAddressUtil {
	/**
	 * weak check is numberic
	 * 
	 */
	public static boolean isNumeric(String address) {
		String pattern = "^(\\d{1,3}\\.){3}\\d{1,3}$";
	    //String str = "192.160.2.3";
	    Pattern p = Pattern.compile(pattern);
	    Matcher m = p.matcher(address);
	    
	    while (m.find()) {
	    	return true;
	    }
	    
		return false;
	}
    
}





