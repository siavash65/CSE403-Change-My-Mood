package cmm.util;
// For Code Review
public class Utilz {
	public static boolean stringIsEmpty(String s) {
		return s==null || s.trim().isEmpty();
	}
	
	public static final String urlAppendParam(	String url, 
												String key, 
												String value) {
		StringBuilder ret = new StringBuilder();
		ret.append(url);
		
		boolean firstParam = url.indexOf("?") == -1;
		if (firstParam) {
			System.out.println("YES!!!");
		}
		
 		if (firstParam && !url.endsWith("/")) {
			ret.append("/");
		}
		
		if (firstParam) {
			ret.append("?");
		} else {
			ret.append("&");
		}
		
		ret.append(key + "=" + value);
		
		return ret.toString();
	}
	
	public static final String urlAppend(String url, String appen) {
		if (!appen.endsWith("/")) {
			appen = appen + "/";
		}
		
		if (url.endsWith("/")) {
			return url + appen;
		} else {
			return url + "/" + appen;
		}
	}
}
