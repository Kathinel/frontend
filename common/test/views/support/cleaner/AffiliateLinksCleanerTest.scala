package views.support.cleaner
import conf.Configuration
import org.joda.time.DateTime
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import views.support.AffiliateLinksCleaner._

class AffiliateLinksCleanerTest extends AnyFlatSpec with Matchers {

  "linkToSkimLink" should "correctly convert a link to a skimlink" in {
    val link = "https://www.piratendating.nl/"
    val pageUrl = "/guardian-pirates/soulmates"
    linkToSkimLink(link, pageUrl, "123") should equal(
      s"https://go.skimresources.com/?id=123&url=https%3A%2F%2Fwww.piratendating.nl%2F&sref=${Configuration.site.host}/guardian-pirates/soulmates",
    )
  }

  "shouldAddAffiliateLinks" should "correctly determine when to add affiliate links" in {
    val supportedSections = Set("film", "books", "fashion")

    shouldAddAffiliateLinks(
      switchedOn = false,
      "film",
      None,
      supportedSections,
      Set.empty,
      Set.empty,
      List.empty,
      isInDisableAffiliateLinksTest = False
    ) should be(false)
    shouldAddAffiliateLinks(
      switchedOn = true,
      "film",
      None,
      supportedSections,
      Set.empty,
      Set.empty,
      List.empty,
      isInDisableAffiliateLinksTest = False
    ) should be(true)
    shouldAddAffiliateLinks(
      switchedOn = true,
      "film",
      Some(false),
      supportedSections,
      Set.empty,
      Set.empty,
      List.empty,
      isInDisableAffiliateLinksTest = False
    ) should be(false)
    shouldAddAffiliateLinks(
      switchedOn = true,
      "news",
      Some(true),
      supportedSections,
      Set.empty,
      Set.empty,
      List.empty,
      isInDisableAffiliateLinksTest = False
    ) should be(true)
    shouldAddAffiliateLinks(
      switchedOn = true,
      "news",
      None,
      supportedSections,
      Set("bereavement"),
      Set.empty,
      List("bereavement"),
      isInDisableAffiliateLinksTest = False
    ) should be(false)
    shouldAddAffiliateLinks(
      switchedOn = true,
      "news",
      None,
      supportedSections,
      Set("bereavement"),
      Set.empty,
      List("tech"),
      isInDisableAffiliateLinksTest = False
    ) should be(false)
    shouldAddAffiliateLinks(
      switchedOn = true,
      "fashion",
      None,
      supportedSections,
      Set("bereavement"),
      Set.empty,
      List("tech"),
      isInDisableAffiliateLinksTest = False
    ) should be(true)
    shouldAddAffiliateLinks(
      switchedOn = true,
      "fashion",
      Some(true),
      supportedSections,
      Set.empty,
      Set("bereavement"),
      List("bereavement"),
      isInDisableAffiliateLinksTest = False
    ) should be(false)
    shouldAddAffiliateLinks(
      switchedOn = true,
      "fashion",
      Some(true),
      supportedSections,
      Set.empty,
      Set("bereavement"),
      List("tech"),
      isInDisableAffiliateLinksTest = False
    ) should be(true)
    shouldAddAffiliateLinks(
      switchedOn = true,
      "fashion",
      Some(true),
      supportedSections,
      Set.empty,
      Set("bereavement"),
      List("tech"),
      isInDisableAffiliateLinksTest = True
    ) should be(false)
  }
}
