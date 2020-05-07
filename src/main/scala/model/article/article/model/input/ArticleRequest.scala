package model.article.article.model.input

final case class ArticleRequest(tag: Option[String],
                          author: Option[String],
                          favorited: Option[String],
                          limit: Option[Long],
                          offset: Option[Long])
