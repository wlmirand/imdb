package william.miranda.imdb.model;

import java.util.List;

public class Filme
{
	private String titulo;
	private int ano;
	private String sinopse;
	private List<String> diretores;
	private List<String> criadores;
	private List<String> atores;
	private float rating;
	
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(ano).append(" - ").append(titulo).append('\n');
		sb.append("Rating: ").append(rating).append('\n');
		sb.append("Sinopse: ").append(sinopse).append('\n');
		sb.append("Diretores: ").append(diretores).append('\n');
		sb.append("Criadores: ").append(criadores).append('\n');
		sb.append("Atores: ").append(atores).append('\n');
		
		return sb.toString();
	}
	
	public String getTitulo() {
		return titulo;
	}
	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}
	public int getAno() {
		return ano;
	}
	public void setAno(int ano) {
		this.ano = ano;
	}
	public String getSinopse() {
		return sinopse;
	}
	public void setSinopse(String sinopse) {
		this.sinopse = sinopse;
	}
	public List<String> getDiretores() {
		return diretores;
	}
	public void setDiretores(List<String> diretores) {
		this.diretores = diretores;
	}
	public List<String> getCriadores() {
		return criadores;
	}
	public void setCriadores(List<String> criadores) {
		this.criadores = criadores;
	}
	public List<String> getAtores() {
		return atores;
	}
	public void setAtores(List<String> atores) {
		this.atores = atores;
	}
	public float getRating() {
		return rating;
	}
	public void setRating(float rating) {
		this.rating = rating;
	}	
}
