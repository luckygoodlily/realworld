package database.table

import java.sql.Timestamp
import java.util.Date

import model.article.comment.model.saveDataModel.SaveCommentObject
import slick.jdbc.H2Profile.api._
import slick.lifted.TableQuery

private [database] object CommentTable {
  class Comments(tag:Tag) extends Table[SaveCommentObject](tag,"comments"){
    def currentWhenInserting = new Timestamp((new Date).getTime)

    def id = column[Long]("id",O.AutoInc,O.PrimaryKey)
    def body = column[String]("body")
    def articleId = column[Long]("articleId")
    def authorId = column[Long]("authorId")

    def createdAt =
      column[Timestamp]("createAt",O.Default(currentWhenInserting))
    def updateAt =
      column[Timestamp]("updateAt",O.Default(currentWhenInserting))

    def * = (id,body,articleId,authorId,createdAt,updateAt)<> ((SaveCommentObject.apply _).tupled, SaveCommentObject.unapply)

  }

  val comments = TableQuery[Comments]
  //val createCommentsTableAction = comments.schema.createIfNotExists // Create the tables

}
