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
class ClientMedTest extends Specification {

    @Inject
    private ClientMed clientMed;

    def setup() {
        Client glen = new Client(name: 'Glen');
        glen.contracts = [new Contract(client: glen)]
        glen.contracts[0].payments = [new Payment(
                                        amount: 10,
                                        contract: glen.contracts[0],
                                        date: DateService.fromNowPlusDays(-10),
                                        scheduled: true
                                )]

        Client mark = new Client(name: 'Mark')
        mark.contracts = [new Contract(client: mark)]
        mark.contracts[0].payments = [
                new Payment(
                        amount: 1000,
                        contract: mark.contracts[0],
                        date: DateService.fromNowPlusDays(15),
                        scheduled: true
                )]

        Client greg = new Client(name: 'Greg');
        greg.contracts = [new Contract(client: greg)]
        greg.contracts[0].payments = [
                new Payment(
                        amount: 2000,
                        contract: greg.contracts[0],
                        date: DateService.fromNowPlusDays(10),
                        scheduled: true
                )
        ]

        Client jack = new Client(name: 'Jack')
        jack.contracts = [new Contract(client: jack)]
        jack.contracts[0].payments = [
                new Payment(
                        amount: 2000,
                        contract: jack.contracts[0],
                        date: DateService.fromNowPlusDays(-3),
                        scheduled: false
                )
        ]

        clientMed.setGroup([glen, mark, greg, jack])
    }

    def "retain clients who have scheduled payments within 14 days"() {
        when:
        clientMed.retainBySoonPayments(14)

        then:
        clientMed.group.size() == 2
        clientMed.group.any {man -> man.name == 'Glen'}
        clientMed.group.any {man -> man.name == 'Greg'}
    }

}
