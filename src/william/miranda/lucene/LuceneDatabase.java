package william.miranda.lucene;

import java.io.IOException;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FloatField;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

import william.miranda.imdb.model.Filme;

public class LuceneDatabase
{
	//variaveis internas
	private StandardAnalyzer analyzer;
	private Directory index;
	private IndexWriterConfig config;
	private IndexWriter writer;
	
	public LuceneDatabase() throws IOException
	{
		analyzer = new StandardAnalyzer(Version.LUCENE_45);
		index = new RAMDirectory();
		config = new IndexWriterConfig(Version.LUCENE_45, analyzer);
		writer = new IndexWriter(index, config);
	}
	
	public void addDoc(Filme f) throws IOException
	{
		Document doc = new Document();
		doc.add(new StringField("titulo", f.getTitulo(), Field.Store.YES));
		doc.add(new StringField("imdbUrl", f.getImdbUrl(), Field.Store.YES));
		doc.add(new StringField("sinopse", f.getSinopse(), Field.Store.YES));
		doc.add(new StringField("storyline", f.getStoryline(), Field.Store.YES));
		doc.add(new IntField("ano", f.getAno(), Field.Store.YES));
		doc.add(new IntField("id", f.getId(), Field.Store.YES));
		doc.add(new FloatField("rating", f.getRating(), Field.Store.YES));
		writer.addDocument(doc);
		writer.commit();
		//org.apache.lucene.document.Fl
	}
	
	public void query(String queryStr) throws ParseException, IOException
	{
		QueryParser queryParser = new QueryParser(Version.LUCENE_45, "titulo", analyzer);
		Query query = queryParser.parse(queryStr);
		
		int hitsPerPage = 10;
		IndexReader reader = IndexReader.open(index);
		IndexSearcher searcher = new IndexSearcher(reader);
		TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage, true);
		searcher.search(query, collector);
		ScoreDoc[] hits = collector.topDocs().scoreDocs;
		
		System.out.println("Found " + hits.length + " hits.");
		for(int i=0;i<hits.length;++i) {
		    int docId = hits[i].doc;
		    Document d = searcher.doc(docId);
		    System.out.println((i + 1) + ". " + d.get("titulo") + "\t" + d.get("sinopse"));
		}
	}
	
	/* gets */
	public Directory getIndex()
	{
		return this.index;
	}
}
