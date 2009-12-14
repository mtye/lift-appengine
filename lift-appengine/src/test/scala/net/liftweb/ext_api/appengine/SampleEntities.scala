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
