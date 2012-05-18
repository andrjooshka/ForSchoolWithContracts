package tap.execounting.components.editors;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.tapestry5.OptionGroupModel;
import org.apache.tapestry5.OptionModel;
import org.apache.tapestry5.SelectModel;
import org.apache.tapestry5.SelectModelVisitor;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.internal.OptionModelImpl;
import org.apache.tapestry5.ioc.annotations.Inject;

import tap.execounting.dal.CrudServiceDAO;
import tap.execounting.data.FacilitySelectModel;
import tap.execounting.data.RoomSelectModel;
import tap.execounting.data.TeacherSelectModel;
import tap.execounting.data.TypeSelectModel;
import tap.execounting.entities.Client;
import tap.execounting.entities.Contract;
import tap.execounting.entities.Event;
import tap.execounting.entities.EventType;
import tap.execounting.entities.Facility;
import tap.execounting.entities.Teacher;

public class AddEvent {

	@Property
	@Persist
	private boolean updateMode;

	@Inject
	private CrudServiceDAO dao;

	@Property
	@Persist
	private Event event;

	@Property
	private SelectModel facilitySelect;

	@SuppressWarnings("unused")
	@Property
	private SelectModel teacherSelect;

	@SuppressWarnings("unused")
	@Property
	private SelectModel roomSelect;

	@SuppressWarnings("unused")
	@Property
	private SelectModel typeSelect;

	@InjectComponent
	private Zone roomZone;

	private String etype;

	public String getEtype() {
		return event.getTypeId() == 0 ? "" : dao.find(EventType.class,
				event.getTypeId()).getTitle();
	}

	@Persist
	private List<String> clientNames;

	public List<String> getClientNames() {
		return clientNames;
	}

	public void setClientNames(List<String> clients) {
		clientNames = clients;
	}

	public void setEtype(String etype) {
		this.etype = etype;
	}

	public void setup(Event e) {
		roomSelect = new RoomSelectModel(dao.find(Facility.class,
				e.getFacilityId()));
		
		if(!updateMode){
			e.setFacilityId(event.getFacilityId());
			e.setRoomId(event.getRoomId());
		}
		event = e;
	}

	public void setup(Event e, boolean update) {
		updateMode = update;
		setup(e);
	}

	public void setup(Teacher t) {
		updateMode = false;
		event = new Event();
		event.setHostId(t.getId());
		event.setDate(new Date());

	}

	public void setup(Facility f) {
		updateMode = false;
		event = new Event();
		event.setFacilityId(f.getFacilityId());
		event.setDate(new Date());
	}

	public void reset() {
		event = new Event();
		event.setDate(new Date());
		updateMode = false;
	}

	EventType checkType() {
		EventType eventType = null;
		List<EventType> etypes = dao.findWithNamedQuery(EventType.ALL);
		for (EventType et : etypes)
			if (et.getTitle().equals(etype)) {
				eventType = et;
				break;
			}

		if (eventType == null)
			throw new IllegalArgumentException("Тип занятий " + etype
					+ " не найден");
		return eventType;
	}

	Client findClient(String name) {
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("name", name);
		Client c = dao.findUniqueWithNamedQuery(Client.BY_NAME, params);
		if (c == null)
			throw new IllegalArgumentException("Клиент с именем '" + name
					+ "' не найден");
		return c;
	}

