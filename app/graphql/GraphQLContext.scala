package graphql

import repos.{LinkRepo, UserRepo}

case class GraphQLContext(
  userRepo: UserRepo,
  linkRepo: LinkRepo)
