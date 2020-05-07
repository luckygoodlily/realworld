package database.table

import model.users.model.save.SaveUser
import slick.jdbc.H2Profile.api._

private [database] object UserTable {
  class Users(tag: Tag) extends Table[SaveUser](tag, "users") {
    def id = column[Long]("id", O.AutoInc, O.PrimaryKey) // This is the primary key column

    def username = column[String]("username")

    def email = column[String]("email")

    def password = column[String]("password")

    def bio = column[Option[String]]("bio")

    def image = column[Option[String]]("image")

    def token = column[String]("token")

    def following = column[Boolean]("following")


    def * = (id, username, email, password, bio, image, token, following)<> ((SaveUser.apply _).tupled, SaveUser.unapply)
  }

  val users = TableQuery[Users]
  //val createUsersTableAction = testUsers.schema.createIfNotExists // Create the tables
}
