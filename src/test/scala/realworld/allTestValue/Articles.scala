package realworld.allTestValue


import database.util.ISO8601
import model.article.article.model.input.{ArticleRequest, CreateArticle, UpdateArticle}
import model.article.article.model.output.{ArticleMultipleArticles, SingleArticle, control}
import model.article.article.model.output.control.ControlSingleArticle
import model.article.article.model.saveDataModel.{ArticleTag, Favorite, SaveArticleObject}
import model.tag.model.save.TagV

object Articles {
  val normalArticle = SaveArticleObject(
    id=1,
    slug="slug",
    title="title",
    description="description",
    body="body",
    authorId=1,
    createdAt=Date.currentWhenInserting,
    updatedAt=Date.currentWhenInserting,
    favorited=false,
    0)

  val normalArticle1 = SaveArticleObject(
    id=1,
    slug="slug",
    title="title",
    description="description",
    body="body",
    authorId=1,
    createdAt=Date.currentWhenInserting,
    updatedAt=Date.currentWhenInserting,
    favorited=false,
    1)

  val testArticle = CreateArticle("title","description","body",Seq("1","2"))
  val tags=Seq(TagV(id=1, name="1"),TagV(id=2, name="2"))
  val articleTag =Seq(ArticleTag(1, 1,1),ArticleTag(2, 1,2))
  val resultArticle = Some(control.ControlSingleArticle(
    SingleArticle(
      slug = "slug",
      title = "title",
      description = "description",
      body = "body",
      tagList = Seq("1", "2"),
      createdAt = ISO8601(normalArticle.createdAt),
      updatedAt = ISO8601(normalArticle.updatedAt),
      favorited = false,
      favoritesCount = 0,
      author = Profiles.profiletest
    )))
  val updateTest=UpdateArticle(title= Some("new title"),
  description= None,
  body= Some("new body"))

  val title = "title"
  val slug = "slug"

  val favorite=Favorite(id=1,
    userId=1,
    favoritedId=1)


  val favoriteResultArticle = Some(ControlSingleArticle(
    SingleArticle(
      slug = "slug",
      title = "title",
      description = "description",
      body = "body",
      tagList = Seq("1", "2"),
      createdAt = ISO8601(normalArticle.createdAt),
      updatedAt = ISO8601(normalArticle.updatedAt),
      favorited = true,
      favoritesCount = 1,
      author = Profiles.profiletest
    )))
  val tagv=TagV(id=1, name="1")
  val tagv2=TagV(id=2, name="2")
  val getTagsResult =Seq((1L,tagv),(1L,tagv2)) //articleId , tagV

  val ArticleAskTest =ArticleRequest(tag=Some("1"),
    author= None,
    favorited= None,
    limit= None,
    offset= None)

  val multipleResult=ArticleMultipleArticles(Seq(resultArticle.get.article),1)

  val updateDescription = "update description"
}
