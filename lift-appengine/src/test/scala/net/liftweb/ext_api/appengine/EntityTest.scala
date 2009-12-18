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
import net.liftweb.ext_api.appengine.test.AppEngineSuite
import org.junit.runner.RunWith
import org.scalatest.BeforeAndAfterEach
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers

@RunWith(classOf[JUnitRunner])
class EntityTest extends AppEngineSuite with ShouldMatchers with BeforeAndAfterEach {
  
  var maybeParent: Option[ParentEntity] = None
  var maybeChild: Option[ChildEntity] = None
  var maybeGrandchild: Option[GrandchildEntity] = None
  
  def parent: ParentEntity = maybeParent.get
  def child: ChildEntity = maybeChild.get
  def grandchild: GrandchildEntity = maybeGrandchild.get
  
  override def beforeEach {
    super.beforeEach
    maybeParent = Some(new ParentEntity)
    maybeChild = Some(new ChildEntity)
    maybeGrandchild = Some(new GrandchildEntity)
    child.parent(parent)
    grandchild.parent(child)
  }
  
  def datastoreService = datastore.DatastoreServiceFactory.getDatastoreService
  
  def testKind {
    parent.kind should equal ("net.liftweb.ext_api.appengine.ParentEntity")
    child.kind should equal ("net.liftweb.ext_api.appengine.ChildEntity")
    grandchild.kind should equal ("net.liftweb.ext_api.appengine.GrandchildEntity")
  }
  
  def testHas {
    parent.has("foo") should be (false)

    parent.entity.setProperty("foo", "bar")
    parent.has("foo") should be (true)
    
    parent.entity.removeProperty("foo")
    parent.has("foo") should be (false)
  }
  
  def testGet {
    parent.get("foo") should be (None)
    
    parent.entity.setProperty("foo", "bar")
    parent.get("foo") should be(Some("bar"))
    
    parent.entity.removeProperty("foo")
    parent.get("foo") should be(None)
  }
  
  def testSet {
    val firstSet = parent.set("foo", Some("bar"))
    parent.entity.getProperty("foo") should be ("bar")
    parent should be theSameInstanceAs (firstSet)
    
    val secondSet = parent.set("foo", None)
    parent.entity.hasProperty("foo") should be (false)
    parent.entity.getProperty("foo") should be (null)
    parent should be theSameInstanceAs (secondSet)
  }
  
  def testEntityKey {
    val parentKey = parent.entityKey
    parentKey.getKind should be ("net.liftweb.ext_api.appengine.ParentEntity")
    parentKey.getParent should be (null)
    
    val childKey = child.entityKey
    childKey.getKind should be ("net.liftweb.ext_api.appengine.ChildEntity")
    childKey.getParent should be theSameInstanceAs (parentKey)
    
    val grandchildKey = grandchild.entityKey
    grandchildKey.getKind should be("net.liftweb.ext_api.appengine.GrandchildEntity")
    grandchildKey.getParent.getKind should be("net.liftweb.ext_api.appengine.ChildEntity")
    grandchildKey.getParent should be theSameInstanceAs (childKey)
  }
  
  def testKeyIsComplete {
    parent.keyIsComplete should be (false)
    
    datastoreService.put(parent.entity)
    parent.keyIsComplete should be (true)
  }
  
  def testKey {
    datastoreService.put(parent.entity)
    parent.key should not be (null)
    
    datastoreService.put(child.entity)
    child.key should not be (null)
    
    datastoreService.put(grandchild.entity)
    grandchild.key should not be (null)
  }
  
  def testDelete {
    datastoreService.put(parent.entity)
    datastoreService.get(parent.entity.getKey) should not be (null)
    parent.delete()
    try {
      datastoreService.get(parent.entity.getKey)
      fail("Should have thrown EntityNotFoundException")
    } catch {
      case enfe: datastore.EntityNotFoundException => 
    }
  }
  
  def testDeleteInvalidEntity {
    try {
      parent.delete()
      fail("Should have thrown InvalidArgumentException")
    } catch {
      case iae: IllegalArgumentException => 
    }
  }
  
  def testCreateParentEntity {
    
    val original = new ParentEntity
    original.set("int", Some(123))
    original.set("string", Some("Foo"))
    
    val entity = new datastore.Entity(original.kind)
    entity.setProperty("int", 456)
    entity.setProperty("string", "Bar")
    
    val created = original.create(entity)
    
    original should not be theSameInstanceAs (created)
    created.kind should be (original.kind)
    created.entity.getKey should be (entity.getKey)
    created.get("none") should be (None)
    created.get("int") should be (Some(456))
    created.get("string") should be (Some("Bar"))
  }
  
  def testCreateChildEntity {
    
    val original = new ChildEntity
    val parent = new ParentEntity
    original.parent(parent)
    original.set("int", Some(123))
    original.set("string", Some("Foo"))
    
    val key = datastore.KeyFactory.createKey(parent.kind, 1)
    
    val entity = new datastore.Entity(original.kind, key)
    entity.setProperty("int", 456)
    entity.setProperty("string", "Bar")
    
    val created = original.create(entity)
    
    original should not be theSameInstanceAs (created)
    created.kind should be (original.kind)
    created.entity.getKey should be (entity.getKey)
    created.get("none") should be (None)
    created.get("int") should be (Some(456))
    created.get("string") should be (Some("Bar"))
  }
  
  
  def testQueryByExample {
    val simple = new ParentEntity
    simple.intProp(Some(123)).longProp(Some(456L)).stringProp(Some("foo"))
    
//    val query = simple.queryByExample
//    query.getKind should be ("net.liftweb.ext_api.appengine.ParentEntity")
//    query.getFilterPredicates.size should be (3)
//    should be (true)(query.getFilterPredicates.contains(new FilterPredicate("intProp", EQUAL, 123)))
//    should be (true)(query.getFilterPredicates.contains(new FilterPredicate("longProp", EQUAL, 456L)))
//    should be (true)(query.getFilterPredicates.contains(new FilterPredicate("stringProp", EQUAL, "foo")))
  }
  
  def testId {
    datastoreService.put(parent.entity)
    datastoreService.put(child.entity)
    
    val parentKey = datastore.KeyFactory.createKey(parent.kind, parent.id)
    
    val parentEntity = datastoreService.get(parentKey)
    parentEntity should be (parent.entity)
    
    val childKey = datastore.KeyFactory.createKey(parent.entityKey, child.kind, child.id)
    
    val childEntity = datastoreService.get(childKey)
    childEntity should be (child.entity)
  }
  
  
  def testGetParentOf {
    
    parent.put
    child.put
    
    child.keyIsComplete should be (true)
    
    val found = ParentEntity.getParentOf(child)
    
    child.keyIsComplete should be (true)
    found.key should be (parent.key) 
  }
  
  def testFindAllWithParent {
    
    parent.put
    child.put
    child.keyIsComplete should be (true)

    grandchild.put
    grandchild.keyIsComplete should be (true)
    
    val children: List[ChildEntity] = ChildEntity.findAllWithParent(parent)
    
    children.size should be (1)
    children(0).key should be (child.key)
    
    val grandchildren = GrandchildEntity.findAllWithParent(child)
    
    grandchildren.size should be (1)
    grandchildren(0).key should be (grandchild.key)
    

  }
  

  
}
