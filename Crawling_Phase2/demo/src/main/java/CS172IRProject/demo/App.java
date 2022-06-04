package CS172IRProject.demo;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
//import org.apache.lucene.document.LongPoint;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
//import org.apache.lucene.document.StringField;
//import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.util.Version;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;



public class App
{

    private IndexWriter writer;
    public static void main(String[] args) {
        /** Index all text files under a directory.
         * <p>
         * This is a command-line application demonstrating simple Lucene indexing.
         * Run it with no command-line arguments for usage information.
         */

        /** Index all text files under a directory. */
        String usage = "java org.apache.lucene.demo.IndexFiles"
                + " [-index INDEX_PATH] [-docs DOCS_PATH] [-update]\n\n"
                + "This indexes the documents in DOCS_PATH, creating a Lucene index"
                + "in INDEX_PATH that can be searched with SearchFiles";
        String docsPath = "better.json";
        String indexPath = "index";
        boolean create = true;
        for(int i=0;i<args.length;i++) {
            if ("-index".equals(args[i])) {
                indexPath = args[i+1];
                i++;
            } else if ("-docs".equals(args[i])) {
                docsPath = args[i+1];
                i++;
            } else if ("-update".equals(args[i])) {
                create = false;
            }
        }

        if (docsPath == null) {
            System.err.println("Usage: " + usage);
            System.exit(1);
        }

        // final Path docDir = Paths.get(docsPath);
        // if (!Files.isReadable(docDir)) {
        //     System.out.println("Document directory '" +docDir.toAbsolutePath()+ "' does not exist or is not readable, please check the path");
        //     System.exit(1);
        // }
        final Path docDir = Paths.get(docsPath);
        System.out.println(docDir.getFileName());
        if (!Files.isReadable(docDir)) {
            System.out.println("Document directory '" +docDir.toAbsolutePath()+ "' does not exist or is not readable, please check the path");
            System.exit(1);
        }

        Date start = new Date();
        try {
            System.out.println("Indexing to directory '" + indexPath + "'...");

            Directory dir = FSDirectory.open(Paths.get(indexPath));
            Analyzer analyzer = new StandardAnalyzer();
            IndexWriterConfig iwc = new IndexWriterConfig(analyzer);

            if (create) {
                // Create a new index in the directory, removing any
                // previously indexed documents:
                iwc.setOpenMode(OpenMode.CREATE);
            } else {
                // Add new documents to an existing index:
                iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);
            }

            // Optional: for better indexing performance, if you
            // are indexing many documents, increase the RAM
            // buffer.  But if you do this, increase the max heap
            // size to the JVM (eg add -Xmx512m or -Xmx1g):
            //
            // iwc.setRAMBufferSizeMB(256.0);

            //IndexWriter writer = new IndexWriter(dir, iwc);
            //indexDoc(writer, docDir, start.getTime());

            // NOTE: if you want to maximize search performance,
            // you can optionally call forceMerge here.  This can be
            // a terribly costly operation, so generally it's only
            // worth it when your index is relatively static (ie
            // you're done adding documents to it):
            //
            //writer.forceMerge(1);

            //writer.close();
            //JSONArray jsonObjects = parseJSONFile();
            App iw = new App();
            iw.createIndex();

            Date end = new Date();
            System.out.println(end.getTime() - start.getTime() + " total milliseconds");

        } catch (IOException e) {
            System.out.println(" caught a " + e.getClass() +
                    "\n with message: " + e.getMessage());
        }
    }

    /** Indexes a single document */
    static void indexDoc(IndexWriter writer, Path file, long lastModified) throws IOException {
        try (InputStream stream = Files.newInputStream(file)) {
            // make a new, empty document
            Document doc = new Document();

            // Add the path of the file as a field named "path".  Use a
            // field that is indexed (i.e. searchable), but don't tokenize
            // the field into separate words and don't index term frequency
            // or positional information:
            Field pathField = new StringField("path", file.toString(), Field.Store.YES);
            doc.add(pathField);

            // Add the last modified date of the file a field named "modified".
            // Use a LongPoint that is indexed (i.e. efficiently filterable with
            // PointRangeQuery).  This indexes to milli-second resolution, which
            // is often too fine.  You could instead create a number based on
            // year/month/day/hour/minutes/seconds, down the resolution you require.
            // For example the long value 2011021714 would mean
            // February 17, 2011, 2-3 PM.
            doc.add(new LongPoint("modified", lastModified));

            // Add the contents of the file to a field named "contents".  Specify a Reader,
            // so that the text of the file is tokenized and indexed, but not stored.
            // Note that FileReader expects the file to be in UTF-8 encoding.
            // If that's not the case searching for special characters will fail.
            doc.add(new TextField("contents", new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))));

            if (writer.getConfig().getOpenMode() == OpenMode.CREATE) {
                // New index, so we just add the document (no old document can be there):
                System.out.println("adding " + file);
                writer.addDocument(doc);
            } else {
                // Existing index (an old copy of this document may have been indexed) so
                // we use updateDocument instead to replace the old one matching the exact
                // path, if present:
                System.out.println("updating " + file);
                writer.updateDocument(new Term("path", file.toString()), doc);
            }
        }
    }

    public JSONArray parseJSONFile() throws FileNotFoundException {

        //Get the JSON file, in this case is in ~/resources/test.json
        //InputStream jsonFile =  getClass().getResourceAsStream("C:/Users/huntm/Desktop/cs172/lucene_project/better.json");
        //FileReader jsonFile = new FileReader("better.json");
        FileReader jsonFile = new FileReader("better.json");
        final Path docDir = Paths.get("better.json");
        if (!Files.isReadable(docDir)) {
            System.out.println("Document directory '" +docDir.toAbsolutePath()+ "' does not exist or is not readable, please check the path");
            System.exit(1);
        }
        System.out.println(jsonFile.toString());
        //Reader readerJson = new InputStreamReader(jsonFile);

        //Parse the json file using simple-json library
        Object fileObjects= JSONValue.parse(jsonFile);
        JSONArray arrayObjects=(JSONArray)fileObjects;

        //System.out.println(fileObjects.toString());
        return arrayObjects;

    }

    public void addDocuments(JSONArray jsonObjects) throws IOException {
        Analyzer analyzer = new StandardAnalyzer();
        IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
        iwc.setOpenMode(OpenMode.CREATE);
        Directory dir = FSDirectory.open(Paths.get("index"));
        writer = new IndexWriter(dir, iwc);

        for(JSONObject object : (List<JSONObject>) jsonObjects){
            Document doc = new Document();
            // for(String field : (Set<String>) object.keySet()){
            //Class username = object.get("username").getClass();
            //Class timestamp = object.get("timestamp").getClass();
            //Class location = object.get("location").getClass();
            //Class text = object.get("text").getClass();
            //Class titles = object.get("titles").getClass();
            //Class type = object.get(field).getClass();
            if (object.get("titles") instanceof JSONArray) {
                JSONArray arr = (JSONArray)object.get("titles");
                for (int i = 0; i < arr.size(); ++i) {
                    String x = (String)arr.get(i);
                    doc.add(new TextField("titles", x, Field.Store.YES));
                    //System.out.println(x);
                }
            }
            else if (object.get("titles") instanceof Long){
                doc.add(new TextField("titles", "", Field.Store.NO));
            }
            else {
                doc.add(new TextField("titles", (String)object.get("titles"), Field.Store.YES));
                //System.out.println((String)object.get("titles"));
            }

            // if(type.equals(String.class)){
            //doc.add(new StringField(field, (String)object.get(field), Field.Store.NO));
            doc.add(new StringField("username", object.get("username") == null ? "null" : (String)object.get("username"), Field.Store.YES));
            doc.add(new LongPoint("timestamp" , (Long)object.get("timestamp")));
            System.out.println(object.get("timestamp"));
            //if (object.get("location") == null)
            //String loc = object.get("location") == JSONObject.null ? "" : (String)object.get("location");
            doc.add(new TextField("location", object.get("location") == null ? "null" : (String)object.get("location"), Field.Store.YES));
            doc.add(new TextField("text", object.get("text") == null ? "null" : (String)object.get("text"), Field.Store.YES));
            //doc.add(new StringField("titles", (String)object.get("titles"), Field.Store.NO));
            //}
            //}
            try {
                writer.addDocument(doc);
            } catch (IOException ex) {
                System.err.println("Error adding documents to the index. " +  ex.getMessage());
            }
        }
        writer.close();
    }

    public void createIndex() {
        try {
            JSONArray jsonObjects = parseJSONFile();
            addDocuments(jsonObjects);
        } catch (IOException ex) {
            System.err.println("Error adding documents to the index. " +  ex.getMessage());
        }
    }
}


