package tap.execounting.pages;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import tap.execounting.entities.Contract;
import tap.execounting.entities.Event;

public class PayrollTest {

	@Test
	public void testToGroup() {
		// SETUP
		Payroll p = new Payroll();

		// contracts
		List<Contract> contracts;
		Contract c1 = new Contract(), c2 = new Contract(), c3 = new Contract();
		c1.setId(1);
		c2.setId(2);
		c3.setId(3);

		// events
		List<Event> events = new ArrayList<Event>();
		Event e1 = new Event(), e2 = new Event(), e3 = new Event();
		e1.setId(1);
		e1.setId(2);
		e1.setId(3);
		e1.setTypeId(0);
		e2.setTypeId(0);
		e3.setTypeId(0);
		e1.getContracts().add(c1);
		e1.getContracts().add(c2);
		e1.getContracts().add(c3);
		e2.getContracts().add(c1);
		e2.getContracts().add(c2);
		e2.getContracts().add(c3);
		e3.getContracts().add(c1);
		e3.getContracts().add(c2);
		e3.getContracts().add(c3);

		events.add(e1);
		events.add(e2);
		events.add(e3);

		// ACT
		contracts = p.toContracts(events);

		// ASSERT
		assert (contracts.size() == 3);
		for (Contract c : contracts) {
			assert(c.getEvents().size()==3);
			boolean b1, b2, b3;
			b1 = b2 = b3 = false;
			for (Event e : c.getEvents()) {
				b1 |= e.getId() == 1;
				b2 |= e.getId() == 2;
				b3 |= e.getId() == 3;
			}
			assert(b1&&b2&&b3);
		}
	}

}
