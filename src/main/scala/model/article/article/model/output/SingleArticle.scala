package model.article.article.model.output

import model.profile.model.output.Profile

final case class SingleArticle(slug: String,
                               title: String,
                               description: String,
                               body: String,
                               tagList: Seq[String],
                               createdAt: String,
                               updatedAt: String,
                               favorited: Boolean,
                               favoritesCount: Int,
                               author: Profile)
