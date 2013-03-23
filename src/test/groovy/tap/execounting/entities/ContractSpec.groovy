package tap.execounting.entities

import org.apache.tapestry5.beanvalidator.BeanValidatorModule
import org.apache.tapestry5.hibernate.HibernateCoreModule
import org.apache.tapestry5.ioc.annotations.SubModule
import org.apache.tapestry5.services.TapestryModule
import spock.lang.Specification
import tap.execounting.dal.HibernateModule
import tap.execounting.services.AppModule
import tap.execounting.services.DateService

import static tap.execounting.data.ContractState.active
import static tap.execounting.data.ContractState.canceled
import static tap.execounting.data.ContractState.complete
import static tap.execounting.util.Helper.*

/**
 * User: truth0
 * Date: 3/20/13
 * Time: 8:37 PM
 */
@SubModule([TapestryModule, BeanValidatorModule, HibernateCoreModule, HibernateModule, AppModule])
class ContractSpec extends Specification {
    def "frozen state of a contract is calculated correctly" (){
        Contract c
        when: "date of freeze and unfreeze are null"
        c = new Contract()
        then: "contract is not frozen"
        !c.isFrozen()

        when: "date of freeze and unfreeze are set up and now is between them"
        c = new Contract(
                dateFreeze: DateService.fromNowPlusDays(-10),
                dateUnfreeze: DateService.fromNowPlusDays(10)
                )
        then: "contract is frozen"
        c.isFrozen()

        when: "date of freeze and unfreeze are ahead from now"
        c = new Contract(
                dateFreeze: DateService.fromNowPlusDays(10),
                dateUnfreeze: DateService.fromNowPlusDays(20)
        )
        then: "contract is not frozen"
        !c.isFrozen()

        when: "date of freeze and unfreeze are gone"
        c = new Contract(
                dateFreeze: DateService.fromNowPlusDays(-20),
                dateUnfreeze: DateService.fromNowPlusDays(-5)
        )
        then: "contract is not frozen"
        !c.isFrozen()
    }

    def "completed state of a contract is computed correctly"(){
        Contract c
        when: "contract have X (10) lessons, and X - 1 (9) are completed"
        c = genContract(10,9)
        then: "contract is active"
        c.getState() == active

        when: "contract have 10 lessons, and 10 are completed"
        c = genContract(10,10)
        then: "contract is completed"
        c.state == complete

        when: "contract is completed and canceled"
        c = genContract(10,10)
        c.canceled = true
        then: "contract is canceled"
        c.state == canceled

        when: "contract is not completed and canceled"
        c = genContract()
        assert c.state == active
        c.canceled = true
        then:
        c.state == canceled
    }
}
