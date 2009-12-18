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

import com.google.appengine.api.{datastore => gae}

import net.liftweb.common.{Box, Empty, Failure, Full}
import net.liftweb.util.Log

import scala.collection.jcl._

trait EntityCreator {
  private[appengine] var createEntity: () => gae.Entity
}

trait Entity[T <: Entity[T]] {
  
  val kind = getClass.getCanonicalName
  
  private[appengine] var entity = new gae.Entity(kind)
  
  private[appengine] def has(name: String): Boolean = entity.hasProperty(name)

  private[appengine] def get(name: String): Option[Any] = if (has(name)) Some(entity.getProperty(name)) else None

  private[appengine] def set(name: String, maybe: Option[Any]): T = { maybe match {
      case Some(value) => entity.setProperty(name, value)
      case None => entity.removeProperty(name)
    }
    this.asInstanceOf[T]
  }

  private[appengine] def entityKey = entity.getKey

  private[appengine] def keyIsComplete = entity.getKey.isComplete
  
  def id: Long = entity.getKey.getId
  def key: String = gae.KeyFactory.keyToString(entity.getKey)
  
  def delete() = Entity.datastore.delete(entity.getKey)
  
  def put(): T = {
    Entity.datastore.put(entity)
    this.asInstanceOf[T]
  }
  
  def create(entity: gae.Entity): T = {
    require(entity.getKind == this.kind, "Entities must be of same kind")
    val created = getClass.newInstance.asInstanceOf[T]
    created.entity = entity
    created
  }
  
  private def query: gae.Query = {
    val query = new gae.Query(kind)
    import Conversions.convertMap
    for ((name, value) <- entity.getProperties) {
      query.addFilter(name, gae.Query.FilterOperator.EQUAL, value)
    }
    query
  }
  
  def findAllLike: List[T] = { 
    val preparedQuery = Entity.datastore.prepare(query)
    val it = new MutableIterator.Wrapper(preparedQuery.asIterator)
    it.map(create(_)).toList
  }
  
}

object Entity {
  
  lazy val datastore = gae.DatastoreServiceFactory.getDatastoreService
}

trait Parent {
  
  self: Entity[_] =>
  
  type Parent <: Entity[Parent]
  
  private var isParentSet: Boolean = false

  private[appengine] def getParent: gae.Entity = Entity.datastore.get(entity.getParent)
    
  def parent(parent: Parent) {
    require( (parent != null), "Parent can not be null")
    require( !isParentSet, "Parent has already been set")
//    require( parent.keyIsComplete, "Parent key is incomplete" )
    isParentSet = true
    self.entity = new gae.Entity(kind, parent.entityKey)
  }
    
}

trait MetaEntity[T <: Entity[T]] {
  
  val entity: T
  
  val canonicalName = getClass getCanonicalName
  val companionName = canonicalName take (canonicalName.length - 1)
  
  def kind = entity.kind
  
  def apply(): T = Class.forName(companionName).newInstance.asInstanceOf[T]
  
  def create(entity: gae.Entity) = this.entity create entity
  
  def delete(entities: List[T]) {
    entities foreach (_.delete())  
  }
  
  private def get(key: gae.Key): Box[T] = {
    try {
      Full(create(Entity.datastore get key))
    } catch {
      case e: Exception => Failure(e.getMessage, Full(e), Empty)
    }
  }
  
  def get(key: String): Box[T] = {
    this get (gae.KeyFactory stringToKey key) 
  }
  
  def getWithId(id: Long): Box[T] = {
    this get gae.KeyFactory.createKey(kind, id)
  }
  
  def getWithId(id: String): Box[T] = {
    try {
      this getWithId id.toLong
    } catch {
      case nfe: NumberFormatException => Failure("Id must be numeric", Full(nfe), Empty)
    }
  }
  
  def getWithParentAndId(parent: Entity[_], id: String): Box[T] = {
    this get gae.KeyFactory.createKey(parent.entityKey, kind, id.toLong)
  }

  def getParentOf(child: Parent): T = create (child getParent)

  def all: gae.PreparedQuery = Entity.datastore prepare (new gae.Query(kind))
  
  def list(query: gae.Query): List[T] = {
    val preparedQuery = Entity.datastore.prepare(query)
    val it = new MutableIterator.Wrapper(preparedQuery.asIterator)
    it.map(create(_)).toList
  }
  
  def first(query: gae.Query): Option[T] = {
    val preparedQuery = Entity.datastore.prepare(query)
    val it = preparedQuery.asIterator
    if (it.hasNext) Some(create(it.next)) else None
  }
  
  def find(key: gae.Key): T = {
    this create Entity.datastore.get(key)
  }

  def findAll: List[T] = {
    val query = new gae.Query(kind)
    list(query)
  }
  
  def findAllWithParent(parent: Entity[_]): List[T] = {
    val key: gae.Key = gae.KeyFactory.stringToKey(parent.key)
    val query = new gae.Query(kind, key)
    list(query)
  }
  
  def findOneWithParent(parent: Entity[_], sortedBy: Property[_, T], direction: gae.Query.SortDirection): Option[T] = {
    val key: gae.Key = gae.KeyFactory.stringToKey(parent.key)
    val query = new gae.Query(kind, key)
    query.addSort(sortedBy.kind, direction)
    first(query)
  }

  def findFirstWithParent(parent: Entity[_], sortedBy: Property[_, T]): Option[T] =
    findOneWithParent(parent, sortedBy, gae.Query.SortDirection.ASCENDING)
  
  def findLastWithParent(parent: Entity[_], sortedBy: Property[_, T]): Option[T] =
    findOneWithParent(parent, sortedBy, gae.Query.SortDirection.DESCENDING)
  
  def countAll: Int = all.countEntities

}