### Finds the publications by professors at the Computer Science department at UIC from the dblp xml dataset and creates a graph displaying the Bibliographic coupling between them.

#### **Tools used**
1) Intellij
2) Scala
3) Sbt
4) Cloudera Quickstart Vm - linux vm with all the necessary Apache tools(Mapreduce, HDFS) installed.
5) Gephi - graphing tool

sbt 2.12.8: The dependencies are defined within build.sbt
#### **Instructions **
Install jdk 1.8 on cloudera

1) Change Java Home Directory in the cloudera manager configurations tab. 

2) Add JAVA_HOME in /etc/hadoop/conf/hadoop-env.sh.

3) Increase Mapreduce Timeout to 6000000

4)Configure Intellij to connect with cloudera through SFTP. Get ip address of Intellij using ip addr
and connect using the default username and password `cloudera`.

5)Define mapping in Mapping tab.

6)Create fat jar using sbt clean, sbt compile and sbt assembly in order.

7)Transfer jar and dblp.xml via sftp.

8)On cloudera vm:

i)go to input folder defined in mapping.
	
ii)start terminal there. Execute the following commands.
	
	`sudo su hdfs`
	
  	`$ hadoop fs -mkdir /user/cloudera`
	
	`$ hadoop fs -chown cloudera /user/cloudera`
	
	`$ exit`
	
	`$ sudo su cloudera`
	
	`$ hadoop fs -mkdir /user/cloudera/wordcount /user/cloudera/wordcount/input `
	
	`$ hadoop fs -put <input_path>/dblp.xml  /user/cloudera/wordcount/input`
	
	`$ hadoop jar untitled-assembly-1.2.8.jar /user/cloudera/wordcount/input /user/cloudera/wordcount/output`
	
	`$ hadoop fs -get /user/cloudera/wordcount/output <output path>`

9) Copy resulting csv file and add it to project directory.

10) Rename output file to **dblp.csv**

11) Run **graphMapper.scala** on output file.

12)Take result file **result.csv** and run on Gephi.

13)Import the file as a edges table with separator semicolon and set the Merge strategy to sum(if not already set).


