package tap.execounting.testutil

import org.hibernate.SessionFactory
import org.hibernate.cfg.Configuration
import tap.execounting.entities.Client
import tap.execounting.entities.Contract
import tap.execounting.entities.ContractType
import tap.execounting.entities.Event
import tap.execounting.entities.EventType
import tap.execounting.entities.Facility
import tap.execounting.entities.Payment
import tap.execounting.entities.Room
import tap.execounting.entities.Teacher
import tap.execounting.entities.TeacherAddition
import tap.execounting.entities.User
import tap.execounting.entities.WeekSchedule

import static tap.execounting.entities.ContractType.Standard
import static tap.execounting.data.EventState.complete
import static tap.execounting.data.EventState.planned
import static tap.execounting.util.DateUtil.*

/**
 * User: truth0
 * Date: 3/20/13
 * Time: 8:53 PM
 */
class Helper {
    /**
     * @param number how many events do you need
     * @param completed how many from them will be completed
     * @return
     */
    static List<Event> genEvents(int number = 10, int completed = 0) {
        def t = []
        completed.times { t.add genEvent(true) }
        (number - completed).times { t.add genEvent() }
        return t
    }

    static Event genEvent(boolean completed = false) {
        new Event(
                state: completed ? complete : planned,
                eventType: new EventType(title: "guitar : 111"))
    }

    static def genContract(int eventsNumber = 10, int completed = 5) {
        new Contract(
                lessonsNumber: eventsNumber,
                events: genEvents(eventsNumber, completed),
                contractTypeId: Standard)
    }

    static Client genClient(int contracts = 5, int completed = 4) {
        new Client(
                name: "Glen Quagmire",
                contracts: genContracts(contracts, completed))
    }

    static List<Contract> genContracts(int total, int completed) {
        def t = []
        completed.times { t.add genContract(5, 5) }
        (total - completed).times { t.add genContract(5, 2) }
        return t
    }

    static List<Payment> genPayments(int total, int completed = total / 2) {
        def res = []
        completed.times { res.add genPayment(true) }
        (total - completed).times { res.add genPayment(false) }
        res
    }

    static Payment genPayment(boolean completed, Date date = new Date(), int amount = 1000) {
        new Payment(
                amount: amount,
                date: date,
                scheduled: !completed)
    }

    static SessionFactory genSessionFactory() {
        Configuration cfg = new Configuration()
        [Client, Contract, Event, EventType, Teacher,
                ContractType, Facility, Room, TeacherAddition, User,
                WeekSchedule, Payment].each { cfg.addAnnotatedClass it }

        cfg.setProperty('hibernate.dialect', 'org.hibernate.dialect.MySQL5Dialect')
                .setProperty('hibernate.connection.url', 'jdbc:mysql://127.0.0.1/music')
                .setProperty('hibernate.connection.driver_class', 'com.mysql.jdbc.Driver')
                .setProperty('hibernate.connection.characterEncoding', 'utf8')
                .setProperty('hibernate.connection.username', 'root')
                .setProperty('hibernate.connection.password', '123258789')
                .setProperty('hibernate.connection.provider_class', 'org.hibernate.connection.C3P0ConnectionProvider')
                .setProperty('hibernate.c3p0.idle_test_period', '3000')
                .setProperty('hibernate.c3p0.max_size', '20')
                .setProperty('hibernate.c3p0.max_statements', '50')
                .setProperty('hibernate.c3p0.min_size', '2')
                .setProperty('hibernate.c3p0.timeout', '300')
                .buildSessionFactory()
    }

    static List<Teacher> genCustomTeachers(){
        Teacher ivan = new Teacher(name: 'Ivan', id: 1)

        Teacher sergey = new Teacher(name: 'Sergey', id: 2)

        Teacher spock = new Teacher(name: 'Spock', id: 3)

        [ivan, sergey, spock]
    }

    static List<Client> genGuys(){
        Client glen = new Client(name: 'Glen', contracts: []);
        glen.contracts << new Contract(
                client: glen,
                date: fromNowPlusDays(-10),
                lessonsNumber: 10,
                events: genEvents(),
                teacherId: 1) // Ivan
        glen.contracts << new Contract(
                client: glen,
                date: fromNowPlusDays(-15),
                lessonsNumber: 10,
                events: genEvents(),
                teacherId: 2) // Sergey
        glen.contracts[0].payments = [new Payment(
                amount: 10,
                contract: glen.contracts[0],
                date: fromNowPlusDays(-10),
                scheduled: true)]


        Client mark = new Client(name: 'Mark', contracts: [])
        mark.contracts << new Contract(
                client: mark,
                date: fromNowPlusDays(-5),
                lessonsNumber: 10,
                events: genEvents(),
                teacherId: 2) // Sergey
        mark.contracts << new Contract(
                client: mark,
                date: fromNowPlusDays(-25),
                lessonsNumber: 10,
                events: genEvents(),
                teacherId: 3) // Spock
        mark.contracts[0].payments = [new Payment(
                        amount: 1000,
                        contract: mark.contracts[0],
                        date: fromNowPlusDays(15),
                        scheduled: true)]


        Client greg = new Client(name: 'Greg', contracts: []);
        greg.contracts << new Contract(
                client: greg,
                date: fromNowPlusDays(-8),
                lessonsNumber: 10,
                events: genEvents(),
                teacherId: 3) // Spock
        greg.contracts << new Contract(
                client: greg,
                date: fromNowPlusDays(-16),
                lessonsNumber: 10,
                events: genEvents(),
                teacherId: 3) // Spock
        greg.contracts[0].payments = [new Payment(
                        amount: 2000,
                        contract: greg.contracts[0],
                        date: fromNowPlusDays(10),
                        scheduled: true)]


        Client jack = new Client(name: 'Jack', contracts: [])
        jack.contracts << new Contract(
                client: jack,
                date: fromNowPlusDays(-25),
                lessonsNumber: 10,
                events: genEvents(),
                teacherId: 3) // Spock
        jack.contracts << new Contract(
                client: jack,
                date: fromNowPlusDays(-33),
                lessonsNumber: 10,
                events: genEvents(),
                teacherId: 1) // Ivan
        jack.contracts[0].payments = [new Payment(
                        amount: 2000,
                        contract: jack.contracts[0],
                        date: fromNowPlusDays(20),
                        scheduled: false)]

        [glen, mark, greg, jack]
    }
}
