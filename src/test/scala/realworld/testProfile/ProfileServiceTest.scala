package realworld.testProfile

import database.storage.UserStorage
import org.mockito.scalatest.IdiomaticMockito
import realworld.DBServiceTest
import realworld.allTestValue.{Profiles, Users}
import service.ProfileService

import scala.concurrent.ExecutionContext.Implicits.global
import slick.dbio.DBIO
class ProfileServiceTest extends DBServiceTest with IdiomaticMockito{

  "follow" should "return profile with follow true" in new Context {
    //(userStorage.getUserByUsername _)(*) answers DBIO.successful(Some(Users.normalUser))
    (userStorage.follow _)(*,*) answers DBIO.successful(Users.normalUser)

    awaitFuture(for {
      profile <- profileService.follow(1L,Users.normalUser.username)
    } yield {
      profile should be (Profiles.profileResult)
    })
  }

//  it should "return profile fail" in new Context {
//    //(userStorage.getUserByUsername _)(*) answers DBIO.successful(None)
//    (userStorage.follow _)(*,*) answers DBIO.successful(0) //fail
//
//    awaitFuture(for {
//      profile <- profileService.follow(1L,"kkkk")
//    } yield {
//
//      profile should be (None)
//    })
//  }

  "unfollow" should "return profile with follow false" in new Context {
   // (userStorage.getUserByUsername _)(*) answers DBIO.successful(Some(Users.normalUser))
    (userStorage.unfollow _)(*,*) answers DBIO.successful(Users.normalUser)

    awaitFuture(for {
      profile <- profileService.unfollow(1L,Users.normalUser.username)
    } yield {
      profile should be (Profiles.profileResult1)
    })
  }

  "getProfile" should "return profile by id" in new Context {
    (userStorage.getUserByUsername _)(*) answers DBIO.successful(Some(Users.normalUser))
    (userStorage.isFollowing)(*,*) answers DBIO.successful(true)

    awaitFuture(for {
      profile <- profileService.getProfile("lily",Some(1L))
    } yield {
      profile should be (Profiles.profileResult)
    })
  }


  trait Context{
    //val storageRunner = new StorageRunner(databaseConnector)
    val userStorage = mock[UserStorage]
    val profileService = new ProfileService(databaseConnector, userStorage)

  }
}
