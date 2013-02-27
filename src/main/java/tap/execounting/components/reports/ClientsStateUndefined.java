package tap.execounting.components.reports;

import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.beaneditor.BeanModel;
import org.apache.tapestry5.corelib.data.GridPagerPosition;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.BeanModelSource;
import tap.execounting.dal.mediators.interfaces.ClientMed;
import tap.execounting.data.ClientState;
import tap.execounting.entities.Client;
import tap.execounting.entities.Comment;
import tap.execounting.entities.Event;
import tap.execounting.services.DateService;

import java.util.Date;
import java.util.List;

public class ClientsStateUndefined {
    @Inject
    private ComponentResources resources;
    @Property
    private BeanModel<Client> model;
    @Inject
    private BeanModelSource beanModelSource;
    @Inject
    private ClientMed med;
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
            model.add("endedInfo", null);
            model.add("comment", null);
            model.exclude("return", "date", "id", "balance",
                    "studentInfo", "firstContractDate", "state",
                    "firstPlannedPaymentDate");
        }
    }


    public List<Client> getUndefinedClients() {
        List<Client> list = med.reset().retainByState(ClientState.undefined).sortByName().getGroup(true);
        return list;
    }
      // Used to display the date of the latest event
//    public String getEndedInfo() {
//        // TODO optimize that by caching recent events
//        if (client.getContracts().size() == 0)
//            return messages.get("no-contracts");
//        Event lastEvent = null;
//        List<Event> cache = eventMed.filter(DateService.fromNowPlusDays(-31),
//                DateService.fromNowPlusDays(1)).getGroup();
//        try {
//            lastEvent = eventMed
//                    .setGroup(cache.subList(0, eventMed.countGroupSize()))
//                    .filter(client).sortByDate(false).getGroup().get(0);
//        } catch (IndexOutOfBoundsException e) {
//            return messages.get("no-events");
//        } finally {
//            eventMed.reset();
//        }
//        String result = DateService.formatDayMonthNameYear(lastEvent.getDate());
//        result += "\t" + lastEvent.getEventType().getTitle();
//        return result;
//    }

    public String getComment() {
        Comment c = med.setUnit(client).getComment();
        return c == null ? "" : c.getText();
    }

    public Date getCommentDate() {
        Comment c = med.setUnit(client).getComment();
        return c == null ? null : c.getDate();
    }
}
