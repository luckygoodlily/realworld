package route

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import io.circe.generic.auto._
import io.circe.syntax._
import service.TagService

import scala.concurrent.ExecutionContext

class TagRoute(tagService:TagService) (implicit executionContext: ExecutionContext)
  extends FailFastCirceSupport {

  import tagService._

  val route: Route =
    concat(
      path("tags") {
        get {
          complete(getTags().map(_.asJson))
        }
      }
    )

}
