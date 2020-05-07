package database.storage

import model.article.comment.model.saveDataModel.SaveCommentObject
import database.table.CommentTable
import slick.dbio.DBIO
import slick.jdbc.H2Profile.api._

trait CommentStorage {
  def createComment(comment:SaveCommentObject):DBIO[SaveCommentObject]

  def deleteComments(commentId:Long):DBIO[Int]

  def getComments(articleId:Long):DBIO[Seq[SaveCommentObject]]
}

class JdbcCommentStorage extends CommentStorage {
  def createComment(comment:SaveCommentObject):DBIO[SaveCommentObject]=
    (CommentTable.comments returning CommentTable.comments.map(_.id)
      into ((u,id)=>u.copy(id=id))) += comment

  def deleteComments(commentId:Long):DBIO[Int] = {
    println("delete sucessful")
    CommentTable.comments.filter(c => c.id === commentId).delete
  }

  def getComments(articleId: Long): DBIO[Seq[SaveCommentObject]] =
    CommentTable.comments.filter(c => c.articleId === articleId).result


}

