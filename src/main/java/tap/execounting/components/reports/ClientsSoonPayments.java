package tap.execounting.components.reports;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.beaneditor.BeanModel;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.corelib.data.GridPagerPosition;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.BeanModelSource;
import org.apache.tapestry5.services.ajax.AjaxResponseRenderer;
import tap.execounting.dal.CRUDServiceDAO;
import tap.execounting.dal.mediators.interfaces.ClientMed;
import tap.execounting.dal.mediators.interfaces.PaymentMed;
import tap.execounting.entities.Client;
import tap.execounting.entities.Comment;
import tap.execounting.entities.Payment;
import tap.execounting.security.AuthorizationDispatcher;
import tap.execounting.util.DateUtil;

import java.util.*;

/**
 * User: truth0
 * Date: 2/24/13
 * Time: 5:02 PM
 */
public class ClientsSoonPayments {
    @Parameter
    @Property
    private GridPagerPosition pagerPosition = GridPagerPosition.BOTH;
    @Parameter("1000")
    @Property
    private int rows;
    // Useful bits
    @Inject
    private ComponentResources resources;
    @Inject
    private BeanModelSource beanModelSource;
    @Inject
    private ClientMed clientMed;

    // Screen fields
    @Property
    private BeanModel<Client> model;
    @Property
    private Client client;
    // Page components
    @Component
    private Zone paymentZone;
    @Property
    private Payment loopPayment;
    @Persist
    private int loopPaymentId;
    @Inject
    private Block authBlock;
    @Inject
    private Block confirmBlock;
    @Inject
    private AjaxResponseRenderer renderer;
    @Inject
    private AuthorizationDispatcher dispatcher;
    @Inject
    private PaymentMed paymentMed;

    void setupRender() {
        if (model == null) {
            model = beanModelSource.createDisplayModel(Client.class,
                    resources.getMessages());
            model.add("paymentsInfo", null);
            model.add("comment", null);
            model.exclude("return", "date", "id", "balance", "managerName",
                    "studentInfo", "firstContractDate", "state", "phoneNumber", "canceled");
        }
    }

    public List<Client> getSource() {
        return clientMed.reset().retainBySoonPayments(14).getGroup(true);
    }

    public String getComment() {
        Comment c = clientMed.setUnit(client).getComment();
        return c == null ? "" : c.getText();
    }

    public Date getCommentDate() {
        Comment c = clientMed.setUnit(client).getComment();
        return c == null ? null : c.getDate();
    }

    @Property
    private boolean editing;

    public String getPaymentInfo() {
        StringBuilder builder = new StringBuilder();
        builder.append(DateUtil.toString("dd MMM", loopPayment.getDate()) + ": ");
        builder.append(loopPayment.getAmount());
        builder.append(" р. за '"
                + loopPayment.getContract().getEventType().getTitle() + "'.");
        if (loopPayment.getComment() != null
                && !loopPayment.getComment().equals(""))
            builder.append("Комм.: " + loopPayment.getComment());
        return builder.toString();
    }

    public Block getDeleteBlock() {
        if (dispatcher.canDeletePayments())
            return confirmBlock;
        return authBlock;
    }

    void onEdit(int paymentId) {
        if (dispatcher.canEditPayments())
            editing = true;
        loopPayment = paymentMed.getUnitById(paymentId);
        renderer.addRender("paymentBody" + loopPayment.getId(), paymentZone);
    }

    void onDelete(int paymentId) {
        loopPaymentId = paymentId;
        loopPayment = null;
        if (dispatcher.canDeletePayments())
            dao.delete(Payment.class, paymentId);
        else
            loopPayment = dao.find(Payment.class, loopPaymentId);
        renderer.addRender("paymentBody" + paymentId, paymentZone);
    }

    @Inject
    private CRUDServiceDAO dao;

    void onSuccessFromPaymentEditor() {
        dao.update(loopPayment);
        editing = false;
        renderer.addRender("paymentBody" + loopPayment.getId(), paymentZone);
    }
}
