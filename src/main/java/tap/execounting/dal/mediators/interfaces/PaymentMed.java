package tap.execounting.dal.mediators.interfaces;

public interface PaymentMed {
//unit methods
	//unit
	public Payment getUnit();
	public void setUnit();

	//getters
	//planned
	public boolean getPlanned();

	//Comment
	public String getComment();
	
	//Amount
	public int getAmount();
	
	//Date
	public Date getDate();
	
//group methods
	//group
	public List<Payment> getGroup();
	public void setGroup();
	public List<Payment> getAllPayments();
	public void reset();
	public String getFilterState();

	//filters
	//contract
	public PaymentMed filter(Contract unit);
	
	//Date
	public PaymentMed filter(Date date);

	//Planned (state)
	pubilc PaymentMed filter(boolean state);

	//counters
	public Integer countItems();
	
	//amount
	public Integer countAmount();
	public Integer countReturn(Date date1, Date date2);
}
