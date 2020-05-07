package route

import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import io.circe.generic.auto._
import io.circe.syntax._
import model.article.article.model.input.ArticleRequest
import model.article.article.model.input.collect.{CollectCreateArticle, CollectUpdateArticle}
import service.{ArticleService, UserService}

import scala.concurrent.ExecutionContext

class ArticleRoute(usersService: UserService,
                   articleService:ArticleService,
                   commentRoute: CommentRoute)
                  (implicit executionContext: ExecutionContext)
  extends FailFastCirceSupport{

  import articleService._
  import usersService.Authentication

  val route: Route =
    concat(
      optionalHeaderValueByName("Authorization") { Authorization =>
        path("articles") { //List Articles
          get {
            Authorization match {
              case Some(u) => {
                onSuccess(Authentication(u.split(" ")(1))) {
                  case Some(_) => {
                    parameters('tag.?, 'author.?, 'favorited.?, 'limit.as[Long].?, 'offset.as[Long].?)
                      .as(ArticleRequest) { request =>
                        complete(getArticles(request))//getArticles(request).map(_.asJson)
                      }
                  }
                  case None => complete(BadRequest -> None.asJson)
                }
              }
              case _ => {
              parameters('tag.?, 'author.?, 'favorited.?, 'limit.as[Long].?, 'offset.as[Long].?)
                .as(ArticleRequest) { request =>
                    complete(getArticles(request))
                  }
                }
              }
            }
          }
        }
    ) ~
    concat(
      headerValueByName("Authorization") { Authorization =>
        onSuccess(Authentication(Authorization.split(" ")(1))) {
          case Some(id) => {
            concat(
              path("articles") { //create new model.article
                post {
                  entity(as[CollectCreateArticle]) { order =>
                    if (order.article.title.isEmpty || order.article.description.isEmpty || order.article.body.isEmpty)
                      complete("title、description、body can't be empty")
                    else {
                      complete(createArticle(id, order.article))
                    }
                  }
                }
              },
              path("articles" / Segment) { slug => {
                concat(
                  put { // update model.article
                    entity(as[CollectUpdateArticle]) { order =>
                      complete(updateArticleBySlug(slug, id, order.article).map {
                        case Some(article) => OK -> article.asJson
                        case None => BadRequest -> None.asJson
                      })
                    }
                  },
                  delete { //delete Article
                    complete(deleteArticleBySlug(slug)) //沒要還傳，所以沒有case Some None
                  }
                )
              }},
              path("articles" / Segment / "favorite") { slug => { //Favorite Article
                concat(
                post {
                  complete(favoriteArticle(id, slug).map {
                    case Some(article) => OK -> article.asJson
                    case None => NotFound -> None.asJson
                  })
                },
                delete {
                  complete(unFavoriteArticle(id, slug).map {
                    case Some(article) => OK -> article.asJson
                    case None => NotFound -> None.asJson
                  })
                }
                )
              }},
              path( "articles" / "feed" ) { //display recent user follow
                get {
                  parameters('limit.as[Int] ? 20, 'offset.as[Int] ? 0) {
                    (limit, offset) => {
                      complete(getFeeds(id, limit, offset).map(_.asJson))
                    }
                  }
                }
              }
            )
          }
          case None => complete(BadRequest -> None.asJson)
        }
      }
    ) ~
      concat(
        path("articles" / Segment) { slug => { // get model.article by slug
          get {
            complete(getArticleBySlug(slug).map {
              case Some(article) => OK -> article.asJson
              case None => BadRequest -> None.asJson
            })
          }
        }}
      )~
      commentRoute.route

}
