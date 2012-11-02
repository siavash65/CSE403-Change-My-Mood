package cmm.model;

public enum Content {
	PICTURE, VIDEO, TEXT, MUSIC;
	
	public static Content fromInt(int i) {
		for (Content c : Content.values()) {
			if (i == c.ordinal()) {
				return c;
			}
		}
		throw new IndexOutOfBoundsException(/* TODO: Give this a good msg*/);
	}
}
