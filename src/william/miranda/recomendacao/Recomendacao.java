package william.miranda.recomendacao;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import william.miranda.imdb.model.Filme;
import william.miranda.imdb.model.FilmeRating;
import william.miranda.imdb.parser.UserParser;
import william.miranda.lucene.LuceneDatabase;
import william.miranda.lucene.LuceneResult;
import william.miranda.lucene.LuceneSearch;
import william.miranda.xml.XMLParser;

/** Esta classe implementa o algoritmo de Recomendação baseada em Itens
 * @author william.miranda
 */
public class Recomendacao
{
	//estrutura para armazenar as entradas do u.data
	private Map<Integer, List<FilmeRating>> mapRatings;
	private UserParser userParser;
	
	//engine do Lucene
	LuceneDatabase luceneDB;
	
	//inicializamos os pre requisitos
	public Recomendacao()
	{
		//faz o parsing dos ratings dos usuarios guardando no objeto mapRatings
		Path p = Paths.get("data/ml-100k/u.data");
		userParser = new UserParser(p);
		mapRatings = userParser.getUserRatings();
		
		//iniciamos a engine do Lucene com os diretorios de entrada e saida
		Path localXml = Paths.get("out/");
		Path localIndex = Paths.get("index/");
		
		luceneDB = new LuceneDatabase(localXml, localIndex, false);
	}
	
	//obtem as triplas do arquivo u.data, chamando o algorito de predicao para cada tripla
	public void percorrerAvaliacoes(int numFilmesSimilares)
	{
		//para cada user, pega a Lista de Reviews
		for (int userId : mapRatings.keySet())
		{
			List<FilmeRating> lista = mapRatings.get(userId);
			
			//varremos a lista para obter as triplas originais
			for (FilmeRating fr : lista)
			{
				int filmeId = fr.getFilmeId();
				int rating = fr.getRating();
				
				//tendo a tripla original do arquivo, jogamos no algoritmo
				PredizerNota(userId, filmeId, rating, numFilmesSimilares);
			}
		}
	}
	
	/* Este metodo implementa o algorito de Predição
	 * O objetivo eh "adiviinhar" a nota de um usuario U daria para um filme F
	 * baseado nas outras notas da base de dados.
	 * A tripla que entra nesse metodo é o "grupo de teste" e todo o resto eh o "grupo de treinamento" */
	public void PredizerNota(int userId, int filmeId, int rating, int numFilmesSimilares)
	{
		//obtemos o XML do filme que foi passado
		Filme f = XMLParser.parseXML(Paths.get("out/" + filmeId + ".xml"));
		
		if (f == null)
			return;
		
		//calculamos as similaridades para o filme passado
		LuceneSearch luceneSearch = new LuceneSearch(f, luceneDB, numFilmesSimilares);
		List<LuceneResult> listaSimilares = luceneSearch.getListaSimilaridadeGeneros();
		
		if (listaSimilares == null)
			return;
		
		//obtemos a média das notas do filme, desconsiderando a tripla atual (que foi passada como parametro)
		float media_i = userParser.mediaRatingFilme(filmeId, userId);
		
		/*  algoritmo  */
		float soma = 0;
		float sim_soma = 0;
		
		//pega as avaliacoes dos filmes similares a filmeId que foram avaliadas por userId
		for (LuceneResult lr : listaSimilares)
		{
			FilmeRating filmeRating = userParser.getTripla(userId, lr.getId());
			
			if (filmeRating == null)
				continue;
			
			float media_j = userParser.mediaRatingFilme(lr.getId(), userId);
			soma += lr.getSimilaridade() * (filmeRating.getRating() - media_j);
			sim_soma += lr.getSimilaridade();
		}
		
		float nota_predita_u_i = media_i + soma/sim_soma;
		
		System.out.println(nota_predita_u_i);
	}
}
