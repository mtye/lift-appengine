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
import datastore.DatastoreServiceFactory._
import net.liftweb.ext_api.appengine.test.AppEngineSuite
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class DatastoreTest extends AppEngineSuite {
    
  def testEntity {
    
    val entity = new datastore.Entity("foo")
    val key = getDatastoreService.put(entity)
    val got = getDatastoreService.get(key)
    
    assert(entity.getKind === got.getKind)
  }
  
  def testParentEntity {
    
    val entity = new ParentEntity
    
    entity.booleanProp(Some(true))
    entity.intProp(Some(43))
    entity.longProp(Some(123456789L))
    entity.stringProp(Some("foo"))
    
    entity.put
    
    assert(ParentEntity.kind === entity.kind)
    
    import datastore.KeyFactory._
    
    val found = ParentEntity.find(stringToKey(entity.key))
    
    assert(Some(true) === found.booleanProp.asOption)
    assert(Some(43) === found.intProp.asOption)
    assert(Some(123456789L) === found.longProp.asOption)
    assert(Some("foo") === found.stringProp.asOption)
  }



}