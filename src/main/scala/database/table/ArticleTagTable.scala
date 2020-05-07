package database.table

import model.article.article.model.saveDataModel.ArticleTag
import slick.jdbc.H2Profile.api._

private [database] object ArticleTagTable {
  class ArticleTags(tag: Tag) extends Table[ArticleTag](tag, "articles_tags") {
    def id = column[Long]("id", O.AutoInc, O.PrimaryKey)
    def articleId = column[Long]("article_id")
    def tagId = column[Long]("tag_id")

    def * =
      (id, articleId, tagId) <> ((ArticleTag.apply _).tupled, ArticleTag.unapply)
  }

  val articleTags = TableQuery[ArticleTags]
  //val createArticleTagsTableAction = articleTags.schema.createIfNotExists
}
