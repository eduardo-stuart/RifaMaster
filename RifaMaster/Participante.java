import java.util.*;
import java.io.*;
import java.time.*;
import java.time.format.*;

public class Participante implements Serializable {

    public static int codigoAnterior = 0;
    private int codigo;
    private String nome;
    private int ticketsAdquiridos;
    LocalDate diaDaCompra;
    RifaMaster rifaMaster;
    
    // Formatador de data para que possamos mostrar a data como 12 Nov 2017
    final private static DateTimeFormatter dataFormatada = DateTimeFormatter.ofPattern("dd MMMM yyyy");
    
    // Não será possível criar um objeto dessa classe sem algumas propriedades-chave.
    // Portanto, não iremos usar um construtor padrão para somente depois atribuir
    // valores a essas propriedades.
    public Participante(String nome, int ticketsAdquiridos, LocalDate diaDaCompra, RifaMaster rifaMaster){
       this.codigo = ++codigoAnterior;
       this.nome = nome;
       this.ticketsAdquiridos = ticketsAdquiridos;
       this.diaDaCompra = diaDaCompra;
       this.rifaMaster = rifaMaster;
    }
    
    public Participante(String nome, int ticketsAdquiridos, RifaMaster rifaMaster){
        this(nome, ticketsAdquiridos, LocalDate.now(), rifaMaster);
    }
    
    public int getCodigo() { return codigo; }
    public void setNome(String nome) { this.nome = nome; }
    public String getNome() { return nome; }
    public void setTicketsAdquiridos(int tickets) { this.ticketsAdquiridos = tickets; }
    public int getTicketsAdquiridos() { return ticketsAdquiridos; }
    public void setDiaDaCompra(LocalDate diaDaCompra) { this.diaDaCompra = diaDaCompra; }
    public LocalDate getDiaDaCompra() { return diaDaCompra; }
    
    public String toString() {
        return String.format("Código: %d\nNome: %s\nRifa: %s\nTicketsAdquiridos: %d\nValor Investido: %.2f\nData da Compra: %s\n", codigo, nome, rifaMaster.getTitulo(), ticketsAdquiridos, ticketsAdquiridos * rifaMaster.getValorDoTicket(), diaDaCompra.format(dataFormatada));
    }
    
    public boolean equals(Object obj) {
        // Dois objetos serão iguais se seus códigos forem idênticos
        // Antes, porém, vamos verificar se é possível compará-los...
        if (obj == null) return false;
        if (!(obj instanceof Participante)) return false;
        
        Participante outro = (Participante)obj;
        return codigo == outro.getCodigo();
    }
    
    public int hashCode(){
        return (String.format("%d%s", codigo, nome)).hashCode();
    }
    
    public int compareTo(Participante p){
        return new Integer(codigo).compareTo(p.getCodigo());
    }

}