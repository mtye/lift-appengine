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

import com.google.appengine.api.datastore.{Entity => GaeEntity}
import com.google.appengine.api.datastore.Blob
import com.google.appengine.api.datastore.Key
import com.google.appengine.api.datastore.Link
import com.google.appengine.api.datastore.ShortBlob
import com.google.appengine.api.datastore.Text
import com.google.appengine.api.users.{User => GaeUser}
import com.google.appengine.api.users.UserServiceFactory

import java.util.Date
import java.text.DateFormat

trait Property [T <: Any, Owner <: Entity[Owner]] {
  
  val owner: Owner
  val kind = getClass.getSimpleName.split('$').toList.last

  def asOption: Option[T] = owner.get(kind).asInstanceOf[Option[T]]

  def apply(value: T): Owner = value match {
    case null => owner.set(kind, None)
    case _ => owner.set(kind, Some(value))
  }
  
  def apply(value: Option[T]): Owner = value match {
    case null => owner.set(kind, None)
    case _ => owner.set(kind, value)
  }

  def asString(default: String): String = asOption.map(_.toString).getOrElse(default)
  def asText(default: String): xml.Text = xml.Text(asString(default))

  override def equals(other: Any): Boolean = other match {
    case that: Property[_,_] => (that canEqual this) && this.asOption == that.asOption
  }
  def canEqual(other: Any): Boolean = other.isInstanceOf[Property[_,_]]
}

object Property {
  implicit def propertyToOption[T, Owner <: Entity[Owner]](p: Property[T, Owner]): Option[T] = p.asOption 
}

// Standard Scala/Java properties

class BooleanProperty[Owner <: Entity[Owner]](val owner: Owner) extends Property[Boolean, Owner]

class ByteProperty[Owner <: Entity[Owner]](val owner: Owner) extends Property[Byte, Owner]
class ShortProperty[Owner <: Entity[Owner]](val owner: Owner) extends Property[Short, Owner]
class IntProperty[Owner <: Entity[Owner]](val owner: Owner) extends Property[Int, Owner]
class LongProperty[Owner <: Entity[Owner]](val owner: Owner) extends Property[Long, Owner]
class FloatProperty[Owner <: Entity[Owner]](val owner: Owner) extends Property[Float, Owner]
class DoubleProperty[Owner <: Entity[Owner]](val owner: Owner) extends Property[Double, Owner]

class StringProperty[Owner <: Entity[Owner]](val owner:Owner) extends Property[String, Owner]

class DateProperty[Owner <: Entity[Owner]](val owner: Owner) extends Property[Date, Owner] {
  def now() = apply(Some(new Date()))
  def withFormat(default: String, format: DateFormat) = asOption.map(format.format(_)) getOrElse default
  def asText(default: String, format: DateFormat) = xml.Text(withFormat(default, format))
}

// Google API properties

class TextProperty[Owner <: Entity[Owner]](val owner: Owner) extends Property[Text, Owner]

class ShortBlobProperty[Owner <: Entity[Owner]](val owner: Owner) extends Property[ShortBlob, Owner]
class BlobProperty[Owner <: Entity[Owner]](val owner: Owner) extends Property[Blob, Owner]
 
class KeyProperty[Owner <: Entity[Owner]](val owner: Owner) extends Property[Key, Owner]

class LinkProperty[Owner <: Entity[Owner]](val owner: Owner) extends Property[Link, Owner]

class UserProperty[Owner <: Entity[Owner]](val owner: Owner) extends Property[GaeUser, Owner] {
  def current(): Owner = apply(User.currentUser)
  def authDomain: Option[String] = asOption.map(_.getAuthDomain)
  def email: Option[String] = asOption.map(_.getEmail)
  def nickname: Option[String] = asOption.map(_.getNickname)
}

