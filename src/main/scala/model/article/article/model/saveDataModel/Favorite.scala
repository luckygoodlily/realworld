package model.article.article.model.saveDataModel

final case class Favorite(id: Long,
                    userId: Long,
                    favoritedId: Long) //喜歡文章的id