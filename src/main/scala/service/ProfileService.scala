package service

import database.storage.UserStorage

import database.util.{DBIOOptional, DataBaseConnector}
import model.profile.model.output.{Profile, control}
import model.profile.model.output.control.ControlProfile

import scala.concurrent.{ExecutionContext, Future}

class ProfileService(runner: DataBaseConnector,userStorage: UserStorage)
                    (implicit executionContext: ExecutionContext) {
  def follow(userId: Long, username: String): Future[ControlProfile] =
    runner.runInTransaction(
      (for {
        //p <- DBIOOptional(userStorage.getUserByUsername(username))
        result <- userStorage.follow(userId,username)
      } yield {
        ControlProfile(Profile(result.username, result.bio, result.image, true))
      }))

  def unfollow(userId: Long,
                username: String): Future[ControlProfile] =
    runner.runInTransaction(
      (for {
        //p <- DBIOOptional(userStorage.getUserByUsername(username))
        p <- userStorage.unfollow(userId,username)
      } yield {
        ControlProfile(Profile(p.username, p.bio, p.image, false))
      }))

  def getProfile(username: String,id:Option[Long]): Future[Option[ControlProfile]] = {
    println("getProfile~~")
    runner.run(
      (for {
        p <- DBIOOptional(userStorage.getUserByUsername(username))
        isFollowing <- DBIOOptional(userStorage.isFollowing(p.id,id).map(Some(_)))
      } yield {
        control.ControlProfile(Profile(p.username, p.bio, p.image, isFollowing))
      }).dbio)
  }

}

