package tap.execounting.components.reports;

import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.beaneditor.BeanModel;
import org.apache.tapestry5.corelib.data.GridPagerPosition;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.BeanModelSource;
import tap.execounting.dal.mediators.interfaces.ClientMed;
import tap.execounting.dal.mediators.interfaces.EventMed;
import tap.execounting.data.ClientState;
import tap.execounting.entities.Client;
import tap.execounting.entities.Comment;
import tap.execounting.entities.Event;
import tap.execounting.services.DateService;

import java.util.Date;
import java.util.List;

public class ClientsStateUndefined {
    // Useful bits
    @Inject
    private ComponentResources resources;
    @Inject
    private ClientMed med;
    @Inject
    private EventMed eventMed;
    @Inject
    private BeanModelSource beanModelSource;
    @Inject
    private Messages messages;

    // Screen fields
    @Property
    private BeanModel<Client> model;
    @Property
    private Client client;

    // Screen fields
    @Parameter @Property
    private GridPagerPosition pagerPosition = GridPagerPosition.BOTH;
    @Parameter("1000") @Property
    private int rows;

    public void setupRender(){
        // subscription ended
        if (model == null) {
            model = beanModelSource.createDisplayModel(Client.class,
                    resources.getMessages());
            model.add("lastEventDate", null);
            model.add("info", null);
            model.add("comment", null);
            model.exclude("return", "date", "id", "balance",
                    "studentInfo", "firstContractDate", "state",
                    "firstPlannedPaymentDate","phoneNumber", "canceled");
        }
    }


    public List<Client> getUndefinedClients() {
        List<Client> list = med.reset().retainByState(ClientState.inactive).sortByName().getGroup(true);
        return list;
    }
    public String getLastEventDate(){
        if (client.getContracts().size() == 0)
            return messages.get("no-contracts");
        Event lastEvent = null;
        eventMed.retainByDatesEntry(DateService.fromNowPlusDays(-31),
                DateService.fromNowPlusDays(1)).retainByClient(client);
        try {
            lastEvent = eventMed.lastByDate();
        } catch (IndexOutOfBoundsException e) {
            return messages.get("no-events");
        }
        return DateService.toString("dd.MM.YY", lastEvent.getDate());
    }

    // Used to display the date of the latest event
    public String getInfo() {
        // TODO optimize that by caching recent events
        if (client.getContracts().size() == 0)
            return messages.get("no-contracts");
        Event lastEvent = null;
        try {
            lastEvent = eventMed.lastByDate();
        } catch (IndexOutOfBoundsException e) {
            return messages.get("no-events");
        } finally {
            eventMed.reset();
        }
        String result = DateService.formatDayMonthNameYear(lastEvent.getDate());
        result += "\t" + lastEvent.getEventType().getTitle();
        return result;
    }

    public String getComment() {
        Comment c = med.setUnit(client).getComment();
        return c == null ? "" : c.getText();
    }

    public Date getCommentDate() {
        Comment c = med.setUnit(client).getComment();
        return c == null ? null : c.getDate();
    }
}
