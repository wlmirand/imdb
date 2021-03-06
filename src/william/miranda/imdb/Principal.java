package william.miranda.imdb;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import william.miranda.imdb.model.Filme;
import william.miranda.imdb.parser.HtmlParser;
import william.miranda.imdb.parser.MovieFeeder;
import william.miranda.imdb.parser.ResultadoPredicao;
import william.miranda.imdb.parser.Utils;
import william.miranda.lucene.LuceneDatabase;
import william.miranda.lucene.LuceneResult;
import william.miranda.lucene.LuceneSearch;
import william.miranda.lucene.LuceneSearch.TipoSimilaridade;
import william.miranda.recomendacao.Recomendacao;
import william.miranda.xml.XMLParser;


public class Principal
{
	//lista para armazenar os objetos do tipo Filme apos parsear cada URL
	private static List<Filme> filmes = new ArrayList<>();
	
	//lista que contem as URLs que foram obtidas para os filmes do arquivo
	private static List<String> urls;
	
	private static Runnable r = new Runnable()
	{
		@Override
		public void run()
		{
			/* parseia o filme para cada URL lida do arquivo de entrada
			 * cada linha eh da forma <ID> | <URL> */
			for (String linha : urls)
			{
				try
				{
					System.out.println(linha);
					
					String[] tmp = linha.split("\\|");
					
					int id = Integer.valueOf(tmp[0].trim());
					String url = tmp[1].trim();
					
					Filme f = startParser(url);
					
					if (f != null)
					{
						f.setImdbUrl(url);
						f.setId(id);
						
						Utils.salvarFilme(f);
					}
					
					Thread.sleep(2000);
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
			
			//Neste ponto, temos uma Lista de Filmes... entao geramos o XML
			//Filme.toXML(filmes);
		}
	};
	
	public static void main(String[] args)
	{
		//parseamos o arquivo do MovieLens e obtemos as URLs de todos os filmes
		//parseMovieLens();

		/* roda a thread para parsear as URLs contendo os dados dos filmes
		 * , com intervalo, de modo que nao exceda o limite de requisicoes
		 */
		//parseImdb();
		
		//pega todos os arquivos pequenos e gera o XML grande
		//makeResultFile();
		
		//chama a parte do Lucene (para testes)
		//luceneTest();
		
		//chama o algoritmo colaborativo para recomendacao
		recomendacao();
	}
	
	/**
	 * Parseia uma URL do IMDB e retorna o XML contendo os metadados do Filme correspondente
	 * @param url
	 * @return
	 */
	private static Filme startParser(String url)
	{
		try
		{			
			//url = "http://www.imdb.com/title/tt0113189";//GoldenEye
			//url = "http://www.imdb.com/title/tt0082398";//For Your Eyes Only
			//url = "http://www.imdb.com/title/tt1038988";//Rec
			//url = "http://www.imdb.com/title/tt0162930";//Die Hard Dracula
			if (!"null".equals(url))
			{
				url = Utils.preparaURL(url);
				
				HtmlParser pick = new HtmlParser(url);
				pick.parseURL();
				pick.parseReviews();
				
				//neste ponto temos um objeto do tipo Filme preenchido
				Filme f = pick.getFilme();
				
				return f;
			}
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	private static void parseMovieLens()
	{
		MovieFeeder mf = new MovieFeeder("data/ml-100k/u.item");
		mf.readFile();
	}
	
	public static void parseImdb()
	{
		Path path = Paths.get("urls.txt");
		urls = Utils.readFromFile(path);
		new Thread(r).start();
	}
	
	public static void makeResultFile()
	{
		try {
			Utils.generateFinalXML();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void luceneTest()
	{
		//iniciamos a engine do Lucene com os diretorios de entrada e saida
		Path localXml = Paths.get("out/");
		Path localIndex = Paths.get("index/");
		
		LuceneDatabase luceneDB = new LuceneDatabase(localXml, localIndex, false);//o construtor ja prepara os indices

		//definimos as variaveis da busca
		int numResultados = 5;
		TipoSimilaridade tipo = TipoSimilaridade.GENEROS;
		
		//criamos o objeto que ira fazer a busca nos indices e definimos um filme inicial
		Filme f = XMLParser.parseXML(Paths.get("out/2.xml"));
		LuceneSearch luceneSearch = new LuceneSearch(f, luceneDB, numResultados);
		
		System.out.println("Filmes Similares à: " + f.getTitulo() + " baseado no meta-dado " + tipo.name() + " e N = " + numResultados);
		System.out.println("-----------------------------");
		
		for (LuceneResult r : luceneSearch.getMetadado(tipo))
		{
			System.out.println(r);
		}
	}
	
	public static void recomendacao()
	{
		//define os parametros
		TipoSimilaridade[] arrayTipoSimilaridade = {TipoSimilaridade.ATORES, TipoSimilaridade.CRIADORES, TipoSimilaridade.DIRETORES, TipoSimilaridade.KEYWORDS, TipoSimilaridade.GENEROS};
		int[] arrayNumFilmesSimilares = {2, 5, 10, 20, 30, 40, 50};
		
		//varia os parametros
		for (TipoSimilaridade tipoSimilaridade : arrayTipoSimilaridade)
		{
			for (int numFilmesSimilares : arrayNumFilmesSimilares)
			{
				System.out.println("Rodando o algoritmo para: " + tipoSimilaridade.name() + " e N = " + numFilmesSimilares);
				
				//roda o algoritmo
				Recomendacao recomendacao = new Recomendacao();
				List<ResultadoPredicao> r = recomendacao.percorrerAvaliacoes(numFilmesSimilares, tipoSimilaridade);
				
				//salva os resultados
				Path path = Paths.get("resultados/resultados_" + tipoSimilaridade.name() + "_" + numFilmesSimilares + ".txt");
				Utils.saveToFile(path, r);
			}
		}
	}
}
