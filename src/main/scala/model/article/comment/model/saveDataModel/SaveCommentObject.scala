package model.article.comment.model.saveDataModel

import java.sql.Timestamp

case class SaveCommentObject(id: Long,
                             body: String,
                             articleId: Long,
                             authorId:Long,
                             createdAt: Timestamp,
                             updatedAt: Timestamp)

//author: Profile)
