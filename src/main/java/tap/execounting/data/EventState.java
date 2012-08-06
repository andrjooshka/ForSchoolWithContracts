package tap.execounting.data;

public enum EventState {
	planned(0, "Запланировано"), 
	complete(1, "Состоялось"), 
	failed(2,"Не состоялось"), 
	failedByClient(3,"НС - Клиент"), 
	failedByTeacher(4,"НС - Учитель");

	private int code;
	private String translation;

	private EventState(int code, String translation) {
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
	
	@Override
	public String toString(){
		return this.translation;
	}
}
