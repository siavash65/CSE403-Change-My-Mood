package cmm.model;

public enum Mood {
	HUMOROUS, ENERVATE, ROMANTIC, INSPIRE;
	
	public static Mood fromInt(int i) {
		for (Mood m : Mood.values()) {
			if (i == m.ordinal()) {
				return m;
			}
		}
		throw new IndexOutOfBoundsException(/* TODO: Give this a good msg*/);
	}
}
