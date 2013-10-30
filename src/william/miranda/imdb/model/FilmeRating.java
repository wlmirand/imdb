package william.miranda.imdb.model;

/* Container para os ratings dos usuarios que forem parseados do u.data
 * No programa, esse arquivo eh parseado em uma estrutura do tipo:
 * --------------------------------
 * user_1: [rating_1, rating_2, rating_3, ..., rating_N]
 * user_2: [rating_1, rating_2, rating_3, ..., rating_N]
 * user_3: [rating_1, rating_2, rating_3, ..., rating_N]
 * user_4: [rating_1, rating_2, rating_3, ..., rating_N]
 * --------------------------------
 * A estrutura eh armazenada em um HashMap, cuja chave eh o userId.
 * e o value é uma lista de FilmeRating */
public class FilmeRating
{
	private int filmeId;
	private int rating;
	
	public FilmeRating(int filmeId, int rating)
	{
		this.filmeId = filmeId;
		this.rating = rating;
	}
	
	public int getFilmeId() {
		return filmeId;
	}
	
	public int getRating() {
		return rating;
	}
}