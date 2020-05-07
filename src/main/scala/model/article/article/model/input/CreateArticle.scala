package model.article.article.model.input

import java.sql.Timestamp
import java.util.Date

import model.article.article.model.saveDataModel.SaveArticleObject


final case class CreateArticle(title: String,
                               description: String,
                               body: String,
                               tagList:Seq[String]){
  def create(authorId: Long): SaveArticleObject = {
    SaveArticleObject(
      id = 0,
      slug = model.article.article.model.toBeSlug(title),
      title = title,
      description = description,
      body = body,
      authorId=authorId,
      createdAt=new Timestamp((new Date).getTime),
      updatedAt=new Timestamp((new Date).getTime),
      favorited= false,
      favoritesCount= 0
    )
  }

}

