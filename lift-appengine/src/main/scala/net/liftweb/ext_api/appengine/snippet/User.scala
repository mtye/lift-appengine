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
