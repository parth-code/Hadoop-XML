import java.lang.Iterable

import com.typesafe.config.ConfigFactory
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.Path
import org.apache.hadoop.io.{IntWritable, LongWritable, Text}
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat
import org.apache.hadoop.mapreduce.{Job, Mapper, Reducer}
import org.apache.log4j.Logger

import scala.collection.JavaConverters._
import scala.xml.{Node, NodeSeq, XML}
object WordCount {
  val config = ConfigFactory.load("application.conf")
  class XMLMapper extends Mapper[LongWritable, Text, Text, IntWritable] {
    val logger = Logger.getLogger(classOf[XMLMapper])
    val one = new IntWritable(1)

    override def map(key: LongWritable, value: Text, context: Mapper[LongWritable, Text, Text, IntWritable]#Context): Unit = {
      logger.warn("Entered mapper")
      val start: String = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>" +
        "<!DOCTYPE dblp PUBLIC \"-//DBLP//DTD//EN\" \"https://dblp.uni-trier.de/xml/dblp.dtd\">" +
        "<dblp>"
      val end = "</dblp>"
      val xmlString = start + value.toString + end
      val one = new IntWritable(1)
      val xmlShard = XML.loadString(xmlString)
      val profList = config.getStringList("prof_list").asScala.toList

      def testresults: Seq[String] = (xmlShard \\ "_").filter(childEquals(profList)).filter(childExists()).map(x => (resultFormatter(x \ "author")))
      logger.warn("Filtered professors")
      val range = (0 to testresults.length)
      testresults.foreach {
        result =>
          context.write(
            new Text(result),
            one)
      }
    }

    def childEquals(value: List[String])(node: Node): Boolean = {
      val range = (0 to value.length - 1).toList
      range.foreach(valueIndex =>
        (node \ "author").foreach(auth =>
          if (auth.text.equals(value(valueIndex)))
            return true))
      false
    }

    def childExists()(node: Node) = {
      !(node \ "year").isEmpty
    }

    def resultFormatter(value: NodeSeq): String = {
      val ls: String = value.map(x => x.text).toList.sorted.mkString(", ")
      ls
    }
  }
  class XMLReducer extends Reducer[Text, IntWritable, Text, IntWritable] {
    override def reduce(key: Text, values: Iterable[IntWritable], context: Reducer[Text, IntWritable, Text, IntWritable]#Context): Unit = {
      var sum = values.asScala.foldLeft(0)(_ + _.get)
      context.write(key, new IntWritable(sum))
    }
  }


  def main(args: Array[String]): Unit = {
    val configuration = new Configuration
    configuration.set("xmlinput.start.tags", config.getString("startList"))
    configuration.set("xmlinput.end.tags", config.getString("endList"))
    configuration.set("mapred.textoutputformat.separatorText", ",")
    configuration.set(
      "io.serializations",
      "org.apache.hadoop.io.serializer.JavaSerialization," +
        "org.apache.hadoop.io.serializer.WritableSerialization"
    )
    val job = Job.getInstance(configuration)
    job.setJobName("count")
    job.setJarByClass(this.getClass)
    job.setMapperClass(classOf[XMLMapper])
    job.setReducerClass(classOf[XMLReducer])
    
    job.setOutputKeyClass(classOf[Text])
    job.setOutputValueClass(classOf[IntWritable])

    job.setInputFormatClass(classOf[XMLInputModified])
    //job.setNumReduceTasks(0)
    FileInputFormat.setInputPaths(job, new Path(args(1)))
    FileOutputFormat.setOutputPath(job, new Path(args(2)))
    System.exit(if (job.waitForCompletion(true)) 0 else 1)
  }
}
