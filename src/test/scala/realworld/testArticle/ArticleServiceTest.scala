package realworld.testArticle

import database.storage.{ArticleStorage, TagStorage, UserStorage}
import org.scalatest.concurrent.ScalaFutures
import realworld.DBServiceTest
import realworld.allTestValue.{Articles, Date, Users}
import service.ArticleService

import scala.concurrent.ExecutionContext.Implicits.global
import slick.dbio.DBIO
class ArticleServiceTest extends DBServiceTest with ScalaFutures{
  "createArticle" should "create an model.article and return the one" in new Context {
    articleStorage.createArticle(*) answers DBIO.successful(Articles.normalArticle)
    tagStorage.findTagByNames(*) answers DBIO.successful(Seq())
    tagStorage.insertAndGet(*) answers DBIO.successful(Articles.tags)
    articleStorage.insertArticleTag(*) answers DBIO.successful(Articles.articleTag)
    userStorage.getUser(*) answers DBIO.successful(Some(Users.normalUser))
    articleStorage.isFavoriteArticleId(*,*) answers DBIO.successful(Seq())
    articleStorage.countFavorite(*) answers DBIO.successful(0)

    awaitFuture(for {
      article<-articleService.createArticle(1,Articles.testArticle)
    } yield {
      println("model.article~~"+article)
      println(Articles.resultArticle)
      article should be (Articles.resultArticle)
    })
  }

  "get model.article by slug" should "return model.article by slug" in new Context {
    articleStorage.getArticleBySlug(*) answers DBIO.successful(Some(Articles.normalArticle))
    userStorage.getUser(*) answers DBIO.successful(Some(Users.normalUser))
    tagStorage.getTagsByArticle(*) answers DBIO.successful(Articles.tags)
    articleStorage.isFavoriteArticleId(*,*) answers DBIO.successful(Seq())
    articleStorage.countFavorite(*) answers DBIO.successful(0)

    awaitFuture(for {
      article<-articleService.getArticleBySlug(Articles.slug)
    } yield {
      println("get model.article:"+article)
      article should be (Articles.resultArticle)
    })
  }

  "update model.article" should "return update model.article" in new Context {
    articleStorage.getArticleBySlug(*) answers DBIO.successful(Some(Articles.normalArticle))
    articleStorage.updateArticle(*) answers DBIO.successful(Articles.normalArticle.copy(
      title=Articles.updateTest.title.get,
      slug="new-title",
      body=Articles.updateTest.body.get,
      updatedAt=Date.currentWhenInserting
    ))
    userStorage.getUser(*) answers DBIO.successful(Some(Users.normalUser))
    tagStorage.getTagsByArticle(*) answers DBIO.successful(Articles.tags)
    articleStorage.isFavoriteArticleId(*,*) answers DBIO.successful(Seq())
    articleStorage.countFavorite(*) answers DBIO.successful(0)

    whenReady(
      for {
        article <- articleService.updateArticleBySlug(Articles.title, 1L, Articles.updateTest)
      } yield article) { article =>
      article.isDefined should be(true)
      article.foreach { a =>
        a.article.title should be("new title")
        a.article.slug should be("new-title")
        a.article.description should be(Articles.resultArticle.get.article.description)
        a.article.body should be ("new body")
        a.article.tagList should be(Articles.resultArticle.get.article.tagList)
      }
    }
  }

  "delete Article by slug" should "delete model.article" in new Context {
    articleStorage.deleteArticleBySlug(*) answers DBIO.successful()

    awaitFuture(for{
      article <- articleService.deleteArticleBySlug(Articles.slug)
    } yield {
      println("a:"+article)
      article should be (a[Unit])
    })
  }

  "favorite Article" should "favorite" in new Context {
    articleStorage.getArticleBySlug(*) answers DBIO.successful(Some(Articles.normalArticle))
    articleStorage.favoriteArticle(*,*) answers DBIO.successful(Articles.favorite)
    articleStorage.countFavorite(*) answers DBIO.successful(1)
    userStorage.getUser(*) answers DBIO.successful(Some(Users.normalUser))
    tagStorage.getTagsByArticle(*) answers DBIO.successful(Articles.tags)

    awaitFuture(for {
      article<-articleService.favoriteArticle(1, Articles.slug)
    } yield {
      println("favorite Article:"+article)
      article should be (Articles.favoriteResultArticle)
    })
  }

  "unfavorite Article" should "unfavorite" in new Context {
    articleStorage.getArticleBySlug(*) answers DBIO.successful(Some(Articles.normalArticle))
    articleStorage.unFavoriteArticle(*,*) answers DBIO.successful(1)
    articleStorage.countFavorite(*) answers DBIO.successful(0)
    userStorage.getUser(*) answers DBIO.successful(Some(Users.normalUser))
    tagStorage.getTagsByArticle(*) answers DBIO.successful(Articles.tags)

    awaitFuture(for {
      article <- articleService.unFavoriteArticle(1, Articles.slug)
    } yield {
      println("unfavorite"+article)
      article should be (Articles.resultArticle)
    })
  }

  "get Articles" should "Articles" in new Context {
    articleStorage.getArticles(*) answers DBIO.successful(Seq(Articles.normalArticle))
    userStorage.getUsersByUserIds(*) answers DBIO.successful(Seq(Users.normalUser))
    tagStorage.getTagsByArticles(*) answers DBIO.successful(Articles.getTagsResult)
    articleStorage.countFavorites(*) answers DBIO.successful(Seq((1L, 0)))

    awaitFuture(for {
      article <- articleService.getArticles(Articles.ArticleAskTest)
    } yield {
      println("get Articles"+article)
      article should be (Articles.multipleResult)
    })
  }

  "get Feeds" should "Feeds" in new Context {
    articleStorage.getArticlesByFollowees(*, *, *) answers DBIO.successful(Seq(Articles.normalArticle))
    articleStorage.isFavoriteArticleIds(*,*) answers DBIO.successful(Seq())
    articleStorage.countFavorites(*) answers DBIO.successful(Seq((1L, 0)))
    userStorage.getUsersByUserIds(*) answers DBIO.successful(Seq(Users.normalUser))
    tagStorage.getTagsByArticles(*) answers DBIO.successful(Articles.getTagsResult)

    awaitFuture(for{
      article <- articleService.getFeeds(1,20,0)
    } yield {
      println("get Feeds"+article)
      article should be (Articles.multipleResult)
    })
  }


  trait Context {
    val articleStorage = mock[ArticleStorage]
    val userStorage = mock[UserStorage]
    val tagStorage = mock[TagStorage]

    val articleService= new ArticleService(databaseConnector,articleStorage, userStorage, tagStorage)
  }

}
