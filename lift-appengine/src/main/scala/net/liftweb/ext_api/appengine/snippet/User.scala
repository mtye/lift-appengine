package net.liftweb.ext_api.appengine.snippet

import net.liftweb.http.SHtml
import net.liftweb.util.Helpers
import Helpers._

import scala.xml.NodeSeq
import scala.xml.Text

import net.liftweb.ext_api.appengine.User._

class User {
  
  def loggedIn(html: NodeSeq) = if (isLoggedIn) {
    bind("user", html,
         "authDomain" -> authDomain.get,
         "email" -> email.get,
         "nickname" -> nickname.get
    )
  } else NodeSeq.Empty
  
  def loggedOut(html: NodeSeq) = if (isLoggedIn) NodeSeq.Empty else html 


}
