package realworld.testArticle

import database.storage.{JdbcArticleStorage, JdbcTagStorage, JdbcUserStorage}
import realworld.DBServiceTest
import realworld.allTestValue.{Articles, Users}
import model.article.article.model.saveDataModel.ArticleTag
import model.tag.model.save.TagV

import scala.concurrent.ExecutionContext.Implicits.global

class ArticleStorageTest extends DBServiceTest{
//  before{
//    println("before")
//    databaseConnector.createTable
//  }
//  after {
//    println("after")
//    databaseConnector.deleteTable
//  }

  "create model.article" should "return model.article" in new Context {
    awaitFuture(
      newdbRun { runner =>
        //Await.result(
        runner.run(for {
          user <- userStorage.register(Users.testRegister)
          article <- articleStorage.createArticle(
            Articles.normalArticle.copy(authorId = user.id))
        } yield {
          println("create model.article:" + article)
          article should be(Articles.normalArticle)
        })
      })
  }

  "getArticleBySlug" should "return model.article by slug" in new Context {
    awaitFuture(
      newdbRun { runner =>
        //Await.result(
        runner.run(for {
          user <- userStorage.register(Users.testRegister)
          _ <- articleStorage.createArticle(
            Articles.normalArticle.copy(authorId = user.id))
          article <- articleStorage.getArticleBySlug("slug")
        } yield {
          println("getArticleBySlug:" + article)
          article should be(Some(Articles.normalArticle))
        })
      })
  }

  "update model.article" should "return update data" in new Context {
    awaitFuture(
      newdbRun { runner =>
        //Await.result(
        runner.run(for {
          user <- userStorage.register(Users.testRegister)
          a <- articleStorage.createArticle(
            Articles.normalArticle.copy(authorId = user.id))
          article <- articleStorage.updateArticle(
            a.copy(body = Articles.updateDescription))
        } yield {
          println("update model.article:" + article)
          article should be
          (Articles.normalArticle.copy(body = Articles.updateDescription))
        })
      })
  }

  "delete model.article" should "delete model.article by slug" in new Context {
    awaitFuture(
      newdbRun { runner =>
        //Await.result(
        runner.run(for {
          user <- userStorage.register(Users.testRegister)
          a <- articleStorage.createArticle(
            Articles.normalArticle.copy(authorId = user.id))
          _ <- articleStorage.deleteArticleBySlug(Articles.normalArticle.slug)
          article <- articleStorage.getArticleBySlug(Articles.normalArticle.slug)
        } yield {
          article should be(None)
        })
      })
  }

  "favorite model.article" should "should set favorite" in new Context {
    awaitFuture(
      newdbRun { runner =>
        //Await.result(
        runner.run(for {
          user <- userStorage.register(Users.testRegister)
          article <- articleStorage.createArticle(Articles.normalArticle.copy(authorId = user.id))
          a <- articleStorage.favoriteArticle(user.id, article.id)
          count <- articleStorage.countFavorite(article.id)
        } yield {
          println("a:" + a)
          count should be(1)
        })
      })
  }

  "unfavorite model.article" should "should unset favorite" in new Context {
    awaitFuture(
      newdbRun { runner =>
        //Await.result(
        runner.run(for {
          user <- userStorage.register(Users.testRegister)
          article <- articleStorage.createArticle(Articles.normalArticle.copy(authorId = user.id))
          _ <- articleStorage.favoriteArticle(user.id, article.id)
          a <- articleStorage.unFavoriteArticle(user.id, article.id)
          count <- articleStorage.countFavorite(article.id)
        } yield {
          println(count)
          count should be(0)
        })

      })
  }

  "count favorite" should "should count favorite numbers" in new Context {
    awaitFuture(
      newdbRun { runner =>
        //Await.result(
        runner.run(for {
          user1 <- userStorage.register(Users.testUser1)
          user2 <- userStorage.register(Users.testUser2)
          article <- articleStorage.createArticle(
            Articles.normalArticle.copy(authorId = user1.id))
          _ <- articleStorage.favoriteArticle(user1.id, article.id)
          _ <- articleStorage.favoriteArticle(user2.id, article.id)
          count <- articleStorage.countFavorite(article.id)
        } yield {
          println(count)
          count should be(2)
        })
      })
  }

  "insert tag model.article" should "should insert model.article tag" in new Context {
    awaitFuture(
      newdbRun { runner =>
        //Await.result(
        runner.run(for {
          user <- userStorage.register(Users.testRegister)
          article <- articleStorage.createArticle(
            Articles.normalArticle.copy(authorId = user.id))
          tags <- tagStorage.insertAndGet(Seq(TagV.create("2222")))
          tag <- articleStorage.insertArticleTag(Seq(ArticleTag(0, article.id, tags.head.id)))
        } yield {
          println("tags:" + tags)
          println("tag:" + tag)
          tag should be(Vector(ArticleTag(tag.head.id, article.id, tags.head.id)))
        })
      })
  }

  trait Context{
    val articleStorage = new JdbcArticleStorage()
    val tagStorage = new JdbcTagStorage()
    val userStorage=new JdbcUserStorage()
  }

}
