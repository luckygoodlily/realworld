package model.article.comment.model.output

import model.profile.model.output.Profile

case class SingleComments(id: Long,
                          createdAt: String,
                          updatedAt: String,
                          body: String,
                          author: Profile)
