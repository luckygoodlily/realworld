package route

import akka.http.scaladsl.model.StatusCodes.{BadRequest, NotFound, OK}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import model.article.comment.model.input.collect.CollectAddComment
import model.article.comment.model.output.CommentBodyError
import model.article.comment.model.output.control.ControlCommentBodyError
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import io.circe.generic.auto._
import io.circe.syntax._
import service.{CommentService, UserService}

import scala.concurrent.ExecutionContext

class CommentRoute(commentService: CommentService, usersService: UserService)
                  (implicit executionContext: ExecutionContext)
  extends FailFastCirceSupport{

  import commentService._
  import usersService.Authentication

  val route:Route =
    concat(
      optionalHeaderValueByName("Authorization") { Authorization =>
        concat(
          path("articles" / Segment / "comments") { slug => //Get Comments
            get {
              Authorization match {
                case Some(u) => {
                  onSuccess(Authentication(u.split(" ")(1))) {
                    case Some(id) =>
                      complete(getComments(slug, Some(id)))
                    case None => complete(BadRequest -> None.asJson)
                  }
                }
                case _ =>
                  complete(getComments(slug, None))
              }
            }
          }
        )
      }
    )~
    concat(
      headerValueByName("Authorization") { Authorization =>
        onSuccess(Authentication(Authorization.split(" ")(1))) {
          case Some(id) => {
            concat(
              path("articles" / Segment / "comments") { slug => { //Add Comments to an model.article
                post {
                  entity(as[CollectAddComment]) { order =>
                    if (order.comment.body.isEmpty)
                      complete(422 -> ControlCommentBodyError(CommentBodyError("can't be empty" :: Nil)))
                    else {
                      complete(createComment(slug, id, order).map {
                        case Some(x) => OK -> x.asJson
                        case None => NotFound -> None.asJson
                      })
                    }
                  }
                }
              }},
              path("articles" / Segment / "comments" / IntNumber) { (slug, commentId) => //delete comments
                delete {
                  complete(deleteComment(slug, commentId)) //沒要還傳，所以沒有case Some None
                }
              }
            )
          }
          case None => complete(BadRequest -> None.asJson)
        }
      }
    )
}
