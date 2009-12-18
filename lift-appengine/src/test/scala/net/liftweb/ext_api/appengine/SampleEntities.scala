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

class ParentEntity extends Entity[ParentEntity] {

  object booleanProp extends BooleanProperty(this)
  object intProp extends IntProperty(this)
  object longProp extends LongProperty(this)
  object stringProp extends StringProperty(this)
}
object ParentEntity extends MetaEntity[ParentEntity] {
  
  val entity = new ParentEntity
}

class ChildEntity extends Entity[ChildEntity] with Parent {

  type Parent = ParentEntity
}
object ChildEntity extends MetaEntity[ChildEntity] {
  
  val entity = new ChildEntity
}

class GrandchildEntity extends Entity[GrandchildEntity] with Parent {
  
  type Parent = ChildEntity
}
object GrandchildEntity extends MetaEntity[GrandchildEntity] {
  
  val entity = new GrandchildEntity
}
