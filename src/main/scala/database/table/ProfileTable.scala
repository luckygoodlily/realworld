package database.table

import model.profile.model.save.SaveFollow
import slick.jdbc.H2Profile.api._

private [database] object ProfileTable {
  class Followers(tag: Tag) extends Table[SaveFollow](tag, "followers") {
    def id = column[Long]("id") //user
    def followeeId= column[Long]("followeeId") //user follower的user

    def * = (id, followeeId)<> ((SaveFollow.apply _).tupled, SaveFollow.unapply)
  }

  val followers = TableQuery[Followers]
  //val createFollowersTableAction = followers.schema.createIfNotExists // Create the tables
}
