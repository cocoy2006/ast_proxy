package molab.java.util;

public class Status {

	public static enum Common {
		ERROR(0), SUCCESS(1), FALSE(0), TRUE(1);
		private int value;

		private Common(int value) {
			this.value = value;
		}

		public int getInt() {
			return value;
		}
	}

}