package gscraper

import gscraper.actor.GeneanetScraper
import gscraper.LocalConfig._

object Main extends App {

  assert(url.contains("lang=fr"), "The initial URL must use the French locale")

  val pathMatcher = "A*D*S?".r

  "" match {
    case pathMatcher(_*) => ()
    case _ => throw new Exception()
  }

  GeneanetScraper.scrape(url, cookieValue, userAgent, pathMatcher){ tree =>
    import gscraper.gedcom.GedcomUtils._

    writeGedcom(toGedcom(tree), "test5.ged")
  }

}
