package tap.execounting

import geb.spock.GebSpec
import tap.execounting.gebpages.Clients
import tap.execounting.gebpages.Home
import tap.execounting.gebpages.Reports
import tap.execounting.gebpages.Signin
import tap.execounting.gebpages.Teachers

class GebIntegrationTest extends GebSpec{


    def "every page is accessible and navigation is ok"() {
        when: "user goes to signin page"
        to Signin
        then: "page loaded and everything is fine"
        at Signin

        when: "user signs in"
        username << 'ivan'
        password << 'DraGmar91'
        loginButton.click()
        then: "user is on the Home page"
        at Home

//        when: "user clicks on reports button"
//        btnReports.click()
//        then: "he is on reports page"
//        at Reports

        when: "user goes to the 'Teachers' page via navigation bar button"
        nav.navTeachers.click()
//        $('#mainmenu a', href: contains('teachers')).click()
        then: "he is on 'Teachers' page"
        at Teachers

        when: "user goes to Clients page"
        to Clients
        then: "it opens like"
    }

//    def "statistics page could be clicked and updated"(){
//
//    }
    // Assert that each teacherPage is accessible
    // Assert that payroll is working for everybody
}