package william.miranda.recomendacao;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import william.miranda.imdb.model.Filme;
import william.miranda.imdb.model.FilmeRating;
import william.miranda.imdb.parser.ResultadoPredicao;
import william.miranda.imdb.parser.UserParser;
import william.miranda.lucene.LuceneDatabase;
import william.miranda.lucene.LuceneResult;
import william.miranda.lucene.LuceneSearch;
import william.miranda.lucene.LuceneSearch.TipoSimilaridade;
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
	private LuceneDatabase luceneDB;
	
	//estrutura que guarda os Objetos do tipo Filme, que sao gerados a partir de seus XMLs
	private static Map<Integer, Filme> filmes = new HashMap<>();
	
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
	public List<ResultadoPredicao> percorrerAvaliacoes(int numFilmesSimilares, TipoSimilaridade tipoSimilaridade)
	{
		//lista que ira conter todos os resultados;
		List<ResultadoPredicao> resultados = new ArrayList<>();
		
		//utilizado para calcular o RMSE, para nao precisar rodar 2x o FOR
		double soma = 0;
		
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
				//float notaPredita = PredizerNota(userId, filmeId, rating, numFilmesSimilares, tipoSimilaridade);
				ResultadoPredicao resPred = PredizerNota(userId, filmeId, rating, numFilmesSimilares, tipoSimilaridade);
				
				//agora gravamos a quadrupla em um array
				resultados.add(resPred);
				
				//vai somando as parcelas para o calculo do RMSE
				soma += Math.pow(rating - resPred.getNotaPredita(), 2);
			}
		}
		
		//apos fazer todas as predicoes, calculamos o RMSE
		double rmse = Math.sqrt(soma/UserParser.getNumeroAvaliacoes());
		
		//adicionamos o RMSE como um resultado "dummy" no final dos resultados
		resultados.add(new ResultadoPredicao(-1, -1, -1, rmse));
		
		return resultados;
	}
	
	/* Este metodo implementa o algorito de Predição
	 * O objetivo eh "adiviinhar" a nota de um usuario U daria para um filme F
	 * baseado nas outras notas da base de dados.
	 * A tripla que entra nesse metodo é o "grupo de teste" e todo o resto eh o "grupo de treinamento" */
	public ResultadoPredicao PredizerNota(int userId, int filmeId, int rating, int numFilmesSimilares, TipoSimilaridade tipoSimilaridade)
	{
		//variavel que ira guardar a nota predita
		float nota_predita_u_i;
		
		//obtemos o XML do filme que foi passado
		Filme f = parseXmlSeNecessario(filmeId);
		
		//obtemos a média das notas do filmeId, desconsiderando a tripla atual (que foi passada como parametro)
		float media_i = userParser.mediaRatingFilme(filmeId, userId);
		
		//caso nao tenha o XML do filme (pois nao ha como calcular os dados)
		if (f == null)
		{
			nota_predita_u_i = media_i;
			return new ResultadoPredicao(userId, filmeId, rating, nota_predita_u_i);
		}
		
		//calculamos as similaridades para o filme passado
		LuceneSearch luceneSearch = new LuceneSearch(f, luceneDB, numFilmesSimilares);
		List<LuceneResult> listaSimilares = luceneSearch.getMetadado(tipoSimilaridade);
		
		//caso nao tenha como obter os filmes similares, retornaremos a media_i (nao ha o metadado no XML do filmeID)
		if (listaSimilares == null || listaSimilares.size() == 0)
		{
			nota_predita_u_i = media_i;
			return new ResultadoPredicao(userId, filmeId, rating, nota_predita_u_i);
		}
		
		/*  algoritmo  */
		float soma = 0;
		float sim_soma = 0;
		
		//verifica de fato quantos filmes similares foram retornados pela engine, de modo a comparar com N
		int numFilmesSimilaresRetornados = listaSimilares.size();
		
		//numero de vezes que nao houve resultados, precisando assim pular um item similar
		int numFilmesSimilaresSkipados = 0;
		
		//varre todos os filmes similares a F baseado no metadado passado
		for (LuceneResult lr : listaSimilares)
		{
			//verifica se o filme similar foi avaliado pelo usuário
			FilmeRating filmeRating = userParser.getTripla(userId, lr.getId());
			
			if (filmeRating == null)//se o usuario nao avaliou o filme "lr"
			{
				numFilmesSimilaresSkipados++;
				continue;
			}
			
			float media_j = userParser.mediaRatingFilme(lr.getId(), userId);//calcula a media da nota do filme similar
			soma += lr.getSimilaridade() * (filmeRating.getRating() - media_j);
			sim_soma += lr.getSimilaridade();
		}
			
		if (sim_soma != 0)//se deu tudo certo (existiu ao menos um filme similar que foi avaliado pelo usuário)
			nota_predita_u_i = media_i + (soma/sim_soma);
		else//se o usuario nao avaliou nenhum dos filmes similares
			nota_predita_u_i = media_i;
		
		if (nota_predita_u_i > 5)//trunca as notas para o teto (5.0)
			nota_predita_u_i = 5.0f;
		
		if (nota_predita_u_i < 0)//trunca as notas para o piso (0.0)
			nota_predita_u_i = 0.0f;
		
		//cria o objeto que ira armazenar todos os resultados e retorna
		return new ResultadoPredicao(userId, filmeId, rating, nota_predita_u_i, numFilmesSimilaresRetornados, numFilmesSimilaresSkipados);
		
		//return nota_predita_u_i;
	}
	
	/* Metodo utilizado para testes
	 * Nao sera usado devido a performance, pois o for roda 100 000 vezes.
	 * Esse calculo eh feito durante o a predicao das notas */
	public static double RMSE(List<ResultadoPredicao> resultados)
	{
		float soma = 0;
		
		for (ResultadoPredicao r : resultados)
		{
			soma += Math.pow(r.getNotaOriginal()-r.getNotaPredita(), 2);
		}
		
		//RMSE=sqrt(sum(nota_real -nota_predita)^2) / qtde_notas
		return Math.sqrt(soma/UserParser.getNumeroAvaliacoes());
	}
	
	/* este metodo preenche sob demanda a variavel "filme", de modo a parsear cada filme no maximo uma vez
	 * e nao fazer o parse para cada entrada do u.data */
	private Filme parseXmlSeNecessario(int filmeId)
	{
		//verificamos se o filme ja foi parseado
		if (filmes.containsKey(filmeId))
			return filmes.get(filmeId);
		
		//se ainda nao foi parseado, parseia
		Path path = Paths.get("out/" + filmeId + ".xml");//pega o path
		Filme f = XMLParser.parseXML(path);//parseia
		filmes.put(filmeId, f);	
		return f;
	}
}
