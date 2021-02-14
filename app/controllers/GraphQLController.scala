package controllers

import repos.{LinkRepo, UserRepo}
import play.api.libs.json._
import play.api.mvc._
import sangria.ast.Document
import sangria.execution.{ErrorWithResolver, Executor, QueryAnalysisError}
import sangria.marshalling.playJson._
import sangria.parser.{QueryParser, SyntaxError}
import graphql.{GraphQLSchema, GraphQLContext}

import javax.inject._
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

@Singleton
class GraphQLController @Inject()(val userDao: UserRepo, val linkDao: LinkRepo, val controllerComponents: ControllerComponents)
  (implicit executionContext: ExecutionContext)
  extends BaseController {

  def playground = Action {
    Ok(views.html.playground())
  }

  def graphql = Action.async(parse.json) { request =>
    val query = (request.body \ "query").as[String]
    val operation = (request.body \ "operationName").asOpt[String]
    val variables = (request.body \ "variables").toOption.flatMap {
      case JsString(vars) => Some(parseVariables(vars))
      case obj: JsObject =>  Some(obj)
      case _ => Some(Json.obj())
    }

    QueryParser.parse(query) match {
      case Success(queryAst) => executeGraphQLQuery(queryAst, operation, variables.get)
      case Failure(error: SyntaxError) => Future.successful(BadRequest(Json.obj("error" -> error.getMessage)))
      case Failure(_) => Future.successful(InternalServerError)
    }
  }

  private def parseVariables(variables: String) =
    if (variables.trim == "" || variables.trim == "null")
      Json.obj()
    else
      Json.parse(variables).as[JsObject]

  private def executeGraphQLQuery(query: Document, op: Option[String], vars: JsObject) = {
    Executor.execute(
      GraphQLSchema.schemaDefinition,
      query,
      GraphQLContext(userDao, linkDao),
      operationName = op,
      variables = vars,
      deferredResolver = GraphQLSchema.Resolver)
      .map(Ok(_))
      .recover {
        case error: QueryAnalysisError => BadRequest(error.resolveError)
        case error: ErrorWithResolver => InternalServerError(error.resolveError)
      }
  }
}
