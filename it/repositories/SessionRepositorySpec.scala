package repositories

import config.AppConfig
import fixtures.BaseFixtures
import models.UserAnswers
import org.mockito.Mockito.when
import org.mongodb.scala.model.Filters
import org.scalatest.OptionValues
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatestplus.mockito.MockitoSugar
import play.api.libs.json.Json
import uk.gov.hmrc.mongo.test.DefaultPlayMongoRepositorySupport

import java.time.{Clock, Instant, ZoneId}
import java.time.temporal.ChronoUnit
import scala.concurrent.ExecutionContext.Implicits.global

class SessionRepositorySpec
  extends AnyFreeSpec
    with Matchers
    with DefaultPlayMongoRepositorySupport[UserAnswers]
    with ScalaFutures
    with IntegrationPatience
    with OptionValues
    with MockitoSugar
    with BaseFixtures {

  private val instant = Instant.now.truncatedTo(ChronoUnit.MILLIS)
  private val stubClock: Clock = Clock.fixed(instant, ZoneId.systemDefault)

  private val userAnswers = UserAnswers(testInternalId, testErn, testArc, Json.obj("foo" -> "bar"), Instant.ofEpochSecond(1))

  private val mockAppConfig = mock[AppConfig]
  when(mockAppConfig.cacheTtl) thenReturn 1

  protected override val repository = new SessionRepository(
    mongoComponent = mongoComponent,
    appConfig      = mockAppConfig,
    clock          = stubClock
  )

  ".set" - {

    "must set the last updated time on the supplied user answers to `now`, and save them" in {

      val expectedResult = userAnswers copy (lastUpdated = instant)

      val setResult     = repository.set(userAnswers).futureValue
      val updatedRecord = find(
        Filters.and(
          Filters.equal("internalId", userAnswers.internalId),
          Filters.equal("ern", userAnswers.ern),
          Filters.equal("arc", userAnswers.arc)
        )
      ).futureValue.headOption.value

      setResult mustEqual true
      updatedRecord mustEqual expectedResult
    }
  }

  ".get" - {

    "when there is a record for this id" - {

      "must update the lastUpdated time and get the record" in {

        insert(userAnswers).futureValue

        val result         = repository.get(userAnswers.internalId, userAnswers.ern, userAnswers.arc).futureValue
        val expectedResult = userAnswers copy (lastUpdated = instant)

        result.value mustEqual expectedResult
      }
    }

    "when there is no record for this id" - {

      "must return None" in {

        repository.get(userAnswers.internalId, userAnswers.ern, "wrongArc").futureValue mustBe None
      }
    }
  }

  ".clear" - {

    "must remove a record" in {

      insert(userAnswers).futureValue

      val result = repository.clear(userAnswers).futureValue

      result mustEqual true
      repository.get(userAnswers.internalId, userAnswers.ern, userAnswers.arc).futureValue mustBe None
    }

    "must return true when there is no record to remove" in {
      val result = repository.clear(userAnswers).futureValue

      result mustEqual true
    }
  }

  ".keepAlive" - {

    "when there is a record for this id" - {

      "must update its lastUpdated to `now` and return true" in {

        insert(userAnswers).futureValue

        val result = repository.keepAlive(userAnswers.internalId, userAnswers.ern, userAnswers.arc).futureValue

        val expectedUpdatedAnswers = userAnswers copy (lastUpdated = instant)

        result mustEqual true
        val updatedAnswers = find(
          Filters.and(
            Filters.equal("internalId", userAnswers.internalId),
            Filters.equal("ern", userAnswers.ern),
            Filters.equal("arc", userAnswers.arc)
          )
        ).futureValue.headOption.value
        updatedAnswers mustEqual expectedUpdatedAnswers
      }
    }

    "when there is no record for this id" - {

      "must return true" in {

        repository.keepAlive(userAnswers.internalId, userAnswers.ern, "wrongArc").futureValue mustEqual true
      }
    }
  }
}
