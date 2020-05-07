package service


import database.storage.{ArticleStorage, TagStorage, UserStorage}
import slick.dbio.DBIO
import database.util.{DBIOOptional, DataBaseConnector, ISO8601}
import model.article.article.model.input.{ArticleRequest, CreateArticle, UpdateArticle}
import model.article.article.model.output
import model.article.article.model.output.{ArticleMultipleArticles, SingleArticle, control}
import model.article.article.model.output.control.ControlSingleArticle
import model.article.article.model.saveDataModel.{ArticleTag, SaveArticleObject}
import model.profile.model.output.Profile
import model.tag.model.save.TagV
import model.users.model.save.SaveUser

import scala.concurrent.{ExecutionContext, Future}

class ArticleService(runner: DataBaseConnector, articleStorage:ArticleStorage,
                     userStorage: UserStorage, tagStorage:TagStorage)
                    (implicit executionContext: ExecutionContext) {

  def createArticle(authorId: Long,
                     newArticle: CreateArticle): Future[Option[ControlSingleArticle]] = {
    runner.runInTransaction(
      for {
        article <- articleStorage.createArticle(newArticle.create(authorId))
        tags <- createTags(newArticle.tagList) // saveDataModel TagV
        articleTags <- connectTagArticle(tags, article.id) //saveDataModel articleTags
        res <- getArticleResponse(article, tags, Some(authorId))
      } yield {
        println("createArticle: "+article)
        println("tagV: "+ tags)
        println("articleTags"+ articleTags)
        res
      })
  }

  private def createTags(tagNames: Seq[String]) =
    for {
      existingTags <- tagStorage.findTagByNames(tagNames) //找存在的tag
      newTags <- extractNewTag(tagNames, existingTags)
      tags = existingTags ++ newTags
    } yield tags


  private def extractNewTag(tagNames: Seq[String], existingTags: Seq[TagV]) = {
    val existingTagNames = existingTags.map(_.name).toSet
    val newTagNames = tagNames.toSet -- existingTagNames
    val newTags = newTagNames.map(TagV.create).toSeq

    tagStorage.insertAndGet(newTags)
  }

  private def connectTagArticle(tags: Seq[TagV], articleId: Long) = {
    val articleTags = tags.map(tag => ArticleTag(-1, articleId, tag.id))
    articleStorage.insertArticleTag(articleTags)
  }

  private def getArticleResponse(article: SaveArticleObject,
                                 tags: Seq[TagV],
                                 currentUserId: Option[Long]): DBIO[Option[ControlSingleArticle]] =
    (for {
      u <- DBIOOptional(userStorage.getUser(article.authorId))
      a <- DBIOOptional(
        getArticleFavoritesWithTags(article, u, tags, currentUserId).map(Some(_)))
    } yield a).dbio


  private def getArticleFavoritesWithTags(article: SaveArticleObject,
                                 author: SaveUser,
                                 tags: Seq[TagV],
                                 currentUserId: Option[Long]) =
    for {
      favorites <- articleStorage.isFavoriteArticleId(
        currentUserId.getOrElse(0), article.id)
      favoriteCount <- articleStorage.countFavorite(article.id)
    } yield ControlSingleArticle(
        SingleArticle(
          article.slug,
          article.title,
          article.description,
          article.body,
          //tags.map(t => t.name),
          tags.map(_.name),
          ISO8601(article.createdAt),
          ISO8601(article.updatedAt),
          favorites.contains(article.id),
          favoriteCount, // get value
          Profile(
            author.username,
            author.bio,
            author.image,
            false)))


  def getArticleBySlug(slug: String): Future[Option[ControlSingleArticle]] = {
    runner.run(
      (for {
        article <- DBIOOptional(articleStorage.getArticleBySlug(slug))
        tags <- DBIOOptional(
          tagStorage.getTagsByArticle(article.id).map(Some(_)))

        author <- DBIOOptional(userStorage.getUser(article.authorId))
        res <- DBIOOptional(
          getArticleFavoritesWithTags(article, author, tags, Some(0)).map(Some(_)))
      } yield res).dbio)


  }

  def updateArticleBySlug(slug: String,
                          userId: Long,
                          articleUpdated: UpdateArticle): Future[Option[ControlSingleArticle]] =
    runner.runInTransaction(
      (for {
        a <- DBIOOptional(articleStorage.getArticleBySlug(slug))

        article <- DBIOOptional(articleStorage.updateArticle(
              updateArticle(a, articleUpdated)).map(Some(_)))

        tags <- DBIOOptional(
          tagStorage.getTagsByArticle(article.id).map(Some(_)))
        author <- DBIOOptional(userStorage.getUser(article.authorId))
        res <- DBIOOptional(
          getArticleFavoritesWithTags(article, author, tags, Some(userId))
            .map(Some(_)))
      } yield res).dbio)

  private def updateArticle(article: SaveArticleObject,
                             update: UpdateArticle): SaveArticleObject = {
    val title = update.title.getOrElse(article.title)
    val slug = model.article.article.model.toBeSlug(title)
    val description = update.description.getOrElse(article.description)
    val body = update.body.getOrElse(article.body)

    article.copy(
      title = title,
      slug = slug,
      description = description,
      body = body)
  }

  def deleteArticleBySlug(slug: String): Future[Unit] =
    runner.runInTransaction(
      articleStorage.deleteArticleBySlug(slug))

  def favoriteArticle(userId: Long, slug: String) =
    runner.runInTransaction(
      for {
        article <- articleStorage.getArticleBySlug(slug)
        f <- articleStorage.favoriteArticle(
          userId, article.map(b => b.id).getOrElse(-1L)) //Long = -1
        favoriteCount <- articleStorage.countFavorite(
          article.map(_.id).getOrElse(-1L))
        author <- userStorage
          .getUser(article.map(_.authorId).getOrElse(-1L))
        tags <- tagStorage.getTagsByArticle(article.map(_.id).getOrElse(-1L))
      } yield article.map(
        a => control.ControlSingleArticle(
            SingleArticle(
              a.slug,
              a.title,
              a.description,
              a.body,
              tags.map(_.name),
              ISO8601(a.createdAt),
              ISO8601(a.updatedAt),
              true,
              favoriteCount,
              convertUserToProfile(author)))))

  def unFavoriteArticle(userId: Long, slug: String) =
    runner.runInTransaction(
      for {
        article <- articleStorage.getArticleBySlug(slug)
        f <- articleStorage.unFavoriteArticle(
          userId, article.map(b => b.id).getOrElse(-1L))
        favoriteCount <- articleStorage.countFavorite(
          article.map(_.id).getOrElse(-1L))
        author <- userStorage
          .getUser(article.map(_.authorId).getOrElse(-1L))
        tags <- tagStorage.getTagsByArticle(article.map(_.id).getOrElse(-1L))
      } yield article.map(
        a => control.ControlSingleArticle(
            SingleArticle(
              a.slug,
              a.title,
              a.description,
              a.body,
              tags.map(_.name),
              ISO8601(a.createdAt),
              ISO8601(a.updatedAt),
              false,
              favoriteCount,
              convertUserToProfile(author)))))

  private def convertUserToProfile(author: Option[SaveUser]):Profile =
    author match {
      case Some(a) => Profile(a.username, a.bio, a.image, false)
      case None => Profile("", None, None, false)
    }

  def getArticles(request: ArticleRequest): Future[ArticleMultipleArticles] =
   // println("getArticles~~")
    runner.run(
      for {
        articles <- articleStorage.getArticles(request)
        authors <- userStorage
          .getUsersByUserIds(articles.map(_.authorId))
          .map(a => a.map(t => t.id -> t).toMap)
        tags <- tagStorage.getTagsByArticles(articles.map(_.id)) //DBIO[Seq[(Long, TagV)]]
        favoriteCount <- articleStorage.countFavorites(articles.map(_.authorId))
      } yield {
        println("tags@@"+tags)
        println("favoriteCount~"+favoriteCount)
        //println("model.article~"+articles)
        output.ArticleMultipleArticles(
          articles.map(
            a => {
              SingleArticle(
                a.slug,
                a.title,
                a.description,
                a.body,
                tags.filter(_._1 == a.id).map(_._2.name),
                ISO8601(a.createdAt),
                ISO8601(a.updatedAt),
                false,
                favoriteCount
                  .map(a=> a._1 -> a._2)
                  .toMap
                  .get(a.id)
                  .getOrElse(0),
                convertUserToProfile(authors.get(a.authorId)))
            }),
          articles.length)
      })

  def getFeeds( userId:Long, limit:Int,offset:Int):Future[ArticleMultipleArticles] ={
    println("getFeeds~~")
    runner.run(
      for {
        articles <- articleStorage.getArticlesByFollowees(userId,limit,offset)
        favorites <- articleStorage
          .isFavoriteArticleIds(userId,articles.map(_.authorId))
        favoriteCount <- articleStorage.countFavorites(articles.map(_.authorId))
        authors <- userStorage
          .getUsersByUserIds(articles.map(_.authorId))
          .map(a => a.map(t => t.id -> t).toMap)
        tags <- tagStorage.getTagsByArticles(articles.map(_.id))
      } yield {
        output.ArticleMultipleArticles(
          articles.map(
            a=>
              SingleArticle(
                a.slug,
                a.title,
                a.description,
                a.body,
                tags.filter(_._1==a.id).map(_._2.name),
                ISO8601(a.createdAt),
                ISO8601(a.updatedAt),
                favorites.toSet.contains(a.id),
                favoriteCount
                  .map(a=> a._1 -> a._2)
                  .toMap
                  .get(a.id)
                  .getOrElse(0),
                convertUserToProfile(authors.get(a.authorId)))),
          articles.length
        )
      }
    )
  }
}
