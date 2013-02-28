package tap.execounting.components.reports;

import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.beaneditor.BeanModel;
import org.apache.tapestry5.corelib.data.GridPagerPosition;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.BeanModelSource;
import tap.execounting.dal.mediators.interfaces.ClientMed;
import tap.execounting.dal.mediators.interfaces.ContractMed;
import tap.execounting.data.EventState;
import tap.execounting.entities.Comment;
import tap.execounting.entities.Contract;
import tap.execounting.entities.Event;

import java.util.Date;
import java.util.List;

public class ContractsExpiring {
    // Screen fields
    @Parameter @Property
    private GridPagerPosition pagerPosition = GridPagerPosition.BOTH;
    @Parameter("1000") @Property
    private int rows;
    @Property
    private BeanModel<Contract> model;
    @Inject
    private ComponentResources resources;
    @Inject
    private BeanModelSource beanModelSource;
    @Inject
    private ContractMed med;
    @Inject
    private ClientMed clientMed;
    @Property
    private Contract contract;

    void setupRender(){
        if (model == null) {
            model = beanModelSource.createDisplayModel(Contract.class,
                    resources.getMessages());
            model.add("name", null);
            model.add("info", null);
            model.add("lastEvent", null);
            model.exclude("id", "balance", "gift", "discount", "false",
                    "lessonsNumber", "active", "state", "clientId", "typeId",
                    "teacherId", "frozen", "canceled", "contractTypeId",
                    "money", "moneyPaid", "complete","paid","giftMoney", "completelessonsCost",
                    "completeLessonsCost", "singleLessonCost", "date", "lessonsRemain", "dateFreeze", "dateUnfreeze");
            model.reorder("name", "info", "lastEvent");
        }
    }
    public List<Contract> getSource(){
        return med.reset().retainExpiring(1).getGroup();
    }
    public String getInfo() {
        StringBuilder builder = new StringBuilder();

        if (!contract.isComplete() && contract.getLessonsRemain() < 3) {
            builder.append(contract.getEventType().getTitle() + ": ");
            builder.append(contract.getLessonsRemain());
            if (contract.getScheduledLessons().size() > 0)
                builder.append(" и " + contract.getScheduledLessons().size()
                        + " уже запланировано");
        }
        if (builder.substring(builder.length() - 2).equals(", "))
            builder.replace(builder.length() - 2, builder.length(), "");
        return builder.toString();
    }

    public Date getLastEventDate(){
        for(Event e : contract.getEvents())
            if(e.getState()== EventState.planned)
                return e.getDate();
        return null;
    }
    public Date getCommentDate(){
        Comment c = clientMed.setUnit(contract.getClient()).getComment();
        return c == null ? null : c.getDate();
    }
    public String getComment(){
        Comment c = clientMed.setUnit(contract.getClient()).getComment();
        return c == null ? "" : c.getText();
    }
}
