package CS172IRProject.demo;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.json.simple.JSONArray;
import org.json.simple.JSONValue;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class QueryAnalyzer {
    private final String query;
    private final String username;
    private final String text;
    private final String titles;
    private final String timestamp;

    public String getQuery() {
        return query;
    }

    public String getUsername() {
        return username;
    }

    public String getText() {
        return text;
    }

    public String getTitles() {
        return titles;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public QueryAnalyzer(String content) throws ParseException, IOException {
        this.query = content;
        this.text = null;
        this.username = null;
        this.titles = null;
        this.timestamp = null;
    }
    public QueryAnalyzer(String content, Document doc) throws ParseException, IOException {
        this.query = content;
        this.text = doc.get("text");
        this.username = doc.get("username");
        if(doc.get("titles") == "tweet" || doc.get("titles") == "null")
            this.titles = "none";
        else {
            this.titles = doc.get("titles");
        }
        this.timestamp = doc.get("timestamp");
    }


    public ScoreDoc[] parseUsernames(String query) throws ParseException, IOException {
        QueryParser parser = new QueryParser("titles", new StandardAnalyzer());
        Query q = parser.parse("tweet");
        Directory dir = FSDirectory.open(Paths.get("index"));
        IndexReader reader = DirectoryReader.open(dir);
        IndexSearcher searcher = new IndexSearcher(reader);
        TopDocs docs = searcher.search(q, 100);
        ScoreDoc[] hits = docs.scoreDocs;
        System.out.println("Found " + hits.length + " hits.");

        for(int i=0;i<hits.length;++i) {
            int docId = hits[i].doc;
            System.out.println(docId);
            Document d = searcher.doc(docId);
            System.out.println((i + 1) + ". " + d.get("username") + "\n" + d.get("text") + "\n" + d.get("timestamp") + "\n" + d.get("location") + "\n" + d.get("titles"));
        }

        return hits;
    }

}


