package william.miranda.imdb.model;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class Review
{
	private String titulo;
	private String autor;
	private int rating = -1;
	private String conteudo;
	private Calendar data;
	
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("---------------------------------------------------------------\n");
		sb.append("Titulo: ").append(titulo).append(" - ").append("Nota: ").append(rating).append('\n');
		sb.append("Autor: ").append(autor).append('\n');
		
		if (data != null)
		{
			sb.append("Data: ").append(printData()).append('\n');
		}
		
		sb.append("Conteudo: ").append(conteudo).append('\n');
		
		return sb.toString();
	}
	
	//gets e sets
	public String getTitulo() {
		return titulo;
	}
	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}
	public String getAutor() {
		return autor;
	}
	public void setAutor(String autor) {
		this.autor = autor;
	}
	public int getRating() {
		return rating;
	}

	public void setRating(int rating) {
		this.rating = rating;
	}

	public String getConteudo() {
		return conteudo;
	}
	public void setConteudo(String conteudo) {
		this.conteudo = conteudo;
	}
	
	public Calendar getData() {
		return data;
	}
	
	public void setData(String d)
	{
		String[] tmp = d.split(" ");
		
		int mes = 0;
		int dia = Integer.parseInt(tmp[0]);
		int ano = Integer.parseInt(tmp[2]);
		
		switch(tmp[1])
		{
			case "January":
				mes = 0;
				break;
				
			case "February":
				mes = 1;
				break;
				
			case "March":
				mes = 2;
				break;
				
			case "April":
				mes = 3;
				break;
				
			case "May":
				mes = 4;
				break;
				
			case "June":
				mes = 5;
				break;
				
			case "July":
				mes = 6;
				break;
				
			case "August":
				mes = 7;
				break;
				
			case "September":
				mes = 8;
				break;
				
			case "October":
				mes = 9;
				break;
				
			case "November":
				mes = 10;
				break;
				
			case "December":
				mes = 11;
				break;
		}
		
		this.data = new GregorianCalendar(ano, mes, dia);
	}
	
	public String printData()
	{
		return data.get(Calendar.DAY_OF_MONTH) + "/" + (data.get(Calendar.MONTH)+1) + "/" + data.get(Calendar.YEAR);
	}
}
