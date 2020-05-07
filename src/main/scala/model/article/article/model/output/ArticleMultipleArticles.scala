package model.article.article.model.output

final case class ArticleMultipleArticles(articles:Seq[SingleArticle],
                                   articlesCount:Int)
