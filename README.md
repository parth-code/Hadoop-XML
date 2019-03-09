Done using cloudera vm

Install jdk 1.8 on cloudera

1)Change Java Home Directory in cloudera manager configurations tab. 
2)Also add JAVA_HOME in /etc/hadoop/conf/hadoop-env.sh
3) Increase Mapreduce Timeout to 6000000

sbt 2.12.8: The dependencies are defined within build.sbt


Configure Intellij to connect with cloudera through SFTP. Get ip address of Intellij using 
	ip addr
and connect with username and password cloudera
Define mapping in Mapping tab
Create fat jar using sbt clean, sbt compile and sbt assembly in order.
Transfer jar and dblp.xml via sftp

On cloudera vm:
1)go to input folder defined in mapping
2)start terminal there.
	sudo su hdfs
	$ hadoop fs -mkdir /user/cloudera
	$ hadoop fs -chown cloudera /user/cloudera
	$ exit
	$ sudo su cloudera
	$ hadoop fs -mkdir /user/cloudera/wordcount /user/cloudera/wordcount/input 
	$ hadoop fs -put <input_path>/dblp.xml  /user/cloudera/wordcount/input
	$ hadoop jar untitled-assembly-1.2.8.jar /user/cloudera/wordcount/input /user/cloudera/wordcount/output
	$ hadoop fs -get /user/cloudera/wordcount/output <output path>

Copy resulting csv file and add it to project directory.
Rename output file to dblp.csv
Run graphMapper.scala on output file.
Take result file result.csv and run on Gephi.
import the file as a edges table with separator semicolon and set the Merge strategy to sum(if not already set)


