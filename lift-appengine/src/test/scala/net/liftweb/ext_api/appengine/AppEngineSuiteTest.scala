package net.liftweb.ext_api.appengine

import com.google.appengine.api.users._
import net.liftweb.ext_api.appengine.test.AppEngineSuite
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers

@RunWith(classOf[JUnitRunner])
class AppEngineSuiteTest extends AppEngineSuite with ShouldMatchers {
  
  def testUserService {
    
    val service: UserService = UserServiceFactory.getUserService
    
    service.isUserLoggedIn should be (false)
    service.getCurrentUser should be (null)
  }
  
  def testUserServiceLoggedIn {
    
    environment.loggedIn = true
    environment.authDomain = "foo.com"
    environment.email = "bar@baz.com"
    
    val service: UserService = UserServiceFactory.getUserService
    
    service.isUserLoggedIn should be (true)
    service.isUserAdmin should not be (true)
    
    val currentUser = service.getCurrentUser
    
    currentUser should have (
      'authDomain ("foo.com"), 'email ("bar@baz.com"), 'nickname ("bar@baz.com")
    )
  }
  

}
