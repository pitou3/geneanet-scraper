package gscraper

import gscraper.actor.GeneanetScraper
import gscraper.LocalConfig._

import java.net.URL
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object Main extends App {

  assert(url.contains("lang=fr"), "The initial URL must use the French locale")

  val pathMatcher = "A*D*S?".r

  "" match {
    case pathMatcher(_*) => ()
    case _ => throw new Exception()
  }

  def sanitizeForFileName(str: String): String = str.replaceAll("[\\\\/:*?\"<>|\\s]+", "_").stripPrefix("_").stripSuffix("_")

  val treeName = sanitizeForFileName(new URL(url).getPath.stripPrefix("/"))
  val timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"))

  GeneanetScraper.scrape(url, cookieValue, userAgent, pathMatcher){ tree =>
    import gscraper.gedcom.GedcomUtils._

    val personName = tree.persons.headOption
      .map(p => sanitizeForFileName(s"${p.name}_${p.surname}"))
      .getOrElse("vide")

    writeGedcom(toGedcom(tree), s"${treeName}_${personName}_$timestamp.ged")
  }

}
