package tap.execounting.gebpages

import geb.Page

/**
 * User: truth0
 * Date: 3/22/13
 * Time: 7:02 PM
 */
class Statistics extends Page {
    static url = "http://localhost:8080/bureau/statistics"
    static at = {
        title.equals "Занятия"
        !$('body').text().contains('exception')
    }
}
