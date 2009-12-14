package net.liftweb.ext_api.appengine

import net.liftweb.ext_api.appengine.test.AppEngineSuite
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers

@RunWith(classOf[JUnitRunner])
class UserTest extends AppEngineSuite with ShouldMatchers {
  
  def testMakeUrl {
    User.makeURL() should be ("/")
    User.makeURL("foo") should be ("/foo")
    User.makeURL("foo", "bar") should be ("/foo/bar")
  }
  
  def testNotLoggedIn {
    
    User.isLoggedIn should be (false)
    User.currentUser.isEmpty should be (true)
  }
  
  def testLoggedIn {
    
    environment.loggedIn = true
    environment.authDomain = "foo.com"
    environment.email = "alpha@beta.com"
    
    User.isLoggedIn should be (true)
    User.isAdmin should be (false)
    
    val current = User.currentUser
    current.isEmpty should be (false)
    
    current.get.getAuthDomain should be ("foo.com")
    current.get.getEmail should be ("alpha@beta.com")
  }

}