	void onSubmit() {
		EventType eventType = checkType();

		event.setTypeId(eventType.getId());

		for (String name : clientNames) {
			// the procedure standard for each client follows
			if (name != null && name.length() > 3) {
				Client c = findClient(name);

				// source dependent from updatemode
				List<Contract> source = updateMode ? c.getContracts() : c
						.getActiveContracts();

				// event type check for compatibility with existing contracts of
				// the client
				boolean typeMatch = false;
				List<Contract> candidates = new ArrayList<Contract>();
				for (Contract con : source) {
					// typeMatch = con.getTypeId() == evet.getId(); //strict
					// type_id check;
					typeMatch = con.getEventType().getTypeTitle()
							.equals(eventType.getTypeTitle()); // soft check

					if (event.haveContract(con)) {
						typeMatch = con.getEventType().getTypeTitle()
								.equals(eventType.getTypeTitle());
						if (!typeMatch) {
							throw new IllegalArgumentException(
									String.format(
											"Ошибка: сохраняемое занятие для клиента %s, имеет тип (%s), отличный "
													+ "от типа договора (%s) к которому оно прикреплено. Если вы хотите перекинуть это занятие"
													+ "в другой договор - "
													+ "то сначала удалите его из текущего.",
											name, eventType.getTitle(), con
													.getEventType().getTitle()));
						}
						candidates.clear();
						break;
					}

					if (typeMatch) {
						candidates.add(con);
					}
				}
				if (candidates.size() > 0) {
					sort(candidates); // by date ascending
					event.addContract(candidates.get(0));
				}
				if (!typeMatch)
					throw new IllegalArgumentException(String.format(
							"По клиенту %s договора на %s не найдено", name,
							eventType.getTitle()));

			}
		}

		if (updateMode) {
			dao.update(event);
		} else {
			dao.create(event);
		}
	}

	private void sort(List<Contract> list) {
		for (int i = 0; i < list.size() - 1; i++)
			for (int j = list.size() - 1; j > i; j--) {
				Contract icon = list.get(i);
				Contract jcon = list.get(j);
				if (icon.getDate().after(jcon.getDate())) {
					list.set(j, icon);
					list.set(i, jcon);
				}
			}
	}

	Object onValueChangedFromFacilityId(int facilityId) {
		event.setFacilityId(facilityId);
		Facility f = dao.find(Facility.class, facilityId);
		roomSelect = new RoomSelectModel(f);

		return roomZone.getBody();
	}

	void onPrepareForRender() {
		clientNames = new ArrayList<String>();
		if (event.getClients() != null)
			for (Client c : event.getClients())
				clientNames.add(c.getName());

		teacherSelect = new TeacherSelectModel(dao);
		facilitySelect = new FacilitySelectModel(dao);
		// roomSelect
		if (event == null) {
			roomSelect = new RoomSelectModel(dao.find(Facility.class,
					(Integer) facilitySelect.getOptions().get(0).getValue()));
		} else {
			if (event.getFacilityId() == 0)
				roomSelect = new RoomSelectModel(
						dao.find(Facility.class, (Integer) facilitySelect
								.getOptions().get(0).getValue()));
			else
				roomSelect = new RoomSelectModel(dao.find(Facility.class,
						event.getFacilityId()));
		}

		typeSelect = new TypeSelectModel(dao);
		if(!updateMode) event.setComment("");
	}
	
	void setupRender(){
		if(!updateMode) event.setComment("");
	}

	List<String> onProvideCompletionsFromEventTypes(String starts) {
		List<String> res = new ArrayList<String>();
		starts = starts.toLowerCase();

		for (EventType etype : etypes()) {
			if (etype.getTitle().toLowerCase().contains(starts))
				res.add(etype.getTitle());
		}

		return res;
	}

	SelectModel onProvideCompletionsFromClientField(String starts) {

		List<String> res = dao.findWithNamedQuery(Client.ALL_NAMES);
		starts = starts.toLowerCase();

		for (int i = res.size() - 1; i >= 0; i--)
			if (!res.get(i).toLowerCase().contains(starts))
				res.remove(i);
		return new StringSelectModel(res);
	}

	void onValidateFromForm() {
		for (EventType et : etypes())
			if (et.getTitle().equals(etype)) {
				event.setTypeId(et.getId());
				return;
			}
		String message = "Не найден тип занятий с названием \"" + etype + "\"";
		throw new IllegalArgumentException(message);
	}

	private List<EventType> etypes() {
		return dao.findWithNamedQuery(EventType.ALL);
	}

	private static class StringSelectModel implements SelectModel {
		private final List<String> strings;

		public StringSelectModel(final List<String> strings) {
			this.strings = strings;
		}

		public List<OptionModel> getOptions() {
			final List<OptionModel> options = new ArrayList<OptionModel>();

			for (final String string : this.strings) {
				options.add(new OptionModelImpl(string));
			}

			return options;
		}

		public List<OptionGroupModel> getOptionGroups() {
			return null;
		}

		public void visit(final SelectModelVisitor visitor) {
		}
	}
}
