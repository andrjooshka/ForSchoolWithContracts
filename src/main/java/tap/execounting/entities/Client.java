package tap.execounting.entities;

import java.util.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import org.apache.tapestry5.beaneditor.NonVisual;

import tap.execounting.data.ContractState;
import tap.execounting.entities.interfaces.Dated;
import tap.execounting.entities.interfaces.Deletable;
import tap.execounting.util.DateUtil;

/**
 * This class does support interface tap.execounting.util.entities.interfaces.Deletable, since it is
 * an accounting unit and it should not be deleted in any case, to not break the
 * data
 *
 * @author truth0
 */
@Entity
@Table(name = "clients")
@NamedQueries({
        @NamedQuery(name = Client.ALL, query = "from Client"),
        @NamedQuery(name = Client.ALL_NAMES, query = "select c.name from Client c"),
        @NamedQuery(name = Client.BY_NAME, query = "from Client c where lower(c.name) like :name"),
        @NamedQuery(name = Client.CANCELED, query = "from Client where canceled = true"),
        @NamedQuery(name = Client.BY_MANAGER_ID, query = "from Client where managerId = :managerId")})
public class Client implements Dated, Deletable {

    public static final String ALL = "Client.all";
    public static final String ALL_NAMES = "Client.allNames";
    public static final String BY_NAME = "Client.byName";
    public static final String CANCELED = "Client.canceled";
    public static final String BY_MANAGER_ID = "Client.byManagerId";
    public static Comparator NameComparator = new NameComparator();
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "client_id")
    private int id;
    @NotNull
    @Column(unique = true)
    private String name;
    private String phoneNumber;
    private Integer managerId;
    @ManyToOne
    @JoinColumn(name = "manager_id", updatable = false, insertable = false)
    private User manager;
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    private List<Contract> contracts = new ArrayList<Contract>();
    @NonVisual
    private boolean canceled;
    @NonVisual
    private String comment;
    @NonVisual
    private Date commentDate;
    @NonVisual
    private boolean deleted;

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getCommentDate() {
        return commentDate;
    }

    public void setCommentDate(Date commentDate) {
        this.commentDate = commentDate;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String number) {
        phoneNumber = number;
    }

    public Integer getManagerId() {
        return managerId;
    }

    public void setManagerId(Integer managerId) {
        this.managerId = managerId;
    }

    public String getManagerName() {
        return manager == null ? "" : manager.getFullname();
    }

    public List<Contract> getContracts() {
        return contracts;
    }

    public List<Contract> getContracts(boolean sortAsc) {
        List<Contract> list = getContracts();
        Collections.sort(list);
        if (!sortAsc)
            Collections.reverse(list);
        return list;
    }

    /**
     * Will return Contracts where state == active
     *
     * @return list of active contracts
     */
    public List<Contract> getActiveContracts() {
        List<Contract> contracts = new ArrayList<Contract>();
        for (Contract c : getContracts()) {
            ContractState state = c.getState();
            if (state == ContractState.active)
                contracts.add(c);
        }
        return contracts;
    }

    public List<Contract> getUnfinishedContracts() {
        List<Contract> contracts = new ArrayList<Contract>();
        for (Contract c : getContracts()) {
            ContractState state = c.getState();
            if (state == ContractState.active
                    || state == ContractState.frozen)
                contracts.add(c);
        }
        return contracts;
    }

    public List<Teacher> getCurrentTeachers() {
        // current teachers - those who defined in existing, unfinished,
        // noncanceled contracts which we get from getUnfinishedContracts
        Set<Teacher> res = new HashSet<Teacher>();
        for (Contract c : getUnfinishedContracts())
            res.add(c.getTeacher());
        return new ArrayList<Teacher>(res);
    }

    public int getReturn() {
        int total = 0;
        // TODO check
        for (Contract c : getContracts()) {
            total += c.getMoney();
        }

        return total;
    }

    public int getBalance() {
        int total = 0;
        for (int i = 0; i < getContracts().size(); i++)
            total += getContracts().get(i).getBalance();

        return total;
    }

    public List<Payment> getPlannedPayments() {
        // TODO review planned payments getter (hidden constant)
        List<Payment> payments = new ArrayList<Payment>();
        for (Contract c : getContracts())
            for (Payment p : c.getPlannedPayments())
                if (p.getDate().before(DateUtil.fromNowPlusDays(15)))
                    payments.add(p);
        return payments;
    }

    public Payment getFirstPlannedPayment() {
        List<Payment> payments = getPlannedPayments();
        if (payments.size() == 0)
            return null;
        Payment p = payments.get(0);
        for (Payment t : payments)
            if (t.getDate().before(p.getDate()))
                p = t;
        return p;
    }

    public Date getFirstPlannedPaymentDate() {
        if (getFirstPlannedPayment() != null)
            return getFirstPlannedPayment().getDate();
        return null;
    }

    public Date getDate() {
        // CRUTCH
        // Fix added to not brake the date retainByState
        Date d = getFirstContractDate();
        return d == null ? new Date() : d;
    }

    public boolean isBetweenDates(Date one, Date two) {
        return !getDate().before(one) && getDate().before(two);
    }

    public Date getFirstContractDate() {
        try {
            return getContracts(true).get(0).getDate(); }
        catch (NullPointerException|IndexOutOfBoundsException e) {
            return null; }
    }

    public boolean isCanceled() {
        return canceled;
    }

    public void setCanceled(boolean inactive) {
        this.canceled = inactive;
    }

    static class NameComparator implements Comparator<Client> {

        public int compare(Client c1, Client c2) {
            return c1.name.compareTo(c2.name);
        }
    }
}
