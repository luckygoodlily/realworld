package route

import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import service.{ArticleService, CommentService, ProfileService, TagService, UserService}

import scala.concurrent.ExecutionContext



class HttpRoute(userService: UserService,
                profileService:ProfileService,
                articleService:ArticleService,
                commentService: CommentService,
                tagService:TagService) (implicit executionContext: ExecutionContext) {

  private val usersRouter = new UserRoute(userService)
  private val profileRouter = new ProfileRoute(userService, profileService)
  private val commentRouter = new CommentRoute(commentService,userService)
  private val articleRouter = new ArticleRoute(userService, articleService,commentRouter)


  private val tagRouter = new TagRoute(tagService)


  val route: Route =
    concat(
      pathPrefix("api") {
        usersRouter.route ~
          profileRouter.route ~
           articleRouter.route ~
           tagRouter.route

      }
    )
}
