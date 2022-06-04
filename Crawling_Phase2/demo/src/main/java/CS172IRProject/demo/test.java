package CS172IRProject.demo;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import org.apache.lucene.queryparser.flexible.standard.StandardQueryParser;
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

import static org.apache.lucene.document.FloatPoint.newRangeQuery;

public class test {
    public static void main(String[] args) throws ParseException, IOException, QueryNodeException {
        QueryParser parser = new QueryParser("timestamp", new StandardAnalyzer());
        Query q = parser.parse("1650201448000");              //*wildcard matches character sequence
        //newRangeQuery q = parser

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
    }

}