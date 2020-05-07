package model.article.article


package object model {
  def toBeSlug(title: String): String =title.replaceAll(" ", "-").toLowerCase()
}
