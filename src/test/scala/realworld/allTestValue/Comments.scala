package realworld.allTestValue

import model.article.comment.model.input.AddCommentsArticle
import model.article.comment.model.input.collect.CollectAddComment
import model.article.comment.model.output.SingleComments
import model.article.comment.model.output.control.ControlSingleComment
import model.article.comment.model.saveDataModel.SaveCommentObject
import database.util.ISO8601
import model.profile.model.output.Profile


object Comments {
  val normalComment = SaveCommentObject(
    id=1,
    body="first-comment",
    articleId=1,
    authorId=1,
    createdAt=Date.currentWhenInserting,
    updatedAt=Date.currentWhenInserting)

  val testSlug="test-slug"
  val testUserId = 50
  val testBody = "testBody"
  val testComment = CollectAddComment(AddCommentsArticle(testBody))
  val commentResult=Some(ControlSingleComment(
    SingleComments(
      id=normalComment.id,
      createdAt=ISO8601(normalComment.createdAt),
      updatedAt=ISO8601(normalComment.updatedAt),
      body=normalComment.body,
      author=Profile(
        Users.normalUser.username,
        Users.normalUser.bio,
        Users.normalUser.image,
        true))))

}
