package tap.execounting.data;

public class Entities {
	public static final byte EVENT = 0;
	public static final byte CONTRACT = 1;
	public static final byte TEACHER = 2;
	public static final byte CLIENT = 3;
	public static final byte PAYMENT = 4;
	public static final byte EVENT_TYPE = 5;
	public static final byte FACILITY = 6;
    public static final byte USER = 7;

	public static byte getCode(String name) {
		if (name.equals("Event"))
			return EVENT;
		if (name.equals("Contract"))
			return CONTRACT;
		if (name.equals("Teacher"))
			return TEACHER;
		if (name.equals("Payment"))
			return PAYMENT;
		if (name.equals("Client"))
			return CLIENT;
		if (name.equals("EventType"))
			return EVENT_TYPE;
		if (name.equals("Facility"))
			return FACILITY;
        if (name.equals("User"))
            return USER;
		throw new IllegalArgumentException("Class " + name + " not supported");
	}
}
