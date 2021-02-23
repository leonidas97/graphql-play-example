package controllers

import org.webjars.play.WebJarsUtil
import play.api.mvc.{Action, AnyContent, BaseController, ControllerComponents}

import javax.inject.{Inject, Singleton}

@Singleton
class HomeController @Inject()(val controllerComponents: ControllerComponents)(implicit webJarsUtil: WebJarsUtil) extends BaseController {

  def index(): Action[AnyContent] = Action {
    Ok(views.html.index())
  }

}
