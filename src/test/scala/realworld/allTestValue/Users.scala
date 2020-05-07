package realworld.allTestValue

import model.profile.model.output.Profile
import realworld.testUsers.{TestRegister, TestUpdate}

import model.users.model.input.UpdateUser
import model.users.model.output.User
import model.users.model.output.control.ControlUser
import model.users.model.save.SaveUser

object Users {
  val normalUser = SaveUser(1,
    "lily",
    "123@rrr.rrr",
    "kkkk",
    None,
    None,
    "lily.token.here",
    false)

  val normalUser1 = SaveUser(1,
    "lily",
    "123@rrr.rrr",
    "kkkk",
    None,
    None,
    "lily.token.here",
    true)

  val normalUser2 = SaveUser(2,
    "peter",
    "123@rrr.rrr",
    "kkkk",
    None,
    None,
    "peter.token.here",
    false)

  val normalResult =ControlUser(
    User(username="lily",
      email="123@rrr.rrr",
      bio=None,
      image=None,
      token="lily.token.here"))

  val testUpdateService = UpdateUser(
    username=Some("lily22"),
    email=Some("bbb@gmail.com"),
    password=Some("dddd"),
    bio=Some("i am lily"),
    image=Some("gggg")
  )



  val testRegister = testRegisterFu(TestRegister(1,"lily","123@rrr.rrr","kkkk"))

  //val userResult=Some(SaveUser(1,"lily","123@rrr.rrr","kkkk",None,None,"lily.token.here",false))

  val userResultUpdate=SaveUser(1,"lily22","777@das.fdg","777",Some("bio"),Some("image"),"lily22.token.here",false)


  val testUpdate = testUpdateFu(
    TestUpdate(id=1,
      username=Some("lily22"),
      email=Some("777@das.fdg"),
      password=Some("777"),
      bio=Some("bio"),
      image=Some("image"))
    , SaveUser(1,"lily","123@rrr.rrr","kkkk",None,None,"lily.token.here",false))

  def toBeToken(username:String):String=
    username+".token.here"

  def testRegisterFu(testUser:TestRegister):SaveUser=
    SaveUser(
      id = testUser.id,
      username = testUser.username,
      email = testUser.email,
      password = testUser.password,
      bio = None,
      image = None,
      token = toBeToken(testUser.username),
      following = false
    )

  //println("test~~~~"+testUpdate)

  def testUpdateFu(testUser:TestUpdate,user:SaveUser):SaveUser= {
    var token: String = ""
    if (testUser.username.isDefined)
      token = testUser.username.get+ ".token.here"
    else token = user.token
    SaveUser(
      id = user.id,
      username = testUser.username.getOrElse(user.username),
      email = testUser.email.getOrElse(user.email),
      password = testUser.password.getOrElse(user.password),
      bio = testUser.bio.orElse(user.bio),
      image = testUser.image.orElse(user.image),
      token = token,
      following = user.following
    )
  }

  val testUser1 = testRegisterFu(
    TestRegister(1, "username-1", "username-email-1", "user-password-1"))
  val testUser2 = testRegisterFu(
    TestRegister(2, "username-2", "username-email-2", "user-password-2"))


}
