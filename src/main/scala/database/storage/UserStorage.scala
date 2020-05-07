package database.storage


import database.table.{ProfileTable, UserTable}
import database.util.DBIOOptional
import model.profile.model.save.SaveFollow
import model.users.model.save.SaveUser
import slick.dbio.Effect
import slick.sql.SqlAction
//import slick.dbio.DBIO
import slick.jdbc.H2Profile.api._

import scala.concurrent.ExecutionContext.Implicits.global

trait UserStorage{

  def register(user: SaveUser): DBIO[SaveUser]

  def findUserByEmail(email: String, password: String): DBIO[Option[SaveUser]]

  def findToken(token: String): DBIO[Option[SaveUser]]

  def getUser(userId: Long): DBIO[Option[SaveUser]]

  def saveUser(user: SaveUser): DBIO[SaveUser]

  def getUserByUsername(username: String): DBIO[Option[SaveUser]]

  def follow(userId: Long,username: String): DBIO[SaveUser]

  def unfollow(userId: Long, username: String): DBIO[SaveUser]

  def isFollowing(userId: Long, id:Option[Long]): DBIO[Boolean]

  def getUsersByUserIds(userIds: Seq[Long]): DBIO[Seq[SaveUser]]

  def followingUsers(id:Option[Long],targetUserIds:Seq[Long]):DBIO[Seq[Long]]

  def deleteAllUser():DBIO[Int]

  def userIdInitial()

  //def printAll()

}

class JdbcUserStorage extends UserStorage{
  def register(user: SaveUser): DBIO[SaveUser] =
    (UserTable.users returning UserTable.users.map(_.id) into
      ((u, id) => u.copy(id=id))) += user

  def findUserByEmail(email: String, password: String): DBIO[Option[SaveUser]] =
    UserTable.users.filter(a =>
      a.email === email && a.password === password).result.headOption

  def findToken(token: String): DBIO[Option[SaveUser]] =
    UserTable.users.filter(a => a.token === token).result.headOption

  def getUser(userId: Long): DBIO[Option[SaveUser]] =
    UserTable.users.filter(_.id === userId).result.headOption

  def saveUser(user: SaveUser): DBIO[SaveUser] = {
    println("!@!saveUser: "+user)
    (UserTable.users returning UserTable.users).insertOrUpdate(user).map(_.getOrElse(user))
//    testUsers
//      .filter(_.id === user.id)
//      .update(user)
//      .flatMap(_ => testUsers.filter(_.id === user.id).result.head)
  }

  def getUserByUsername(username: String): DBIO[Option[SaveUser]] =
    UserTable.users.filter(_.username === username).result.headOption //.result會是一個Seq[SaveUser] headOption變[Option[SaveUser]]

  def follow(userId: Long,username: String): DBIO[SaveUser] = {
    val result=UserTable.users.filter(_.username === username).result.head
    result.flatMap(
      targetUser=>
          ProfileTable.followers += SaveFollow(userId, targetUser.id))
    result
  }


  def unfollow(userId: Long,username: String): DBIO[SaveUser] = {
    val result=UserTable.users.filter(_.username === username).result.head
    result.flatMap(
      targetUser=>
         ProfileTable.followers.filter(a =>
           a.id === userId && a.followeeId === targetUser.id).delete)
    result
  }

  def isFollowing(userId: Long,id:Option[Long]): DBIO[Boolean] =
    id match {
      case Some(targetUserId) =>
        ProfileTable.followers
          .filter(m => m.id === userId && m.followeeId === targetUserId)
          .result
          .headOption
          .map(
            _.isDefined)
      case None =>
        ProfileTable.followers.filter(m => m.id === userId)
          .result.headOption.map(_.isDefined)
    }

  def getUsersByUserIds(userIds: Seq[Long]): DBIO[Seq[SaveUser]] =
    UserTable.users.filter(_.id inSet userIds).result

  def followingUsers(id:Option[Long],targetUserIds:Seq[Long]):DBIO[Seq[Long]] =
    id match {
      case Some(userId) =>
        ProfileTable.followers
        .filter(_.id === userId)
        .filter(_.followeeId inSet targetUserIds)
        .map(_.followeeId)
        .result
      case None =>
        ProfileTable.followers
        .filter(_.followeeId inSet targetUserIds)
        .map(_.followeeId)
        .result
    }

  def printAll()=
    UserTable.users.result

  def deleteAllUser():DBIO[Int]=
    UserTable.users.delete

  def userIdInitial():Unit=
    UserTable.users.drop(0)
}
