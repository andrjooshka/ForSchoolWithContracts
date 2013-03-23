package tap.execounting.entities

import org.apache.tapestry5.beanvalidator.BeanValidatorModule
import org.apache.tapestry5.hibernate.HibernateCoreModule
import org.apache.tapestry5.ioc.annotations.SubModule
import org.apache.tapestry5.services.TapestryModule
import spock.lang.Specification
import tap.execounting.dal.HibernateModule
import tap.execounting.services.AppModule

/**
 * User: truth0
 * Date: 3/17/13
 * Time: 3:30 PM
 */
@SubModule([TapestryModule, BeanValidatorModule, HibernateCoreModule, HibernateModule, AppModule])
class EventSpec extends Specification{

    EventType type = new EventType(
            price: 2000,
            shareTeacher: 1000,
            title: 'guitar'
    )
    Event event = new Event(
            eventType: type
    )

    def "event returns money like a boss"(){
        when: "event is not free"
        event.setFree(Event.NOT_FREE)
        then: "it returns 1000 for school and 1000 for teacher"
        event.getTeacherMoney() == 1000
        event.getSchoolMoney() == 1000

        when: "event is free from school"
        event.setFree(Event.FREE_FROM_SCHOOL)
        then: "it should return 0 as school money"
        event.getSchoolMoney() == 0
        event.getTeacherMoney() == 1000

        when: "event is free from teacher"
        event.setFree(Event.FREE_FROM_TEACHER)
        then: "it should return 0 for school and 0 for teacher"
        event.getSchoolMoney() == 0
        event.getTeacherMoney() == 0
    }
}
