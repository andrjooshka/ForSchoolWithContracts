package tap.execounting.pages;

import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;

public class CRUD {

	@Persist
	@Property
	private String selectedTab; // event, type, client, facility, teacher
	@SuppressWarnings("unused")
	@Property
	private String tab;

	void onActivate() {
		if (selectedTab == null)
			selectedTab = "Clients";
	}

	public String[] getTabs() {
		return new String[] { "Клиенты", "Учителя", "События", "Типы событий",
				"Школы" };
	}

	void onSwitchTab(String tab){
		System.out.println("\n\n" + tab + "\n\n");
		
		if(tab.equals("Клиенты")) selectedTab="Clients";
		if(tab.equals("Учителя")) selectedTab="Teachers";
		if(tab.equals("События")) selectedTab="Events";
		if(tab.equals("Типы событий")) selectedTab="EventTypes";
		if(tab.equals("Школы")) selectedTab="Facilities";
		
		System.out.println("\n\n" + selectedTab + "\n\n");
	}
}