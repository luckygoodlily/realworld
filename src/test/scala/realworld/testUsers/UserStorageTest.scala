package realworld.testUsers

import database.storage.JdbcUserStorage
import org.scalatest.concurrent.PatienceConfiguration.Timeout
import realworld.allTestValue.Users
import realworld.DBServiceTest
import scala.concurrent.duration._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.language.postfixOps
class UserStorageTest extends DBServiceTest {


  "addUser" should "create an user and return" in new Context {
    newdbRun(_.run(userStorage.register(Users.testRegister)))
      .futureValue(Timeout(60 seconds)) should be(Users.normalUser)

//    awaitFuture(
//      newdbRun { runner =>
//        runner.run(for {
//          user <- userStorage.register(Users.testRegister)
//          //delete <- userStorage.deleteAllUser()
//        } yield {
//          println("~~~user" + user)
//          user should be(Users.normalUser)
//        })
//      })
  }

  "update" should "update an user return" in new Context {
    newdbRun(_.run{
      userStorage.register(Users.testRegister).flatMap(_=>
      userStorage.saveUser(Users.testUpdate))}).
      futureValue(Timeout(60 seconds)) should not be (Some(Users.userResultUpdate))

//    awaitFuture(
//      newdbRun { runner =>
//        runner.run(for {
//          _ <- userStorage.register(Users.testRegister)
//          user <- userStorage.saveUser(Users.testUpdate)
//          //_<- userStorage.deleteAllUser()
//          table <- userStorage.printAll()
//        } yield {
//          println("usertable~~" + table)
//          user should not be (Some(Users.userResultUpdate))
//        })
//      })
  }

  "login" should "login an user return" in new Context {
//    newdbRun(_.run{
//      userStorage.register(Users.testRegister)
//      userStorage.findUserByEmail("123@rrr.rrr", "kkkk")})
//      .futureValue(Timeout(60 seconds)) should be(Some(Users.normalUser))

    awaitFuture(
    newdbRun { runner =>
        runner.run(for {
          _ <- userStorage.register(Users.testRegister)
          user <- userStorage.findUserByEmail("123@rrr.rrr", "kkkk")
          //_ <- userStorage.deleteAllUser()
        } yield {
          println("login~~" + user)
          user should be(Some(Users.normalUser))
        })
    })
  }

  "authentication" should "authentication an user return" in new Context {
//    newdbRun(_.run{
//      userStorage.register(Users.testRegister)
//      userStorage.findToken("lily.token.here")
//    }).futureValue(Timeout(60 seconds)) should be(Some(Users.normalUser))

    awaitFuture(
      newdbRun { runner =>
        runner.run(for {
          _ <- userStorage.register(Users.testRegister)
          user <- userStorage.findToken("lily.token.here")
        } yield {
          println("Auth"+user)
          user should be(Some(Users.normalUser))
        })
    })
  }


  it should "authentication fail" in new Context {
//    newdbRun(_.run{
//      userStorage.register(Users.testRegister)
//      userStorage.findToken("lily22.token.here")
//    }).futureValue(Timeout(60 seconds)) should be (None)

    awaitFuture(
      newdbRun { runner =>
        runner.run(for {
          _ <- userStorage.register(Users.testRegister)
          user <- userStorage.findToken("lily22.token.here")
        } yield {
          println("Auth fail"+user)
          user should be(None)
        })
    })
  }

  "getCurrentUser" should "getCurrentUser successful" in new Context {
//    newdbRun(_.run{
//      userStorage.register(Users.testRegister)
//      userStorage.getUser(1)
//    }).futureValue(Timeout(60 seconds)) should be (Some(Users.normalUser))

    awaitFuture(
    newdbRun { runner =>
        runner.run(for {
          _ <- userStorage.register(Users.testRegister)
          user <- userStorage.getUser(1)
        } yield {
          println("getCurrentUser:"+user)
          user should be(Some(Users.normalUser))
        })
    })
  }

  it should "getCurrentUser fail" in new Context {
//    newdbRun(_.run{
//      userStorage.register(Users.testRegister)
//      userStorage.getUser(0)
//    }).futureValue(Timeout(60 seconds)) should be(None)

    awaitFuture(
    newdbRun { runner =>
        runner.run(for {
          _ <- userStorage.register(Users.testRegister)
          user <- userStorage.getUser(0)
        } yield {
          println("getCurrentUser fail:"+user)
          user should be(None)
        })
    })
  }

  "getUserByUsername" should "return profile by id" in new Context {
    awaitFuture(
      newdbRun { runner =>
        runner.run(for {
          _ <- userStorage.register(Users.testUser1)
          user <- userStorage.register(Users.testUser2)
          profile <- userStorage.getUserByUsername(Users.testUser2.username)

        } yield {
          println("profile~~ " + profile)
          profile shouldBe (Some(user))
        })
      })
  }

  "follow" should "success" in new Context {
    newdbRun(_.run{
      userStorage.follow(Users.testUser2.id,Users.testUser1.username)
    }).futureValue(Timeout(60 seconds)) should be(1)

//    awaitFuture(
//      newdbRun { runner =>
//        runner.run(for {
//          user1 <- userStorage.register(Users.testUser1)
//          user2 <- userStorage.register(Users.testUser2)
//          successFlag <- userStorage.follow(user1.id, user2.id)
//        } yield {
//          println("user1" + user1)
//          println("user2" + user2)
//          successFlag should be(1)
//        })
//      })
  }

  "isFollowing" should "return true" in new Context {

    awaitFuture(
      newdbRun { runner =>
        runner.run(for {
          _ <- userStorage.follow(Users.testUser1.id,Users.testUser2.username )
          isFollowing <- userStorage.isFollowing(Users.testUser1.id, Some(Users.testUser2.id))
        } yield isFollowing should be(true))
      })
  }

  it should "return false" in new Context {
    awaitFuture(
      newdbRun { runner =>
        runner.run(for {
          _ <- userStorage.follow(Users.testUser1.id,Users.testUser2.username)
          isFollowing <- userStorage.isFollowing(Users.testUser2.id, Some(Users.testUser1.id))
        } yield isFollowing should be(false))
      })
  }

  trait Context{
    val userStorage= new JdbcUserStorage()
  }

}
