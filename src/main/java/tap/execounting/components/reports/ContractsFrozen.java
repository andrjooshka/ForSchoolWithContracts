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
import tap.execounting.data.ClientState;
import tap.execounting.data.ContractState;
import tap.execounting.entities.Client;
import tap.execounting.entities.Comment;
import tap.execounting.entities.Contract;

import java.util.Date;
import java.util.List;

public class ContractsFrozen {
    @Parameter
    @Property
    private String pagerPosition = "BOTH";
    @Parameter @Property
    private int rows = 1000;
    // Useful bits
    @Inject
    private ComponentResources resources;
    @Inject
    private BeanModelSource beanModelSource;
    @Inject
    private ContractMed med;
    @Inject
    private ClientMed clientMed;

    // Screen fields
    @Property
    private BeanModel<Contract> model;
    @Property
    private Contract contract;


    void setupRender(){
        if (model == null) {
            model = beanModelSource.createDisplayModel(Contract.class,
                    resources.getMessages());
            model.add("name", null);
            model.exclude("id", "balance", "gift", "discount", "false",
                    "lessonsNumber", "active", "state", "clientId", "typeId",
                    "teacherId", "frozen", "canceled", "contractTypeId",
                    "money", "moneyPaid", "complete","paid","giftMoney", "completelessonsCost",
                    "completeLessonsCost", "singleLessonCost", "date", "lessonsRemain");
            model.reorder("name", "datefreeze", "dateunfreeze");
        }
    }

    public List<Contract> getSource() {
        return med.reset().retainByState(ContractState.frozen).sortByClientName().getGroup(true);
    }

    public String getComment() {
        Comment c = clientMed.setUnit(contract.getClient()).getComment();
        return c == null ? "" : c.getText();
    }

    public Date getCommentDate() {
        Comment c = clientMed.setUnit(contract.getClient()).getComment();
        return c == null ? null : c.getDate();
    }
}
