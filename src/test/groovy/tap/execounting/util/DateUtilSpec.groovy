package tap.execounting.util

import spock.lang.Specification
import tap.execounting.entities.Payment

import static java.util.Calendar.*
import static tap.execounting.util.DateUtil.*
import static tap.execounting.util.DateUtil.retainByDatesEntry

/**
 * User: truth0
 * Date: 3/29/13
 * Time: 9:31 PM
 */
class DateUtilSpec extends Specification {
    def 'floor'(){
        println()
        println 'floor spec'
        def etalon = new GregorianCalendar()
        def date
        when:
        date = Date.parse('H:mm:ss DD M YYYY','12:29:30 31 12 2012');
        println date
        etalon.setTime(date)
        etalon.set(HOUR_OF_DAY, 0)
        etalon.set(MINUTE, 0)
        etalon.set(SECOND, 0)
        etalon.set(MILLISECOND,0)
        floor(date)
        then:
        date.equals etalon.time
        println "etalon: ${etalon.time}\treal: ${date}"
    }

    def 'ceil'(){
        println()
        println 'ceil spec'
        def etalon = new GregorianCalendar()
        def date
        when:
        date = Date.parse('H:mm:ss DD M YYYY','12:29:30 31 12 2012');
        println date
        etalon.setTime(date)
        etalon.add(DAY_OF_YEAR, 1)
        etalon.set(HOUR_OF_DAY, 0)
        etalon.set(MINUTE, 0)
        etalon.set(SECOND, 0)
        etalon.set(MILLISECOND,0)
        etalon.add(MILLISECOND,-1)
        ceil(date)
        then:
        date.equals etalon.time
        println "etalon: ${etalon.time}\treal: ${date}"
    }

    def 'parse'(){
        def etalon = new GregorianCalendar()
        etalon.set(2012,1,23,0,0,0)
        etalon.set(MILLISECOND,0)
        when:
        def str = '23.02.2012'
        def format = 'dd.MM.yyyy'
        def result = DateUtil.parse format, str
        println "string: $str\tresult: $result"
        then:
        result == etalon.time
    }

    def 'retain by dates entry'(){
        // retain by dates works with every class that implements Dated
        // so I will use Payment for tests
        def dat = [fromNowPlusDays(30), fromNowPlusDays(20), fromNowPlusDays(-12), fromNowPlusDays(-50)]
        def genSet = { [new Payment(date: dat[0]), new Payment(date: dat[1]),
                new Payment(date: dat[2]), new Payment(date: dat[3])] }
        def set = genSet()
        when: 'user filters anything by dates'
        def date1 = fromNowPlusDays(-12,false)
        def date2 = fromNowPlusDays(20, true)
        retainByDatesEntry(set, date1, date2)
        then: 'there are only two elements'
        set.size().equals 2

        when: 'only upper bound is in the game'
        set = genSet()
        retainByDatesEntry(set,null,date2)
        then: 'there are three elements'
        set.size().equals 3

        when: 'only lower bound is in the game'
        set = genSet()
        retainByDatesEntry(set,date1,null)
        then: 'there are still three elements'
        set.size().equals 3
    }
}
