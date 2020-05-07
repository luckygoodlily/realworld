package model.article.article.model.saveDataModel

import java.sql.Timestamp

final case class SaveArticleObject(id: Long,
                                   slug: String,
                                   title: String,
                                   description: String,
                                   body: String,
                                   authorId: Long,
                                   createdAt: Timestamp,
                                   updatedAt: Timestamp,
                                   favorited: Boolean,
                                   favoritesCount: Int)
//author: Profile)//SaveAuthor的profile
