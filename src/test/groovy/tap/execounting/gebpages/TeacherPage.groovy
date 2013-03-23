package tap.execounting.gebpages

import geb.Page

/**
 * User: truth0
 * Date: 3/22/13
 * Time: 7:01 PM
 */
class TeacherPage extends Page {
    static url = "http://localhost:8080/bureau/teacherpage/6"
    static at = {
        title.contains "Учителя : "
        !$('body').text().contains('exception')
    }
    static content = {
        nav { module NavigationModule }
    }
}
