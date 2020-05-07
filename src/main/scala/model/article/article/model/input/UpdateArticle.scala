package model.article.article.model.input

final case class UpdateArticle(title:Option[String]=None,
                               description:Option[String]=None,
                               body:Option[String]=None)
