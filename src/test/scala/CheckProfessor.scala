import org.scalamock.scalatest.MockFactory
import org.scalatest.FlatSpec

class CheckProfessor extends FlatSpec with MockFactory{
  "Professor's name" must " match with those in list" in {
    val list: List[String] = List("John A", "Peter B")
    var flag = 0
    list.foreach(l => {
      if(l == "John A") flag = 1
    })
    if(flag == 1){
      assert(true)
    }
  }
}
