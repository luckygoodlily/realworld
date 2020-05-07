package service

import database.storage.UserStorage
import database.util.{DBIOOptional, DataBaseConnector}
import model.article.article.model.input.UserRegistration
import model.users.model.input.{LoginUser, RegisterUser, UpdateUser}
import model.users.model.output.{User, control}
import model.users.model.output.control.ControlUser
import model.users.model.save.SaveUser

import scala.concurrent.{ExecutionContext, Future}

class UserService(runner: DataBaseConnector, userStorage: UserStorage)
                 (implicit executionContext: ExecutionContext) { // 處理資料
  val token: String = ".token.here"

  def register(userRegistration: RegisterUser): Future[ControlUser] =
    runner.runInTransaction(
      for {
        a <- userStorage.register(
          UserRegistration(userRegistration.username,
            userRegistration.password,
            userRegistration.email,
            userRegistration.username + token).create())
      } yield {
        println("register" + a)
        ControlUser(
          User(
            username = a.username,
            email = a.email,
            bio = a.bio,
            image = a.image,
            token = a.token))
      })


  def login(order:LoginUser): Future[Option[ControlUser]] = {
    println("LoginUser~~: "+order)
    runner.run(
      (for {
        user <- DBIOOptional(userStorage.findUserByEmail(order.email, order.password))
      } yield {
        println("!!!login: "+user)
        control.ControlUser(
          User(
            username=user.username,
            email=user.email,
            token=user.token,
            //token=encodeToken(user.id),
            bio=user.bio,
            image=user.image))
      }).dbio)
  }

  def Authentication(testToken: String): Future[Option[Long]]=
    //val temp: Option[SaveUser] = FetchNowUser(testToken)
    // if (temp != None) true
    // else false
    runner.run(
      (for {
        user <- DBIOOptional(userStorage.findToken(testToken))
      } yield {
        println("Authentication: "+user)
        user.id
      }).dbio)

  def getCurrentUser(id: Long): Future[Option[ControlUser]] =
    //println("getCurrentUser~~: " + id)
    runner.run(
      (for {
        user <- DBIOOptional(userStorage.getUser(id))
      } yield {
        control.ControlUser(
          User(
            username=user.username,
            email=user.email,
            //token=encodeToken(user.id),
            token=user.token,
            bio=user.bio,
            image=user.image))
      }).dbio)

  def updateUser(id: Long, userUpdate: UpdateUser): Future[Option[ControlUser]] =
    runner.runInTransaction(
      (for {
        u <- DBIOOptional(userStorage.getUser(id))
        user <- DBIOOptional(
          userStorage.saveUser(userUpdate.merge(u)).map(Some(_)))
      } yield {
        println("###user"+user)
        control.ControlUser(
          User(
            username=user.username,
            email=user.email,
            token=user.token,
            bio=user.bio,
            image=user.image))
      }).dbio)

}
