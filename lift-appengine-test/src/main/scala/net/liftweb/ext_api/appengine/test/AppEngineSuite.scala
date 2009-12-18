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
package net.liftweb.ext_api.appengine.test

import com.google.apphosting.api.ApiProxy
import com.google.appengine.api.datastore.dev.LocalDatastoreService
import com.google.appengine.tools.development.ApiProxyLocalImpl
import java.io.File
import org.scalatest.BeforeAndAfterEach
import org.scalatest.Suite
import scala.reflect.BeanProperty

class UnitTestEnvironment extends ApiProxy.Environment {
  
  @BeanProperty var appId = "Unit Tests"
  @BeanProperty var versionId = "1.0"
  
  @BeanProperty var defaultNamespace = "gmail.com"
  @BeanProperty var requestNamespace = "gmail.com"
  @BeanProperty var authDomain = ""
  @BeanProperty var email = ""

  @BeanProperty var admin = false
  @BeanProperty var loggedIn = false

  @BeanProperty var attributes = new java.util.HashMap[String, java.lang.Object]
}

trait AppEngineSuite extends Suite with BeforeAndAfterEach {

  var environment = new UnitTestEnvironment

  override def beforeEach() {
    super.beforeEach
    environment = new UnitTestEnvironment
    ApiProxy.setEnvironmentForCurrentThread(environment)
    val local = new ApiProxyLocalImpl(new File(".")) {}
    local.setProperty(LocalDatastoreService.NO_STORAGE_PROPERTY, true.toString)
    ApiProxy.setDelegate(local)
  }
  
  override def afterEach() {
    val local = ApiProxy.getDelegate.asInstanceOf[ApiProxyLocalImpl]
    local.getService("datastore_v3").asInstanceOf[LocalDatastoreService].clearProfiles
    ApiProxy.setDelegate(null)
    ApiProxy.setEnvironmentForCurrentThread(null)
    super.afterEach
  }

}
