package services

import com.google.inject.Inject
import connectors.traderKnownFacts.GetTraderKnownFactsConnector
import models.TraderKnownFacts
import models.response.{ErrorResponse, TraderKnownFactsException}
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class GetTraderKnownFactsService @Inject()(connector: GetTraderKnownFactsConnector)(implicit ec: ExecutionContext){

  def getTraderKnownFacts(exciseRegistrationId: String)(implicit hc: HeaderCarrier): Future[Option[TraderKnownFacts]] =
    connector.getTraderKnownFacts(exciseRegistrationId).map {
      case Left(_) => throw TraderKnownFactsException(s"No known facts found for trader $exciseRegistrationId")
      case Right(value) => value
    }
}
