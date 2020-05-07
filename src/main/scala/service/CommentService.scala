package service

import model.article.comment.model.input.collect.CollectAddComment
import model.article.comment.model.output.control.ControlSingleComment
import model.article.comment.model.output.{MultipleComments, SingleComments}
import model.article.comment.model.saveDataModel.CreateComment
import database.storage.{ArticleStorage, CommentStorage, UserStorage}
import database.util.{DBIOOptional, DataBaseConnector, ISO8601}
import model.profile.model.output.Profile

import scala.concurrent.{ExecutionContext, Future}

class CommentService(runner: DataBaseConnector, commentStorage:CommentStorage,
                     articleStorage:ArticleStorage, userStorage: UserStorage)
                    (implicit executionContext: ExecutionContext){

  def createComment(slug: String,
                    userId: Long,
                    comment: CollectAddComment): Future[Option[ControlSingleComment]] = {
    runner.runInTransaction(
      (for {
        a <- DBIOOptional(articleStorage.getArticleBySlug(slug))
        u <- DBIOOptional(userStorage.getUser(a.authorId))
        c <- DBIOOptional(
          commentStorage
            .createComment(CreateComment(comment.comment.body, a.id, userId).create())
            .map(Some(_)))
        follow <- DBIOOptional(
          userStorage.isFollowing(userId, Some(a.authorId)).map(Some(_)))
      } yield {
        println("comment: " + c)
        ControlSingleComment(
          SingleComments(
            id=c.id,
            createdAt=ISO8601(c.createdAt),
            updatedAt=ISO8601(c.updatedAt),
            body=c.body,
            author=Profile(
              u.username,
              u.bio,
              u.image,
              follow)))
      }).dbio)
  }

  def deleteComment(slug:String,id:Long):Future[Int]=
    runner.runInTransaction(commentStorage.deleteComments(id))

  def getComments(slug: String, id: Option[Long]): Future[MultipleComments] = {
    runner.run(
      for {
        a <- articleStorage.getArticleBySlug(slug)
        comments <- commentStorage.getComments(
          a.map(_.id).getOrElse(-1))
        users <- userStorage.getUsersByUserIds(comments.map(_.authorId))
        follows <- userStorage.followingUsers(id, comments.map(_.authorId))
      } yield {
        println("model.article: " + a)
        println("comments: " + comments)
        println("testUsers: " + users)
        println("follows: " + follows)
        MultipleComments(
          comments.map(c => {
            SingleComments(
              id = c.id,
              createdAt = ISO8601(c.createdAt),
              updatedAt = ISO8601(c.updatedAt),
              body = c.body,
              author = {
                users.find(a=>a.id == c.authorId) match {
                  case Some(u) => Profile(
                    username = u.username,
                    bio = u.bio,
                    image = u.image,
                    following = follows.toSet.contains(c.authorId)
                  )
                }
              }
            )
          })
        )
      }
    )
  }
}
