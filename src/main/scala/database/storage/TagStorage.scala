package database.storage


import database.table.{ArticleTagTable, TagTable}
import model.tag.model.save.TagV
import slick.dbio.DBIO
import slick.jdbc.H2Profile.api._

import scala.concurrent.ExecutionContext.Implicits.global

trait TagStorage {
  def findTagByNames(tagNames: Seq[String]): DBIO[Seq[TagV]]

  def insertAndGet(tagVs: Seq[TagV]): DBIO[Seq[TagV]]

  def getTagsByArticle(articleId: Long): DBIO[Seq[TagV]]

  def getTagsByArticles(articleIds: Seq[Long]): DBIO[Seq[(Long, TagV)]]

  def getTags(): DBIO[Seq[String]]

}

class JdbcTagStorage extends TagStorage {
  // create model.article
  def findTagByNames(tagNames: Seq[String]): DBIO[Seq[TagV]] =
    TagTable.tags.filter(_.name inSet tagNames).result

  def insertAndGet(tagVs: Seq[TagV]): DBIO[Seq[TagV]] =
    TagTable.tags
      .returning(TagTable.tags.map(_.id))
      .++=(tagVs) // Insert multiple rows, including AutoInc columns.
      .flatMap(ids => TagTable.tags.filter(_.id inSet ids).result)

  def getTagsByArticle(articleId: Long): DBIO[Seq[TagV]] = {
    println("getTagsByArticle: "+articleId)
    ArticleTagTable.articleTags
      .join(TagTable.tags) // Join two queries with a cross join or inner join
      .on(_.tagId === _.id) //ArticleTagTable vs TagTable
      .filter(_._1.articleId === articleId)
      .map(_._2)
      .result
  }

  def getTagsByArticles(articleIds: Seq[Long]): DBIO[Seq[(Long, TagV)]] =
    ArticleTagTable.articleTags
      .join(TagTable.tags)
      .on(_.tagId === _.id)
      .filter(_._1.articleId inSet articleIds)
      .map(a => (a._1.articleId, a._2))
      .result

  def getTags(): DBIO[Seq[String]] = TagTable.tags.map(_.name).result



}