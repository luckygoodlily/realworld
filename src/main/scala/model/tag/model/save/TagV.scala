package model.tag.model.save

final case class TagV(id: Long, name: String)
object TagV {
  def create(tagName: String): TagV = TagV(-1, tagName)
}
