package tap.execounting.dal

import org.apache.tapestry5.beanvalidator.BeanValidatorModule
import org.apache.tapestry5.hibernate.HibernateCoreModule
import org.apache.tapestry5.ioc.annotations.Inject
import org.apache.tapestry5.ioc.annotations.SubModule
import org.apache.tapestry5.services.TapestryModule
import spock.lang.Specification
import tap.execounting.dal.mediators.ClientMediator
import tap.execounting.dal.mediators.ContractMediator
import tap.execounting.dal.mediators.interfaces.ClientMed
import tap.execounting.entities.Client
import tap.execounting.entities.Contract
import tap.execounting.entities.ContractType
import tap.execounting.services.AppModule
import tap.execounting.util.DateUtil

import static tap.execounting.util.DateUtil.*
import static tap.execounting.util.Trans.*

import static tap.execounting.data.ClientState.beginner
import static tap.execounting.data.ClientState.canceled
import static tap.execounting.data.ClientState.continuer
import static tap.execounting.data.ClientState.frozen
import static tap.execounting.data.ClientState.inactive
import static tap.execounting.data.ClientState.trial
import static tap.execounting.testutil.Helper.*

@SubModule([TapestryModule, BeanValidatorModule, HibernateCoreModule, HibernateModule, AppModule])
class ClientMedSpec extends Specification {

    @Inject
    ClientMed med

    @Inject
    CRUDServiceDAO dao

    def setup() {
        med.setGroup genGuys()
    }

    def "became trials"(){
        med.setGroup genTrialsNovicesContinuers()
        Date date1, date2
        when: 'user wants those clients who became trials from 60 to 35 days ago'
        date1 = fromNowPlusDays(-60,false); date2 = fromNowPlusDays(-35, true);
        med.becameTrials date1, date2
        med.group.each {println it.name}
        then: 'there will be only those folks who have TRIAL contract in that ' +
                'date range. Those are: Lisa, Scott and Meg.'
        med.group.size().equals 3
        ['Lisa', 'Scott', 'Meg'].containsAll med.group.collect {it.name}
    }

    def 'became novices'(){
        med.setGroup genTrialsNovicesContinuers()
        Date date1, date2
        when: 'user want to see only those clients who became novices from 40 to 10 days ago'
        date1 = fromNowPlusDays(-40,false); date2 = fromNowPlusDays(-10,true);
        med.becameNovices date1, date2
        then: 'there will be only those who have FIRST STANDARD contract in that ' +
                'period. Those are: Scott and Mike.'
        med.group.collect {it.name}.containsAll(['Scott', 'Mike'])
        med.group.size().equals 2
    }

    def "retain clients who have scheduled payments within 14 days"() {
        when:
        med.retainBySoonPayments(14)

        then:
        med.group.each { println it.name }
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
                dateFreeze: fromNowPlusDays(-5),
                dateUnfreeze: fromNowPlusDays(5)
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
                                lessonsNumber: 3),
                        new Contract(
                                contractTypeId: ContractType.Trial,
                                events: genEvents(3,3),
                                lessonsNumber: 3)])
        then: "he is still trial"
        med.state == trial
    }

    def "contracts to clients"(){
        List<Contract> contracts = med.group.collect { it.contracts[0] }
        when: "contracts are being converted to clients"
        def clients = contractsToClients(contracts)
        then: "clients are the same"
        ['Glen', 'Mark', 'Greg', 'Jack'].each { out -> clients.any { it.name == out } }
        clients.size() == 4
    }

    def 'clients to contracts'(){
        when:
        def contracts = med.group.collectMany { it.contracts }
        then:
        contracts.every { it instanceof Contract }
    }

    def "retain by name"(){
        when: "user filters clients by name"
        med.retainByName 'glen'
        then: "there is only Glen"
        med.group[0].name.equals 'Glen'
        med.group.size() == 1

        when: "user filters by another name"
        med.retainByName 'greg'
        then: "there is no clients at all"
        med.group.size() == 0
    }

    def "retain by date of first contract"(){
        med.setGroup genGuys()
        when: 'user filters clients by date of first contract'
        def date1 = fromNowPlusDays(-33); floor date1;
        // TODO include lower and upper borders the filter
        // TODO check DateService
        def date2 = fromNowPlusDays(-20); ceil date2
        med.retainByDateOfFirstContract date1, date2
        then: 'there are Mark and Jack'
        med.group.any { it.name.equals 'Jack' }
        med.group.any { it.name.equals 'Mark' }
        med.group.size().equals 2

        when: 'user filters clients by date of first contract'
        date1 = fromNowPlusDays(-16); floor date1
        date2 = fromNowPlusDays(-14); ceil date2
        med.setGroup genGuys()
        med.retainByDateOfFirstContract date1, date2
        then: 'there are Greg and Glen'
        med.group.any { it.name.equals 'Greg' }
        med.group.any { it.name.equals 'Glen' }
        med.group.size().equals 2
    }

    def "retain by active teacher"(){
        def teachers = genCustomTeachers()
        assert med.group.size() == 4
        when: 'user filters clients by the teacher of their active contracts'
        med.retainByActiveTeacher(teachers[0]) // teacher Ivan
        then: 'there are only Glen and Jack'
        med.group.collect { it.name }.containsAll(['Glen', 'Jack'])

        when: 'user filters clients by the teacher of their active contracts'
        med.setGroup genGuys()
        med.retainByActiveTeacher(teachers[2]) // teacher Spock
        then: 'there are Mark, Jack and Greg'
        med.group.collect { it.name }.containsAll(['Mark', 'Greg', 'Jack'])
    }


}
