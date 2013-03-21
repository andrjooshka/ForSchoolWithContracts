import org.openqa.selenium.By
import org.openqa.selenium.WebElement
import org.openqa.selenium.firefox.FirefoxDriver
import spock.lang.Specification
import org.openqa.selenium.WebDriver

import static java.util.concurrent.TimeUnit.SECONDS

/**
 * User: truth0
 * Date: 3/17/13
 * Time: 6:19 PM
 */
class IntegrationTest extends Specification{
    WebDriver drv

    def clients = 'clients'
    def clientPage = 'clientpage/2'
    def login = 'http://localhost:8080/bureau/'
    def reports = 'reports'
    def statistics = 'statistics'
    def CRUD = 'CRUD'
    def teachers = 'teachers'
    def schedules = 'schedules'
    def payroll = 'payroll/6/01.03.2013/31.03.2013/false'
    def teacherPage = 'teacherpage/6'

    def setup(){
        drv = new FirefoxDriver()
        drv.manage().timeouts().implicitlyWait 120, SECONDS
    }

    def cleanup(){
        drv.quit()
    }

    def "a user is greeted with login page"(){
        when: "page is loaded"
        drv.get login

        then: "its title is ok"
        drv.title == 'Вход'
    }

    def "a user can login"(){
        when: 'user logs in'
        login()
        then: 'page title is ok'
        drv.title == 'Бюро'
    }

    def "every page is accessable"(){
        when:
        login()
        go reports
        then:
        thereIsNoException()
        !drv.findElement(By.tagName('body')).getText().contains('There is no data')

        when:
        go teachers
        then: "page does not throw an exception"
        thereIsNoException()

        when:
        go teacherPage
        then:
        thereIsNoException()

        when:
        go payroll
        then:
        thereIsNoException()

        when:
        go clients
        then:
        thereIsNoException()

        when:
        go clientPage
        then:
        thereIsNoException()

        when:
        go statistics
        then:
        thereIsNoException()

        when:
        go schedules
        then:
        thereIsNoException()

        when:
        go CRUD
        then:
        thereIsNoException()
    }

    def type(WebElement field, String text){
        field.clear()
        field.sendKeys text
    }
    def go(address) {
        drv.get(login + address)
    }
    def thereIsNoException(){
        def body = drv.findElement(By.tagName('body'))
        !body.text.contains('Exception')
    }

    def login(){
        drv.get login
        WebElement username = drv.findElement(By.id('username'))
        type username, 'ivan'
        WebElement pwd = drv.findElement(By.id('password'))
        type pwd, 'DraGmar91'
        WebElement submit = drv.findElement(By.cssSelector('input.btn'))
        submit.click()
    }

}
