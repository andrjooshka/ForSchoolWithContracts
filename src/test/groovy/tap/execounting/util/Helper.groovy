package tap.execounting.util

import tap.execounting.entities.Client
import tap.execounting.entities.Contract
import tap.execounting.entities.Event
import tap.execounting.entities.EventType

import static tap.execounting.entities.ContractType.Standard
import static tap.execounting.data.EventState.complete
import static tap.execounting.data.EventState.planned

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
    static List<Event> genEvents(int number = 10, int completed = 0){
        def t = []
        completed.times { t.add genEvent(true) }
        (number - completed).times { t.add genEvent() }
        return t
    }

    static Event genEvent(boolean completed = false) {
        new Event(
                state: completed ? complete : planned,
                eventType: new EventType(title: "guitar : 111")
        )
    }

    static def genContract(int eventsNumber = 10, int completed = 5){
        new Contract(
                lessonsNumber: eventsNumber,
                events: genEvents(eventsNumber, completed),
                contractTypeId: Standard
        )
    }

    static Client genClient(int contracts = 5, int completed = 4){
        new Client(
                name: "Glen Quagmire",
                contracts: genContracts(contracts, completed)
        )
    }

    static List<Contract> genContracts(int total, int completed) {
        def t = []
        completed.times { t.add genContract(5,5) }
        (total - completed).times { t.add genContract(5, 2) }
        return t
    }
}
