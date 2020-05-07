package realworld.testArticle.testComment

import database.storage.{JdbcArticleStorage, JdbcCommentStorage, JdbcUserStorage}
import realworld.DBServiceTest
import realworld.allTestValue.{Articles, Comments, Users}

import scala.concurrent.ExecutionContext.Implicits.global
class CommentStorageTest extends  DBServiceTest{
  "createComment" should "return comment" in new Context {
    awaitFuture(
      newdbRun{ runner =>
        runner.run(for {
          commentUser <- userStorage.register(Users.testRegister)
          article <- articleStorage.createArticle(
            Articles.normalArticle.copy(authorId = commentUser.id))
          comment <- commentStorage.createComment(
            Comments.normalComment.copy(authorId=commentUser.id,articleId = article.id))
        } yield {
          println("comment:"+comment)
          comment should be (Comments.normalComment)
        })
      }
    )
  }

  "get comments" should "return comment in new Context" in new Context{
    awaitFuture(
      newdbRun{ runner =>
      runner.run(for {
        commentUser <- userStorage.register(Users.testRegister)
        article <- articleStorage.createArticle(
          Articles.normalArticle.copy(authorId = commentUser.id))
        _ <- commentStorage.createComment(
          Comments.normalComment.copy(authorId = commentUser.id,articleId = article.id))
        comments <- commentStorage.getComments(article.id)
      } yield {
        println("get comments:"+comments)
        comments should be (Seq(Comments.normalComment))
      })
     }
    )
  }

  "delete Comment" should "delete a comment" in new Context {
    awaitFuture(
      newdbRun{ runner =>
        runner.run(for {
          commentUser <- userStorage.register(Users.testRegister)
          article <- articleStorage.createArticle(
            Articles.normalArticle.copy(authorId = commentUser.id))
          c <- commentStorage.createComment(
            Comments.normalComment.copy(authorId = commentUser.id,articleId = article.id))
          delete <- commentStorage.deleteComments(c.id)
          comments <- commentStorage.getComments(article.id)
        } yield{
          delete should be (1)
          comments.length should be (0)
        })

      }
    )
  }

  trait Context {
    val commentStorage = new JdbcCommentStorage()
    val articleStorage = new JdbcArticleStorage()
    val userStorage= new JdbcUserStorage
  }
}
