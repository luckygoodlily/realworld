package model.article.comment.model.saveDataModel

import java.sql.Timestamp
import java.util.Date

final case class CreateComment(body: String, articleId: Long, authorId: Long){
  def create():SaveCommentObject={
    SaveCommentObject(
      id=0,
      body=body,
      articleId=articleId,
      authorId=authorId,
      createdAt=new Timestamp((new Date).getTime),
      updatedAt=new Timestamp((new Date).getTime)
    )
  }

}
