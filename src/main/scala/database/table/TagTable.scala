package database.table

import model.tag.model.save.TagV
import slick.jdbc.H2Profile.api._

private [database] object TagTable {
  class Tags(tag: Tag) extends Table[TagV](tag, "tags") {

    def id = column[Long]("id", O.AutoInc, O.PrimaryKey)

    def name = column[String]("tag")

    def * = (id, name) <> ((TagV.apply _).tupled, TagV.unapply)
  }

  val tags = TableQuery[Tags]
  //val createTagsTableAction = tags.schema.createIfNotExists
}
