package topmentions

import com.gu.contentapi.client.model.ContentApiError
import com.gu.contentapi.client.model.v1.ItemResponse
import model.{TopMentionEntity, TopMentionsDetails, TopMentionsResult}
import org.scalatest.{BeforeAndAfterAll, GivenWhenThen}
import org.scalatest.featurespec.AnyFeatureSpec
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import test.{ConfiguredTestSuite, WithTestExecutionContext}
import org.mockito.Mockito._
import org.scalatestplus.mockito.MockitoSugar

import scala.concurrent.duration._
import scala.util.{Failure, Success}
import scala.concurrent.{Await, Future}

class TopMentionsServiceTest
    extends AnyFlatSpec
    with Matchers
    with BeforeAndAfterAll
    with WithTestExecutionContext
    with MockitoSugar {

  val fakeClient = mock[TopMentionsS3Client]
  val topMentionResult =
    TopMentionsResult(
      name = "name1",
      `type` = TopMentionEntity.Org,
      blocks = Seq("blockId1"),
      count = 1,
      percentage_blocks = 1.2f,
    )
  val successResponse =
    TopMentionsDetails(entity_types = Seq(TopMentionEntity.Org), results = Seq(topMentionResult), model = "model")

  "refreshTopMentions" should "return successfull future given getListOfKeys s3 call fails" in {
    when(fakeClient.getListOfKeys()) thenReturn Future.failed(new Throwable(""))
    val topMentionService = new TopMentionsService(fakeClient)

    Await.result(topMentionService.refreshTopMentions(), 1.second)
    val results = topMentionService.getAllTopMentions()

    results should be(None)
  }

  "refreshTopMentions" should "return successfull future given one of the S3 object calls fails" in {
    when(fakeClient.getListOfKeys()) thenReturn Future.successful(List("key1", "key2"))
    when(fakeClient.getObject("key1")) thenReturn Future.successful(successResponse)
    when(fakeClient.getObject("key2")) thenReturn Future.failed(new Throwable("error happend"))

    val topMentionService = new TopMentionsService(fakeClient)

    val refreshJob = Await.result(topMentionService.refreshTopMentions(), 1.second)
    val results = topMentionService.getAllTopMentions()

    refreshJob shouldBe a[Unit]
    results should be(None)
  }

  "refreshTopMentions" should "update in memory top mentions and return successfull future given one of the S3 object calls fails" in {
    when(fakeClient.getListOfKeys()) thenReturn Future.successful(List("key1"))
    when(fakeClient.getObject("key1")) thenReturn Future.successful(successResponse)

    val topMentionService = new TopMentionsService(fakeClient)

    val refreshJob = Await.result(topMentionService.refreshTopMentions(), 1.second)
    val results = topMentionService.getAllTopMentions()

    refreshJob shouldBe a[Unit]
    results.isDefined should be(true)
    results.get.get("key1") should equal(Some(successResponse))
  }

  "getEntityTopMentions" should "return the correct top mention result given correct blog id, filter entity and filter value" in {
    when(fakeClient.getListOfKeys()) thenReturn Future.successful(List("key1"))
    when(fakeClient.getObject("key1")) thenReturn Future.successful(successResponse)

    val topMentionService = new TopMentionsService(fakeClient)
    val refreshJob = Await.result(topMentionService.refreshTopMentions(), 1.second)

    val result = topMentionService.getEntityTopMentions("key1", TopMentionEntity.Org, "name1")

    result.get should equal(topMentionResult)
  }

  "getEntityTopMentions" should "return none given correct blog id, filter entity and with same filter value but different case" in {
    when(fakeClient.getListOfKeys()) thenReturn Future.successful(List("key1"))
    when(fakeClient.getObject("key1")) thenReturn Future.successful(successResponse)

    val topMentionService = new TopMentionsService(fakeClient)
    val refreshJob = Await.result(topMentionService.refreshTopMentions(), 1.second)

    val result = topMentionService.getEntityTopMentions("key1", TopMentionEntity.Org, "NAME1")

    result should equal(None)
  }

  "getEntityTopMentions" should "return none given a blog id that doesn't exist in cache" in {
    when(fakeClient.getListOfKeys()) thenReturn Future.successful(List("key1"))
    when(fakeClient.getObject("key1")) thenReturn Future.successful(successResponse)

    val topMentionService = new TopMentionsService(fakeClient)
    val refreshJob = Await.result(topMentionService.refreshTopMentions(), 1.second)

    val result = topMentionService.getEntityTopMentions("key2", TopMentionEntity.Org, "name1")

    result should equal(None)
  }

  "getEntityTopMentions" should "return none given a filter entity type that doesn't exist in cache for the relevant blog" in {
    when(fakeClient.getListOfKeys()) thenReturn Future.successful(List("key1"))
    when(fakeClient.getObject("key1")) thenReturn Future.successful(successResponse)

    val topMentionService = new TopMentionsService(fakeClient)
    val refreshJob = Await.result(topMentionService.refreshTopMentions(), 1.second)

    val result = topMentionService.getEntityTopMentions("key1", TopMentionEntity.Person, "Boris")

    result should equal(None)
  }

  "getEntityTopMentions" should "return none given a filter entity value that doesn't exist in cache for the relevant blog" in {
    when(fakeClient.getListOfKeys()) thenReturn Future.successful(List("key1"))
    when(fakeClient.getObject("key1")) thenReturn Future.successful(successResponse)

    val topMentionService = new TopMentionsService(fakeClient)
    val refreshJob = Await.result(topMentionService.refreshTopMentions(), 1.second)

    val result = topMentionService.getEntityTopMentions("key1", TopMentionEntity.Org, "someRandomOrg")

    result should equal(None)
  }
}