import java.io.StringReader

import org.scalatest._
import org.xml.sax.InputSource

import scala.xml.XML

class MapTest extends FlatSpec {
  "map" should "will not output the author name" in {
    val xml1 = XML.load(new InputSource(new StringReader("<article><author>Mark Grechanik</author></article>")))
    val te:String = (xml1 \ "author").text

    assert(te != "<author>Mark Grechanik</author>")
  }
}
