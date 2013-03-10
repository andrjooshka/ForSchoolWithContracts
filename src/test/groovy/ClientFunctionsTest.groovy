import org.apache.tapestry5.beanvalidator.BeanValidatorModule
import org.apache.tapestry5.hibernate.HibernateCoreModule
import org.apache.tapestry5.ioc.annotations.Inject
import org.apache.tapestry5.ioc.annotations.SubModule
import org.apache.tapestry5.services.TapestryModule
import spock.lang.Specification
import tap.execounting.dal.HibernateModule
import tap.execounting.dal.mediators.interfaces.ClientMed
import tap.execounting.entities.Client
import tap.execounting.entities.Contract
import tap.execounting.entities.Payment
import tap.execounting.services.AppModule
import tap.execounting.services.DateService

@SubModule([TapestryModule, BeanValidatorModule, HibernateCoreModule, HibernateModule, AppModule])
class ClientFunctionsTest extends Specification {

    @Inject
    private ClientMed clientMed;

    def setup() {
        clientMed.setGroup(clients())
    }

    def "retain by scheduled payments" () {
        when:
        clientMed.retainBySoonPayments(14)

        then:
        clientMed.getGroup().get(0).name == "Glen"
        clientMed.getGroup().size() == 3
    }

    def List<Client> clients(){
        List<Client> list = new ArrayList<>(5);

        // Client #1
        Client c1 = new Client();
        c1.name = "Glen"
        List<Contract> contracts1 = new ArrayList<>()
        c1.contracts = contracts1;
        Contract contract1 = new Contract()
        contract1.client = c1
        contracts1.add(contract1)
        List<Payment> payments1 = new ArrayList<>()
        contract1.payments = payments1;
        Payment pay1 = new Payment();
        payments1.add(pay1)
        pay1.amount = 0
        pay1.contract = contract1
        pay1.date = DateService.fromNowPlusDays(-10)
        pay1.scheduled = true

        // Client #2
        Client c2 = new Client()
        List<Contract> contracts2 = new ArrayList()
        c2.contracts = contracts2
        Contract contract2 = new Contract();
        contracts2.add(contracts2)
        contract2.client = c2
        List<Payment> payments2 = new ArrayList<>();
        contract2.payments = payments2
        Payment pay2 = new Payment()
        payments2.add(pay2)
        pay2.contract = contract2
        pay2.amount = 0;
        pay2.date = DateService.fromNowPlusDays(15)
        pay2.scheduled = true


        list.add(c1)
        list.add(c2)

        return list
    }
}
