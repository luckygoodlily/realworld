package database.storage


import database.table.{ArticleTable, ArticleTagTable, CommentTable, FavoriteTable, ProfileTable, TagTable, UserTable}
import model.article.article.model.input.ArticleRequest
import model.article.article.model.saveDataModel.{ArticleTag, Favorite, SaveArticleObject}
import slick.dbio.{DBIO, DBIOAction}
import slick.jdbc.H2Profile.api._

import scala.concurrent.ExecutionContext.Implicits.global
trait ArticleStorage {
  def createArticle(newArticle: SaveArticleObject): DBIO[SaveArticleObject]

  def insertArticleTag(atags: Seq[ArticleTag]): DBIO[Seq[ArticleTag]]

  def isFavoriteArticleId(userId: Long, articleId: Long): DBIO[Seq[Long]]
  def isFavoriteArticleIds(userId: Long, articleIds: Seq[Long]): DBIO[Seq[Long]]

  def countFavorite(articleId: Long): DBIO[Int]
  def countFavorites(articleIds: Seq[Long]): DBIO[Seq[(Long, Int)]]

  def getArticleBySlug(slug: String): DBIO[Option[SaveArticleObject]]

  def updateArticle(article: SaveArticleObject): DBIO[SaveArticleObject]

  def deleteArticleBySlug(slug: String): DBIO[Unit]

  def favoriteArticle(userId: Long, articleId: Long): DBIO[Favorite]

  def unFavoriteArticle(userId: Long, articleId: Long): DBIO[Int]

  def getArticles(pageRequest: ArticleRequest): DBIO[Seq[SaveArticleObject]]

  def getArticlesByFollowees(userId: Long,
                             limit: Int,
                             offset: Int): DBIO[Seq[SaveArticleObject]]
}

class JdbcArticleStorage extends ArticleStorage {
  // create Article
  def createArticle(newArticle: SaveArticleObject): DBIO[SaveArticleObject] =
    (ArticleTable.articles returning ArticleTable.articles.map(_.id) into (
      (u, id) => u.copy(id=id))) += newArticle
    //(articles returning articles)+=model.article XX
   //(favorites returning favorites.map(_.id) into (
   //      (favorite, id) => favorite.copy(id = id)) +=
   //      Favorite(0, userId, articleId)
  //(testUsers returning testUsers.map(_.id) into ((u, id) => u.copy(id))) += user

  def insertArticleTag(atags: Seq[ArticleTag]): DBIO[Seq[ArticleTag]] =
    ArticleTagTable.articleTags
      .returning(ArticleTagTable.articleTags.map(_.id))
      .++=(atags)
      .flatMap(ids => ArticleTagTable.articleTags.filter(_.id inSet ids).result)

  def isFavoriteArticleId(userId: Long, articleId: Long): DBIO[Seq[Long]] =
    FavoriteTable.favorites.filter(_.userId === userId)
             .filter(_.favoritedId === articleId)
             .map(_.favoritedId)
             .result

  def isFavoriteArticleIds(userId: Long, articleIds: Seq[Long]): DBIO[Seq[Long]] =
    FavoriteTable.favorites.filter(_.userId===userId)
             .filter(_.favoritedId inSet articleIds)
             .map(_.favoritedId)
             .result

  def countFavorite(articleId: Long): DBIO[Int] =
    FavoriteTable.favorites.filter(_.favoritedId === articleId)
             .length
             .result

  def countFavorites(articleIds:Seq[Long]):DBIO[Seq[(Long,Int)]]=
    FavoriteTable.favorites.filter(_.favoritedId inSet articleIds)
             .groupBy(_.favoritedId)
             .map({case (a, q)=>(a,q.size)})
             .result

  // get model.article
  def getArticleBySlug(slug: String): DBIO[Option[SaveArticleObject]] =
    ArticleTable.articles.filter(_.slug === slug).result.headOption

  def updateArticle(article: SaveArticleObject): DBIO[SaveArticleObject] =
    ArticleTable.articles
      .filter(_.id === article.id)
      .update(article)
      .flatMap(_ => ArticleTable.articles.filter(_.id === article.id).result.head)


  def deleteArticleBySlug(slug: String): DBIO[Unit] = {
    val deleteA = ArticleTagTable.articleTags
      .filter(_.articleId in ArticleTable.articles.filter(a => a.slug === slug).map(_.id))
      .delete
    val deleteB = FavoriteTable.favorites
      .filter(_.favoritedId in ArticleTable.articles.filter(a => a.slug === slug).map(_.id))
      .delete
    val deleteC = CommentTable.comments
      .filter(_.articleId in ArticleTable.articles.filter(a => a.slug === slug).map(_.id))
      .delete
    val deleteD = ArticleTable.articles.filter(_.slug === slug).delete
    println("delete sucessful")
    DBIOAction.seq(deleteA, deleteB,deleteC, deleteD)
  }

  def favoriteArticle(userId: Long, articleId: Long): DBIO[Favorite] =
    FavoriteTable.favorites returning FavoriteTable.favorites.map(_.id) into (
      (favorite, id) => favorite.copy(id)) +=
      Favorite(0, userId, articleId) //insertFavorites 0 -1都沒差因為是自動的
    //(favorites returning favorites)+=Favorite(-1, userId, articleId)

  def unFavoriteArticle(userId: Long, articleId: Long): DBIO[Int] =
    FavoriteTable.favorites
      .filter(a => a.userId === userId && a.favoritedId === articleId)
      .delete

  def getArticles(pageRequest: ArticleRequest): DBIO[Seq[SaveArticleObject]] =
    //println("##getArticles"+pageRequest.author+" "+pageRequest.tag+" "+pageRequest.favorited)
    ArticleTable.articles.join(UserTable.users).on(_.authorId === _.id) // (articles===testUsers)
      .filter { st =>
        pageRequest.author.fold(true.bind)(st._2.username === _) //_1 model.article _2 testUsers
      }
      .filter { st =>
        pageRequest.tag.fold(true.bind) { tag =>
          st._1.id in ArticleTagTable.articleTags // st._1 articles
            .join(TagTable.tags).on(_.tagId === _.id)
            .filter(_._2.name === tag)
            .map(_._1.articleId)
        }
      }
      .filter { st =>
        pageRequest.favorited.fold(true.bind) { favoritedUsername =>
          st._1.id in UserTable.users.filter(_.username === favoritedUsername).map(_.id)
        }
      }
      .map(_._1)
      .drop(pageRequest.offset.getOrElse(0L))
      .take(pageRequest.limit.getOrElse(20L))
      .result

  def getArticlesByFollowees(userId: Long,
                             limit: Int,
                             offset:Int): DBIO[Seq[SaveArticleObject]]= {
    println("getArticlesByFollowees: "+userId+" "+limit+" "+offset)
    ProfileTable.followers
      .join(ArticleTable.articles).on(_.followeeId === _.authorId)
      .filter(a => a._1.id === userId)
      .drop(offset)
      .take(limit)
      .map(_._2)
      .result
  }

}
