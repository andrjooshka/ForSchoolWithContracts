package tap.execounting.util.dal

import org.apache.tapestry5.beanvalidator.BeanValidatorModule
import org.apache.tapestry5.hibernate.HibernateCoreModule
import org.apache.tapestry5.ioc.annotations.Inject
import org.apache.tapestry5.ioc.annotations.SubModule
import org.apache.tapestry5.services.TapestryModule
import spock.lang.Specification
import tap.execounting.dal.CRUDServiceDAO
import tap.execounting.dal.HibernateModule
import tap.execounting.dal.mediators.ClientMediator
import tap.execounting.dal.mediators.ContractMediator
import tap.execounting.dal.mediators.interfaces.ClientMed
import tap.execounting.dal.mediators.interfaces.ContractMed
import tap.execounting.entities.Client
import tap.execounting.entities.Contract
import tap.execounting.entities.ContractType
import tap.execounting.entities.Payment
import tap.execounting.services.AppModule
import tap.execounting.services.DateService

import static tap.execounting.data.ClientState.active
import static tap.execounting.data.ClientState.beginner
import static tap.execounting.data.ClientState.canceled
import static tap.execounting.data.ClientState.continuer
import static tap.execounting.data.ClientState.frozen
import static tap.execounting.data.ClientState.inactive
import static tap.execounting.data.ClientState.trial
import static tap.execounting.util.Helper.*

@SubModule([TapestryModule, BeanValidatorModule, HibernateCoreModule, HibernateModule, AppModule])
class ClientMedSpec extends Specification {

    @Inject
    ClientMed med

    @Inject
    CRUDServiceDAO dao

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

        med.setGroup([glen, mark, greg, jack])
    }

    def "retain clients who have scheduled payments within 14 days"() {
        when:
        med.retainBySoonPayments(14)

        then:
        med.group.size() == 2
        med.group.any {man -> man.name == 'Glen'}
        med.group.any {man -> man.name == 'Greg'}
    }

    def "client state is computed correctly"() {
        med = new ClientMediator(dao: this.dao, contractMed: new ContractMediator())
        when: "client has zero contracts"
        med.unit = genClient(0,0)
        then: "client is inactive"
        med.state == inactive

        when: "client has 5 finshed contracts"
        med.unit = genClient(5,5)
        then: "client is inactive"
        med.state == inactive

        when: "client has 4 completed contracts and one active"
        med.unit = genClient()
        then: "he is continuer (instead of being simply active)"
        med.state == continuer

        when: "client has 4 completed contracts and one frozen"
        med.unit = genClient(4,4)
        med.unit.contracts.add new Contract(
                dateFreeze: DateService.fromNowPlusDays(-5),
                dateUnfreeze: DateService.fromNowPlusDays(5)
        )
        then: "he is frozen"
        med.state == frozen

        when: "man has 3 active contract and he is canceled"
        med.unit = genClient(3,0)
        med.unit.canceled = true
        then: "he is canceled"
        med.state == canceled

        when: "man has one active standard contract"
        med.unit = genClient(1,0)
        then: "he is beginner"
        med.state == beginner

        when: "man has more than one standard contract, and one of them is active"
        med.unit == genClient(2, 1)
        med.unit.contracts = genContracts(2,1)
        assert med.unit.contracts.size() == 2
        then: "he is continuer"
        med.state == continuer

        when: "man has 2 trial contracts, and at least one of them is active"
        med.unit = new Client(
                name: "Glen",
                contracts: [
                        new Contract(
                                contractTypeId: ContractType.Trial,
                                events: genEvents(3,2),
                                lessonsNumber: 3
                        ),
                        new Contract(
                                contractTypeId: ContractType.Trial,
                                events: genEvents(3,3),
                                lessonsNumber: 3
                        )
                ]
        )
        then: "he is still trial"
        med.state == trial
    }
}
