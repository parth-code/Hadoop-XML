import org.scalatest.FlatSpec
//Test for graphMapper
class FormatTest extends FlatSpec{
  "Output" must "format to" in {
    val a = "a,b,1"
    val ls: String = "a,b"
    val list = a.split(",").toList.dropRight(1)
    assert(list.mkString(",") == ls)
  }
}
