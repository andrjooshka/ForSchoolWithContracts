package tap.execounting

import geb.spock.GebSpec
import org.openqa.selenium.firefox.FirefoxDriver
import spock.lang.Ignore
import tap.execounting.gebpages.ClientPage
import tap.execounting.gebpages.Clients
import tap.execounting.gebpages.Home
import tap.execounting.gebpages.Payroll
import tap.execounting.gebpages.Reports
import tap.execounting.gebpages.Settings
import tap.execounting.gebpages.Signin
import tap.execounting.gebpages.Statistics
import tap.execounting.gebpages.TeacherPage
import tap.execounting.gebpages.Teachers

class IntegrationTest extends GebSpec{

    @Ignore
    def "simple navigation using links"(){
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

        when: "user goes to teachers"
        to Teachers
        then: "he is at teachers"
        at Teachers

        when: "user goes to the page of specific teacher"
        to TeacherPage
        then: "it loads"
        at TeacherPage

        when: "user tries to access payroll"
        to Payroll
        then: "it loads"
        at Payroll

        when: "user loads reports"
        to Reports
        then: "they're loaded"
        at Reports

        when: "user goes to the clients page"
        to Clients
        then: "it is loaded"
        at Clients

        when: 'user goes to the page of specific client'
        to ClientPage
        then: "it loads"
        at ClientPage

        when: "user goes to statistics"
        to Statistics
        then: "it loads"
        at Statistics

        when: "user goes to settings"
        to Settings
        then: "it loads"
        at Settings
    }

    @Ignore
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

        when: "user goes to the 'Teachers' page via navigation bar button"
        nav.navTeachers.click()
        then: "he is on 'Teachers' page"
        at Teachers

        when: "user goes to Clients page"
        to nav.navClients.click()
        then: "it opens like a boss"
        at Clients

        when: "user clicks on reports button"
        btnReports.click()
        report "Reports at ${new Date().format("dd.MM.YY")}"
        then: "he is on reports page"
        at Reports
    }

    def "every teacher page is accessible"(){
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

        when: "user goes to the 'Teachers' page via navigation bar button"
        nav.navTeachers.click()
        then: "he is on 'Teachers' page"
        at Teachers

        teacherLinks.size().times { i ->
            to Teachers
            when: "user clicks on some link"
            teacherLinks[i].click()
            then: "it loads"
            at TeacherPage

            when: "user tries to access the payroll"
            payrollDateOne << '01.01.2013'
            payrollDateTwo << '30.03.2013'
            payrollSubmit.click()
            then: "he will get it"
            at Payroll
        }
    }

//    def "statistics page could be clicked and updated"(){
//
//    }
    // Assert that payroll is working for everybody
}