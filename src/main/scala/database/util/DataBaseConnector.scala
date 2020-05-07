package database.util

import database.table.{ArticleTable, ArticleTagTable, CommentTable, FavoriteTable, ProfileTable, TagTable, UserTable}
import slick.dbio.DBIO
import slick.jdbc.H2Profile.api._

import scala.concurrent.Future


class DataBaseConnector(val db:Database) {

  //val db = Database.forConfig("h2mem1")
  val table = ((UserTable.users.schema
    ++ ProfileTable.followers.schema
    ++ ArticleTable.articles.schema
    ++ TagTable.tags.schema
    ++ ArticleTagTable.articleTags.schema
    ++ FavoriteTable.favorites.schema
    ++ CommentTable.comments.schema
    ))
  //db.run(table.createIfNotExists)


  def createTable: Future[Unit] =
    db.run(table.createIfNotExists)

  def deleteTable: Future[Unit] =
    db.run(table.dropIfExists)

  def run[T](actions: DBIO[T]): Future[T] =
    db.run(actions)
  //get db data

  def runInTransaction[T](action: DBIO[T]): Future[T] =
    db.run(action.transactionally)

}
