package realworld.testArticle.testComment

import database.storage.{ArticleStorage, CommentStorage, UserStorage}
import realworld.DBServiceTest
import realworld.allTestValue.{Articles, Comments, Users}
import service.CommentService
import slick.dbio.DBIO

import scala.concurrent.ExecutionContext.Implicits.global
class CommentServiceTest extends DBServiceTest{
  "create a comment" should "create a comment correctly" in new Context {
    articleStorage.getArticleBySlug(*) answers DBIO.successful(Some(Articles.normalArticle))
    userStorage.getUser(*) answers DBIO.successful(Some(Users.normalUser))
    commentStorage.createComment(*) answers DBIO.successful(Comments.normalComment)
    userStorage.isFollowing(*,*) answers DBIO.successful(true)

    awaitFuture(for{
      comment <- commentService.createComment(Comments.testSlug,Comments.testUserId,Comments.testComment)
    }yield {
      println("comment:"+comment)
      comment should be (Comments.commentResult)
    })
  }

  "get Comments" should "get Comments have id" in new Context {
    articleStorage.getArticleBySlug(*) answers DBIO.successful(Some(Articles.normalArticle))
    commentStorage.getComments(*) answers DBIO.successful(Seq(Comments.normalComment))
    userStorage.getUsersByUserIds(*) answers DBIO.successful(Seq(Users.normalUser))
    userStorage.followingUsers(*,*) answers DBIO.successful(Seq(1L))

    awaitFuture(for {
      comments <- commentService.getComments(Comments.testSlug,Some(1L))
    }yield {
      println("get Comments"+comments)
      comments should be (Seq(Comments.normalComment))
    })
  }

  "get Comments" should "get Comments no id" in new Context {
    articleStorage.getArticleBySlug(*) answers DBIO.successful(Some(Articles.normalArticle))
    commentStorage.getComments(*) answers DBIO.successful(Seq(Comments.normalComment))
    userStorage.getUsersByUserIds(*) answers DBIO.successful(Seq(Users.normalUser))
    userStorage.followingUsers(*,*) answers DBIO.successful(Seq())

    awaitFuture(for {
      comments <- commentService.getComments(Comments.testSlug,None)
    }yield {
      println("get Comments"+comments)
      comments should be (Seq(Comments.normalComment))
    })
  }

  "delete Comments" should "delete Comments" in new Context {
    commentStorage.deleteComments(*) answers DBIO.successful(1)

    awaitFuture(for {
      res <- commentService.deleteComment(Comments.testSlug,1)
    } yield {
      println("delete Comments:"+res)
      res should be (1)
    })

  }

  trait Context {
    val articleStorage = mock[ArticleStorage]
    val userStorage = mock[UserStorage]
    val commentStorage = mock[CommentStorage]

    val commentService = new CommentService(databaseConnector,commentStorage, articleStorage, userStorage)
  }

}
