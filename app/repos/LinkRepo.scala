package repos

import models.Link

import javax.inject.Singleton

@Singleton
class LinkRepo {

  private def links = List(
    Link(1, "www.google.com", "search engine", 11),
    Link(2, "www.facebook.com", "social network", 22),
    Link(3, "www.youtube.com", "video sharing platform", 22),
    Link(4, "www.wikipedia.com", "online encyclopedia", 33)
  )

  def allLinks(): List[Link] = {
    links
  }

  def getLinks(ids: Seq[Int]): List[Link] = {
    links.filter(link => ids.contains(link.id))
  }

  def getLinksByUserIds(ids: Seq[Int]): List[Link] = {
    links.filter(link => ids.contains(link.createdBy))
  }
}
