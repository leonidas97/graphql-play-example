package graphql

import models.{Link, User}
import sangria.execution.deferred._
import sangria.macros.derive.{AddFields, deriveObjectType}
import sangria.schema.{ObjectType, _}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object GraphQLSchema {
  /**
   * Relations
   */
  val linkByUserRel = Relation[Link, Int]("byUser", link => Seq(link.createdBy))

  /**
   * Fetchers
   */
  implicit val linkHasId = HasId[Link, Int](_.id)
  implicit val userHasId = HasId[User, Int](_.id)

  val linksFetcher = Fetcher.rel(
    (ctx: GraphQLContext, ids: Seq[Int]) => Future { ctx.linkRepo.getLinks(ids) },
    (ctx: GraphQLContext, ids: RelationIds[Link]) => Future { ctx.linkRepo.getLinksByUserIds(ids(linkByUserRel)) }
  )
  val userFetcher = Fetcher(
    (ctx: GraphQLContext, ids: Seq[Int]) => Future { ctx.userRepo.getUsers(ids) }
  )

  /**
   * Types
   */
  lazy val LinkType: ObjectType[Unit, Link] = ObjectType(
    "Link",
    "Link contains: url, desc and id",
    fields[Unit, Link](
      Field("id", IntType, resolve = _.value.id),
      Field("url", StringType, resolve = ctx => ctx.value.url),
      Field("description", StringType, resolve = ctx => ctx.value.url),
      Field("createdBy", UserType, resolve = ctx => userFetcher.defer(ctx.value.createdBy))
    )
  )
  lazy val UserType: ObjectType[Unit, User] = deriveObjectType[Unit, User](
    AddFields(Field("links", ListType(LinkType), resolve = c => linksFetcher.deferRelSeq(linkByUserRel, c.value.id)))
  )

  /**
   * Arguments
   */
  val Id = Argument("id", IntType)
  val Ids = Argument("ids", ListInputType(IntType))

  /**
   * Queries
   */
  val QueryType = ObjectType(
    "Query",
    fields[GraphQLContext, Unit](
      Field("allLinks", ListType(LinkType), resolve = _.ctx.linkRepo.allLinks()),
      Field("link", OptionType(LinkType), arguments = List(Id), resolve = c => linksFetcher.deferOpt(c.arg(Id))),
      Field("links", ListType(LinkType), arguments = List(Ids), resolve = c => linksFetcher.deferSeq(c.arg(Ids))),
      Field("allUsers", ListType(UserType), resolve = _.ctx.userRepo.allUsers()),
      Field("user", OptionType(UserType), arguments = List(Id), resolve = c => userFetcher.deferOpt(c.arg(Id))),
      Field("users", ListType(UserType), arguments = List(Ids), resolve = c => userFetcher.deferSeq(c.arg(Ids))),
    )
  )

  /**
   * Schema
   */
  val schemaDefinition = Schema(QueryType)

  /**
   * Resolver
   */
  val Resolver = DeferredResolver.fetchers(
    linksFetcher,
    userFetcher
  )
}
