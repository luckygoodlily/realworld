package realworld.testUsers


import database.storage.UserStorage
import realworld.DBServiceTest
import realworld.allTestValue.Users
import service.UserService

import scala.concurrent.ExecutionContext.Implicits.global
import slick.dbio.DBIO

import model.users.model.input.{LoginUser, RegisterUser}
class UserServiceTest extends DBServiceTest {


  "register service" should "create an user and return" in new Context {
    val testRegister=RegisterUser("lily","123@rrr.rrr","kkkk")

    (userStorage.register)(*) answers DBIO.successful(Users.normalUser)

    awaitFuture( for {
      user <- userService.register(testRegister)
    } yield {
      println("user~~"+ user)
      user should be (Users.normalResult)
    })
  }

  "login service" should "login sucessful" in new Context {
    val testLogin=LoginUser("123@rrr.rrr","kkkk")
    (userStorage.findUserByEmail)(*,*) answers DBIO.successful(Some(Users.normalUser))

    awaitFuture( for {
      user <- userService.login(testLogin)
    } yield {

      user should be (Some(Users.normalResult))
    })
  }

  it should "login fail" in new Context {

    val testLogin=LoginUser("lily","66666")
    (userStorage.findUserByEmail)(*,*) answers DBIO.successful(None)

    awaitFuture( for {
      user <- userService.login(testLogin)
    } yield {
      println("~~~~login~"+user)
      // user shouldBe None
      user should be (None)
    })
  }

  "Authentication" should "Authentication sucessful" in new Context {
    val testToken = "lily.token.here"
    (userStorage.findToken)(*) answers DBIO.successful(Some(Users.normalUser))

    awaitFuture( for {
      id <- userService.Authentication(testToken)
    } yield {
      id should be (Some(1))
    })
  }

  it should "Authentication fail" in new Context {
    val testToken = "kkk.token.here"
    (userStorage.findToken)(*) answers DBIO.successful(None)
    awaitFuture( for {
      id <- userService.Authentication(testToken)
    } yield {
      id should be (None)
    })
  }

  "getCurrentUser" should "getCurrentUser sucessful" in new Context {
    (userStorage.getUser)(*) answers DBIO.successful(Some(Users.normalUser))
    awaitFuture( for {
      user <- userService.getCurrentUser(1L)
    } yield {
      user should be (
        Some(Users.normalResult)
      )
    })
  }

  it should "getCurrentUser fail" in new Context {
    (userStorage.getUser)(*) answers DBIO.successful(None)

    awaitFuture( for {
      user <- userService.getCurrentUser(2L)
    } yield {
      user should be (None)
    })
  }

  "updateUser" should "updateUser sucessful" in new Context {

    (userStorage.getUser)(*) answers DBIO.successful(Some(Users.normalUser))
    (userStorage.saveUser)(*) answers DBIO.successful(Users.normalUser)

    awaitFuture( for {
      user <- userService.updateUser(1L,Users.testUpdateService)
    } yield {
      user should be (Some(Users.normalResult))
    })
  }

  "updateUser" should "updateUser fail" in new Context {
    (userStorage.getUser)(*) answers DBIO.successful(None)
    //(userStorage.saveUser)(None) answers DBIO.successful()

    awaitFuture( for {
      user <- userService.updateUser(2L,Users.testUpdateService)
    } yield {
      user should be (None)
    })
  }



  trait Context {

//    val databaseConnector = new DataBaseConnector()
//    val storageRunner = new StorageRunner(databaseConnector)
    val userStorage = mock[UserStorage] //代理動態模擬（Proxy (dynamic) mocks）
    //val userStorage = new JdbcUserStorage
    val userService = new UserService(databaseConnector, userStorage)

  }

}

