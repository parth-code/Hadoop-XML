import java.io.{File, PrintWriter}

import scala.io.Source

object graphMapper {
  def main(args:Array[String]):Unit = {
    val lines = Source.fromFile("dblp.csv").getLines()
    val writer =  new PrintWriter(new File("result.csv"))
    writer.write("source;target\n")
    lines.foreach(line =>
      writer.write(formatLine(line)))
    writer.close()
  }
  def formatLine(s: String):String = {
    val list: List[String] = s.split(",").toList
    val number = list.last.toInt
    val dropList = list.dropRight(1)
    val list1: StringBuilder = new StringBuilder
    if(dropList.length > 2){
      val ListOfArray = dropList.toArray.combinations(2).toList
      ListOfArray.foreach(array => {
        val array1 = new StringBuilder
        array1.append(array.mkString(";").trim)
        array1.append("\n")
//        print(array1)
        list1.append(array1.toString())
      })
    }
    else{
      list1.append(dropList.mkString(";").trim)
      if (dropList.length == 1){
        list1.append(";")
        list1.append(dropList.mkString(";").trim)
      }
      list1.append("\n")
    }
    val stringBuilder = new StringBuilder()
    (0 to number-1).foreach( x => stringBuilder.append(list1))
//    print(stringBuilder)
    stringBuilder.toString
  }
}
