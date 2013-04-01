package tap.execounting.entities

import org.apache.tapestry5.beanvalidator.BeanValidatorModule
import org.apache.tapestry5.hibernate.HibernateCoreModule
import org.apache.tapestry5.ioc.annotations.SubModule
import org.apache.tapestry5.services.TapestryModule
import spock.lang.Specification
import tap.execounting.dal.HibernateModule
import tap.execounting.services.AppModule
import tap.execounting.util.DateUtil

import static tap.execounting.data.ContractState.active
import static tap.execounting.data.ContractState.canceled
import static tap.execounting.data.ContractState.complete
import static tap.execounting.entities.ContractType.Trial
import static tap.execounting.testutil.Helper.*

/**
 * User: truth0
 * Date: 3/20/13
 * Time: 8:37 PM
 */
@SubModule([TapestryModule, BeanValidatorModule, HibernateCoreModule, HibernateModule, AppModule])
class ContractSpec extends Specification {
    def "frozen state of a contract is calculated correctly" (){
        Contract con
        when: "date of freeze and unfreeze are null"
        con = new Contract()
        then: "contract is not frozen"
        !con.frozen

        when: "date of freeze and unfreeze are set up and now is between them"
        con = new Contract(
                dateFreeze: DateUtil.fromNowPlusDays(-10),
                dateUnfreeze: DateUtil.fromNowPlusDays(10))
        then: "contract is frozen"
        con.isFrozen()

        when: "date of freeze and unfreeze are ahead from now"
        con = new Contract(
                dateFreeze: DateUtil.fromNowPlusDays(10),
                dateUnfreeze: DateUtil.fromNowPlusDays(20))
        then: "contract is not frozen"
        !con.isFrozen()

        when: "date of freeze and unfreeze are gone"
        con = new Contract(
                dateFreeze: DateUtil.fromNowPlusDays(-20),
                dateUnfreeze: DateUtil.fromNowPlusDays(-5))
        then: "contract is not frozen"
        !con.isFrozen()
    }

    def "completed state of a contract is computed correctly"(){
        Contract contract
        when: "contract have X (10) lessons, and X - 1 (9) are completed"
        contract = genContract(10,9)
        then: "contract is active"
        contract.state.equals active

        when: "contract have 10 lessons, and 10 are completed"
        contract = genContract(10,10)
        then: "contract is completed"
        contract.state.equals complete

        when: "contract is completed and canceled"
        contract = genContract(10,10)
        contract.canceled = true
        then: "contract is canceled"
        contract.state.equals canceled

        when: "contract is not completed and canceled"
        contract = genContract()
        assert contract.state == active
        contract.canceled = true
        then: 'he is still canceled'
        contract.state.equals canceled

        when: 'contract type is trial and contract is complete by lessons'
        contract = genContract(1,1)
        contract.contractTypeId = Trial
        then: 'it is complete and state equals complete'
        contract.isComplete()
        contract.state.equals complete
    }
}
