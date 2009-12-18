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

import net.liftweb.ext_api.appengine.test.AppEngineSuite
import org.junit.runner.RunWith
import org.scalatest.BeforeAndAfterEach
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers

@RunWith(classOf[JUnitRunner])
class PropertyTest extends AppEngineSuite with ShouldMatchers with BeforeAndAfterEach {
  
  var maybeEntity: Option[ParentEntity] = None
  
  override def beforeEach {
    super.beforeEach
    maybeEntity = Some(new ParentEntity)
  }
  
  def entity: ParentEntity = maybeEntity.get

//  Boolean properties
  def testBooleanProperty {
    entity.booleanProp.isDefined should be (false)
    entity.booleanProp.isEmpty should be (true)
    entity.booleanProp.asOption should be (None)
  }
  def testBooleanPropertyNull {  
    entity.booleanProp(null)
    entity.booleanProp.isDefined should be (false)
    entity.booleanProp.isEmpty should be (true)
  }
  def testBooleanPropertyNone {  
    entity.booleanProp(None)
    entity.booleanProp.isDefined should be (false)
    entity.booleanProp.isEmpty should be (true)
  }
  def testBooleanPropertyTrue {  
    entity.booleanProp(true)
    entity.booleanProp.isDefined should be (true)
    entity.booleanProp.isEmpty should be (false)
    entity.booleanProp.getOrElse(false) should be (true)
  }
  def testBooleanPropertySomeTrue {  
    entity.booleanProp(Some(true))
    entity.booleanProp.isDefined should be (true)
    entity.booleanProp.isEmpty should be (false)
    entity.booleanProp.getOrElse(false) should be (true)
  }
  def testBooleanPropertyFalse {  
    entity.booleanProp(false)
    entity.booleanProp.isDefined should be (true)
    entity.booleanProp.isEmpty should be (false)
    entity.booleanProp.getOrElse(true) should be (false)
  }
  def testBooleanPropertySomeFalse {  
    entity.booleanProp(Some(false))
    entity.booleanProp.isDefined should be (true)
    entity.booleanProp.isEmpty should be (false)
    entity.booleanProp.getOrElse(true) should be (false)
  }
  
//  Int properties
  def testIntProperty {
    entity.intProp.isDefined should be (false)
    entity.intProp.isEmpty should be (true)
    entity.intProp.asOption should be (None)
  }
  def testIntPropertyNone {
    entity.intProp(None)
    entity.intProp.isDefined should be (false)
    entity.intProp.isEmpty should be (true)
    entity.intProp.asOption should be (None)
  }
  def testIntPropertyNull {
    entity.intProp(null)
    entity.intProp.isDefined should be (false)
    entity.intProp.isEmpty should be (true)
    entity.intProp.asOption should be (None)
  }
  def testIntPropertyInt {
    entity.intProp(13579)
    entity.intProp.isDefined should be (true)
    entity.intProp.isEmpty should be (false)
    entity.intProp.getOrElse(0) should be (13579)
  }
  def testIntPropertySomeInt {
    entity.intProp(Some(13579))
    entity.intProp.isDefined should be (true)
    entity.intProp.isEmpty should be (false)
    entity.intProp.getOrElse(0) should be (13579)
  }
  def testIntPropertyEquality {
    entity.intProp(13579)
    val otherEntity = new ParentEntity
    otherEntity.intProp(13579)
    entity.intProp should be (entity.intProp)
    otherEntity.intProp should be (entity.intProp)
  }

//  String properties
  def testStringProperty {
    entity.stringProp.isDefined should be (false)
    entity.stringProp.isEmpty should be (true)
    entity.stringProp.asOption should be (None)
  }
  def testStringPropertyNone {
    entity.stringProp(None)
    entity.stringProp.isDefined should be (false)
    entity.stringProp.isEmpty should be (true)
    entity.stringProp.asOption should be (None)
  }
  def testStringPropertyString {
    entity.stringProp("string")
    entity.stringProp.isDefined should be (true)
    entity.stringProp.isEmpty should be (false)
    entity.stringProp.getOrElse("") should be ("string")
  }
  def testStringPropertySomeString {
    entity.stringProp(Some("string"))
    entity.stringProp.isDefined should be (true)
    entity.stringProp.isEmpty should be (false)
    entity.stringProp.getOrElse("") should be ("string")
  }
  def testStringPropertyEquality {
    entity.stringProp("string")
    val otherEntity = new ParentEntity
    otherEntity.stringProp("string")
    entity.stringProp should be (entity.stringProp)
    otherEntity.stringProp should be (entity.stringProp)
    otherEntity.stringProp("foo")
    entity.stringProp == otherEntity.stringProp should be (false)
  }
  
  

}
