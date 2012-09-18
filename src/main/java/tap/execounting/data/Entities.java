package tap.execounting.data;

public class Entities {
	public static final byte EVENT = 0;
	public static final byte CONTRACT = 1;
	public static final byte TEACHER = 2;
	public static final byte CLIENT = 3;
	public static final byte PAYMENT = 4;
	public static final byte EVENT_TYPE = 5;

	public static byte getCode(String name) {
		if (name == "Event")
			return EVENT;
		if (name == "Contract")
			return CONTRACT;
		if (name == "Teacher")
			return TEACHER;
		if (name == "Payment")
			return PAYMENT;
		if (name == "Client")
			return CLIENT;
		if (name == "EventType")
			return EVENT_TYPE;
		throw new IllegalArgumentException("Class " + name + " not supported");
	}
}
