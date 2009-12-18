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

import com.google.appengine.api._

import net.liftweb.util._
import net.liftweb.http._
import net.liftweb.sitemap._
import net.liftweb.sitemap.Loc._
import Helpers._

class User {
}

object User {
  
  private var service: users.UserService = users.UserServiceFactory.getUserService
  
  def mockServiceWith(mockService: users.UserService) {
    service = mockService
  }
  
  def removeMock() {
    service = users.UserServiceFactory.getUserService
  }
  
  def makeURL(url: String*) = url.mkString("/", "/", "")
    
  def loginURL(redirect: String*) = service.createLoginURL(makeURL(redirect: _*))
  def logoutURL(redirect: String*) = service.createLogoutURL(makeURL(redirect: _*))
    
  def loginLink(redirect: String*) = ExtLink(loginURL(redirect: _*))
  def logoutLink(redirect: String*) = ExtLink(logoutURL(redirect: _*))
    
  lazy val loggedInTest = If(isLoggedIn, S.??("must.be.logged.in"))
  lazy val loggedOutTest = Unless(isLoggedIn, S.??("already.logged.in"))
    
  def loginMenu(home: String*) = Menu(Loc("Login", loginLink(home: _*), S.??("login"), loggedOutTest))
  def logoutMenu(home: String*) = Menu(Loc("Logout", logoutLink(home: _*), S.??("logout"), loggedInTest))
    
  def loginIf(redirect: String*) = If(() => isLoggedIn, () => RedirectResponse(loginURL(redirect: _*)))

  def isAdmin() = service.isUserAdmin
  def isLoggedIn() = service.isUserLoggedIn
    
  private[appengine] def currentUser: Option[users.User] = service.getCurrentUser match {
    case null => None
    case currentUser => Some(currentUser)
  }
    
  def authDomain(): Option[String] = currentUser.map(_.getAuthDomain)
  def email(): Option[String] = currentUser.map(_.getEmail)
  def nickname(): Option[String] = currentUser.map(_.getNickname)
    
}
