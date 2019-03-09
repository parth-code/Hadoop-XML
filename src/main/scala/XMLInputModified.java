import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DataOutputBuffer;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;


/**
 * Reads records that are delimited by a specifc begin/end tag.
 */
public class XMLInputModified extends  TextInputFormat {

    public static final String START_TAGS = "xmlinput.start.tags";
    public static final String END_TAGS = "xmlinput.end.tags";

    @Override
    public RecordReader<LongWritable,Text> createRecordReader(InputSplit is, TaskAttemptContext tac)  {



        return new XmlRecordReader();




    }
    public static class XmlRecordReader extends RecordReader<LongWritable,Text> {
        private  String startTag;
        private  String endTag;
        private  String[] listOfStartTags;
        private  String[] listOfEndTags;
        private  long start;
        private  long end;
        private  FSDataInputStream fsin;
        private  DataOutputBuffer buffer = new DataOutputBuffer();
        private  LongWritable key = new LongWritable();
        private  Text value = new Text();



        @Override
        public void initialize(InputSplit is, TaskAttemptContext tac) throws IOException, InterruptedException {
            FileSplit fileSplit= (FileSplit) is;
            startTag = tac.getConfiguration().get(START_TAGS);
            endTag = tac.getConfiguration().get(END_TAGS);
            listOfStartTags = startTag.split(", ");
            listOfEndTags = endTag.split(", ");
//            for(String tag:listOfStringTags){
//                listOfTags.add(tag.getBytes(StandardCharsets.UTF_8));
//            }
            start = fileSplit.getStart();
            end = start + fileSplit.getLength();
            Path file = fileSplit.getPath();

            FileSystem fs = file.getFileSystem(tac.getConfiguration());
            fsin = fs.open(fileSplit.getPath());
            fsin.seek(start);
        }

        @Override
        public boolean nextKeyValue() throws IOException, InterruptedException {
            if (fsin.getPos() < end) {
                if (readUntilMatch(listOfStartTags, false)) {
                    try {
                        if (readUntilMatch(listOfEndTags, true)) {
                            value.set(buffer.getData(), 0, buffer.getLength());
                            key.set(fsin.getPos());
                            return true;
                        }
                    } finally {
                        buffer.reset();
                    }
                }
            }
            return false;
        }

        @Override
        public LongWritable getCurrentKey() throws IOException, InterruptedException {
            return key;
        }

        @Override
        public Text getCurrentValue() throws IOException, InterruptedException {
            return value;



        }

        @Override
        public float getProgress() throws IOException, InterruptedException {
            return (fsin.getPos() - start) / (float) (end - start);
        }

        @Override
        public void close() throws IOException {
            fsin.close();
        }
        private boolean readUntilMatch(String[] listOfTags, boolean withinBlock) throws IOException {
            int flag = 0;
            StringBuffer sb = new StringBuffer();
            while(true) {
                int b = fsin.read();
                char charAtb = (char)b;
                // end of file:
                if (b == -1) {
                    return false;
                }
                // save to buffer:
                if (withinBlock) {
                    buffer.write(b);
                }
                if (!withinBlock && fsin.getPos() >= end) {
                    return false;
                }
                if (flag == 1) {
                    if (charAtb != '>' && charAtb != ' ') {
                        sb.append(charAtb);
                        flag = 1;
                    } else {
                        for (String match : listOfTags) {
                            if(match == sb.toString()){
                                sb.setLength(0);
                                if(!withinBlock){
                                    buffer.write('<');
                                    buffer.write(match.getBytes());
                                    buffer.write('>');
                                }
                                return true;
                            }
                        }
                        flag = 0;
                    }
                }
                if(flag == 0) {
                    if (charAtb == '<') {
                        flag = 1;
                    } else {
                        flag = 0;
                    }
                }
            }
        }
    }
}