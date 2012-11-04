package cmm.model;
// For Code Review
public enum Content {
	PICTURE, VIDEO, TEXT, MUSIC;
	
	public static Content fromInt(int i) {
		for (Content c : Content.values()) {
			if (i == c.ordinal()) {
				return c;
			}
		}
		throw new IndexOutOfBoundsException("Unexpected error occured with selecting Content");
	}
}
