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
