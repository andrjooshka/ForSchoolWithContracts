package tap.execounting.data;

public enum EventState {
	planned(0), complete(1), failed(2), failedByClient(3), failedByTeacher(4);

	private int code;

	private EventState(int code) {
		this.code = code;
	}

	public int getCode() {
		return this.code;
	}

	public static EventState fromCode(int code) {
		switch (code) {
		case 0:
			return planned;
		case 1:
			return complete;
		case 2:
			return failed;
		case 3:
			return failedByClient;
		case 4:
			return failedByTeacher;
		default:
			return planned;
		}
	}
}
