package route

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import io.circe.generic.auto._
import io.circe.syntax._
import service.UserService

import model.users.model.input.collect.{CollectLoginUser, CollectRegisterUser, CollectUpdateUser}

import scala.concurrent.ExecutionContext

class UserRoute(usersService: UserService)
              (implicit executionContext: ExecutionContext)extends FailFastCirceSupport {// user path complete的處理

//  implicit val saveUserFormat = jsonFormat8(SaveUser)
//  implicit val userFormat = jsonFormat5(User)
//  implicit val loginFormat = jsonFormat2(LoginUser)
//  implicit val collectLoginFormat = jsonFormat1(CollectLoginUser)
//  implicit val registerFormat = jsonFormat3(RegisterUser)//
//  implicit val controlUserFormat = jsonFormat1(ControlUser)
//  implicit val userRegistrationFormat = jsonFormat4(UserRegistration)
//  implicit val collectRegisterFormat = jsonFormat1(CollectRegisterUser)
//  implicit val updateUserFormat = jsonFormat6(UpdateUser)
//  implicit val collectUpdateUserFormat = jsonFormat1(CollectUpdateUser)

  import StatusCodes._
  import usersService._

  val route:Route =
    concat(
        //val authResult= Authentication(Authorization.split(" ")(1))
      path("user") {
        headerValueByName("Authorization") { Authorization =>
          onSuccess(Authentication(Authorization.split(" ")(1))) {
            case Some(id) => {
              concat( // pathEndOrSingleSlash
                get { //Get Current User
                  complete(getCurrentUser(id).map {
                    case Some(user) =>
                      OK -> user.asJson
                    case None =>
                      BadRequest -> None.asJson
                  })
                },
                put { //update user data
                  entity(as[CollectUpdateUser]) { order =>
                    println("update: " + order)
                    complete(updateUser(id, order.user).map {
                      case Some(user) =>
                        OK -> user.asJson
                      case None =>
                        BadRequest -> None.asJson
                    })
                  }
                }
              )
            }
            case None => complete(BadRequest -> None.asJson) //reject
          }
        }
      }
    )~
    concat(
      path("users" / "login") { // login
        post {
          entity(as[CollectLoginUser]) { order =>
            if (order.user.email.isEmpty || order.user.password.isEmpty)
              complete("email、password can't be empty")
            else {
              println("~~input"+order)
              complete(
                login(order.user).map {
                  case Some(user) => OK -> user.asJson
                  case None => BadRequest -> None.asJson
                })
            }
          }
        }
      },
      path("users") { // register
        post {
          entity(as[CollectRegisterUser]) { order =>
            if (order.user.email.isEmpty || order.user.username.isEmpty || order.user.password.isEmpty)
              complete("email、username、password can't be empty")
            else {
              complete(register(order.user))
//              complete(register(order.user).map { user =>
//                user.asJson
//              })
            }
          }
        }
      }
    )
}
