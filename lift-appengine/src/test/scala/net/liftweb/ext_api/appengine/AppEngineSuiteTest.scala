/*
* Copyright 2009 Mark Tye
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions
* and limitations under the License.
*/
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
