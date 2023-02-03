package controllers

import controllers.actions._
import forms.$className$FormProvider
import javax.inject.Inject
import models.Mode
import navigation.Navigator
import pages.$className$Page
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import views.html.$className$View

import scala.concurrent.Future

class $className$Controller @Inject()(
                                       override val messagesApi: MessagesApi,
                                       override val sessionRepository: SessionRepository,
                                       override val navigator: Navigator,
                                       auth: AuthAction,
                                       withMovement: MovementAction,
                                       getData: DataRetrievalAction,
                                       requireData: DataRequiredAction,
                                       formProvider: $className$FormProvider,
                                       val controllerComponents: MessagesControllerComponents,
                                       view: $className$View
                                     ) extends BaseNavigationController {

  def onPageLoad(ern: String, arc: String, mode: Mode): Action[AnyContent] =
    (auth(ern) andThen withMovement(arc) andThen getData andThen requireData) { implicit request =>
      Ok(view(fillForm($className$Page, formProvider()), mode))
    }

  def onSubmit(ern: String, arc: String, mode: Mode): Action[AnyContent] =
    (auth(ern) andThen withMovement(arc) andThen getData andThen requireData).async { implicit request =>
      formProvider().bindFromRequest().fold(
        formWithErrors =>
          Future.successful(BadRequest(view(formWithErrors, mode))),
        value =>
          saveAndRedirect($className$Page, value, mode)
      )
    }
}
