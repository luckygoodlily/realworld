package database.table

import java.sql.Timestamp
import java.util.Date

import model.article.article.model.saveDataModel.SaveArticleObject
import slick.jdbc.H2Profile.api._
import slick.lifted.TableQuery

private [database] object ArticleTable {

  class Articles(tag: Tag) extends Table[SaveArticleObject](tag, "articles") {
    def currentWhenInserting = new Timestamp((new Date).getTime)

    def id = column[Long]("id", O.AutoInc, O.PrimaryKey)

    def slug = column[String]("slug")

    def title = column[String]("title")

    def description = column[String]("description")

    def body = column[String]("body")

    def authorId = column[Long]("author_id")

    def createdAt =
      column[Timestamp]("created_at", O.Default(currentWhenInserting))

    def updatedAt =
      column[Timestamp]("updated_at", O.Default(currentWhenInserting))

    def favorited = column[Boolean]("favorited")

    def favoritesCount = column[Int]("favoritesCount")

    def * =
      (id, slug, title, description, body, authorId, createdAt, updatedAt, favorited, favoritesCount) <> ((SaveArticleObject.apply _).tupled, SaveArticleObject.unapply)
  }

  val articles = TableQuery[Articles]
  //val createArticlesTableAction = articles.schema.createIfNotExists // Create the tables
}
