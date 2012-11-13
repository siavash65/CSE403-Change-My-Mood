package cmm.model;
// For Code Review
public enum Mood {
	HAPPY("HA"), INSPIRED("IN"), ROMANTIC("RO"), EXCITED("EX");
	
	public String value;
	private Mood(String value) {
		this.value = value;
	}
	
	public static Mood fromInt(int i) {
		for (Mood m : Mood.values()) {
			if (i == m.ordinal()) {
				return m;
			}
		}
		throw new IndexOutOfBoundsException("Unexpected error occured with selecting Mood");
	}
}
