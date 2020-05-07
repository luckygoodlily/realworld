package route

import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import io.circe.generic.auto._
import io.circe.syntax._
import service.{ProfileService, UserService}

import scala.concurrent.ExecutionContext

class ProfileRoute(usersService: UserService,profileService:ProfileService)
                  (implicit executionContext: ExecutionContext)extends FailFastCirceSupport {

  import profileService._
  import usersService.Authentication

  val route: Route =
    concat(
      pathPrefix("profiles" / Segment) { username =>
        optionalHeaderValueByName("Authorization") { Authorization =>
          get {
            Authorization match { // oldAuthentication optional
              case Some(u) => {
                //val authResult = Authentication(u.split(" ")(1))
                onSuccess(Authentication(u.split(" ")(1))) {
                  case Some(id) => {
                    complete(getProfile(username,Some(id)).map {
                      case Some(user) => OK -> user.asJson
                      case None => BadRequest -> None.asJson
                    })
                  }
                  case None => complete(BadRequest -> None.asJson)
                }
              }
              case _ => {
                complete(getProfile(username,None).map {
                  case Some(user) => OK -> user.asJson
                  case None => BadRequest -> None.asJson
                })
              }
            }
          }
        }
      }
    ) ~
      concat(
        path("profiles" / Segment / "follow") { username =>
          headerValueByName("Authorization") { Authorization =>
            onSuccess(Authentication(Authorization.split(" ")(1))) {
            case Some(id) => {
              concat(
                post {
                  complete(follow(id, username)
                    //case Some(user) => OK -> user.asJson
                    //case None => BadRequest -> None.asJson
                  ) // SetFollowUser:SaveUser
                },
                delete {
                  complete(follow(id, username))
                }
              )

            }
            case None => complete(BadRequest -> None.asJson)
          }
          }
        }
      )
}
