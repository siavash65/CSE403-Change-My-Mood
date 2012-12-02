package cmm.model;

import android.graphics.drawable.Drawable;

public class ContentInfo {
	private String murl;
	private Drawable purl;
	private String up;
	private String down;
	
	public ContentInfo(String murl, Drawable purl, String up, String down){
		if(murl == null){
			this.purl = purl;
			this.murl = null;
		}else if(purl == null){
			this.murl = murl;
			this.purl = null;
		}
		
		this.up = up;
		this.down = down;
	}
	
	public Drawable getPicture(){
		return purl;
	}
	
	public String getVideo(){
		return murl;
	}
	
	public String getUpInfo(){
		return up;
	}
	
	public String getDownInfo(){
		return down;
	}

}
