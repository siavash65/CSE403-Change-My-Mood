package cmm.model;
// For Code Review
public enum Content {
	PICTURE("PI"), VIDEO("VI"), TEXT("TE"), AUDIO("AU");
	
	public String value;
	private Content(String value) {
		this.value = value;
	}
	
	public static Content fromInt(int i) {
		for (Content c : Content.values()) {
			if (i == c.ordinal()) {
				return c;
			}
		}
		throw new IndexOutOfBoundsException("Unexpected error occured with selecting Content");
	}
}
