package cmm.model;

public enum Rate {
	THUMBSUP, THUMBSDOWN;
	
	public static Rate fromInt(int i){
		for(Rate r : Rate.values()){
			if(i == r.ordinal()){
				return r;
			}
		}
		throw new IndexOutOfBoundsException("Unexpected error occured with rating");
	}
}
