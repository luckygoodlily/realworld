package database.table

import model.article.article.model.saveDataModel.Favorite
import slick.jdbc.H2Profile.api._

private [database] object FavoriteTable {
  class Favorites(tag: Tag) extends Table[Favorite](tag, "favorite") {
    def id = column[Long]("id", O.AutoInc, O.PrimaryKey)
    def userId = column[Long]("user_id")
    def favoritedId = column[Long]("favorited_id")

    def * =
      (id, userId, favoritedId) <> ((Favorite.apply _).tupled, Favorite.unapply)
  }

  val favorites = TableQuery[Favorites]
  //val createFavoritesTableAction = favorites.schema.createIfNotExists
}
