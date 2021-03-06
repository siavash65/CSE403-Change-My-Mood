package cmm.model;

import cmm.util.Utilz;
// For Code Review
public class UrlProvider {
	//"http://10.0.2.2:8000/api/" //for development
	private static final String BASE_URL = "http://changemymood.herokuapp.com/api/"; // for deploy
	//private static final String BASE_URL = "http://testcmm.herokuapp.com/api/";
	private static final String GET_CONTENT = "getContent";
	private static final String RANK_CONTENT = "rateContent";
	private static final String MOOD = "mood";
	private static final String CONTENT = "content";
	public static final String MID = "mid";
	public static final String RATE = "rank";
	public static final String SUCCESS = "success";
	
	public static String getPictureUrl(Mood mood, Content content) {
		String url = Utilz.urlAppend(BASE_URL, GET_CONTENT);
		url = Utilz.urlAppendParam(url, MOOD, "" + mood.value);
		url = Utilz.urlAppendParam(url, CONTENT, "" + content.value);
		return url;
	}
	
	public static String getVideoUrl(Mood mood, Content content){
		String url = Utilz.urlAppend(BASE_URL, GET_CONTENT);
		url = Utilz.urlAppendParam(url, MOOD, mood.value);
		url = Utilz.urlAppendParam(url, CONTENT, content.value);
		return url;
	}
	
	public static String getRankUrl() {
		return Utilz.urlAppend(BASE_URL, RANK_CONTENT);
	}
}
