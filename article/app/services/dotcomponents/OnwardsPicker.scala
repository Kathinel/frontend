package services.dotcomponents

import agents.CuratedContentAgent
import common.Edition
import model.dotcomrendering.OnwardCollectionResponse
import model.{ArticlePage, ContentFormat}
import play.api.mvc.RequestHeader
import views.support.Commercial

class OnwardsPicker(curatedContentAgent: CuratedContentAgent) {
  def forArticle(article: ArticlePage, edition: Edition)(implicit request: RequestHeader): Seq[OnwardCollectionResponse] = {
    val format = article.article.content.metadata.format.getOrElse(ContentFormat.defaultContentFormat)
    val curatedContent = curatedContentAgent.getTrails(format.theme, edition, Commercial.isAdFree(request))

    Seq(
      OnwardCollectionResponse(
        heading = s"More from ${format.theme}",
        trails = curatedContent,
      ),
    )
  }
}