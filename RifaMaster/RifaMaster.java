import java.util.*;
import java.util.stream.*;
import java.time.*;
import java.time.format.*;
import java.io.*;

public class RifaMaster implements Serializable {
    
    private String titulo;
    private String premio;
    private double valorDoPremio;
    private double valorDoTicket;
    private LocalDate dataDoSorteio;
    private static DateTimeFormatter dataFormatada = DateTimeFormatter.ofPattern("dd MMMM yyyy");
    Map<Integer, Participante> participantes = new LinkedHashMap<>();
    private Participante ganhador;
    
    // Propriedade que guarda o número do último registro de participante que foi gerado
    // Esse valor é gravado juntamente com o arquivo do objeto e atualizado assim que
    // esse arquivo é carregado na memória.
    private int ultimoCodigoGerado = 0;
    
    public RifaMaster() {
       
    }
    
    public RifaMaster(String titulo, String premio, double valorPremio, double valorTicket, LocalDate dataDoSorteio){
        this.titulo = titulo;
        this.premio = premio;
        this.valorDoPremio = valorPremio;
        this.valorDoTicket = valorTicket;
        this.dataDoSorteio = dataDoSorteio;
    }
    
    public RifaMaster(String titulo, String premio, double valorPremio, double valorTicket, int dia, int mes, int ano) {
        this(titulo, premio, valorPremio, valorTicket, LocalDate.of(dia, mes, ano));
    }
    
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getTitulo() { return titulo; }
    public void setPremio(String premio) { this.premio = premio; }
    public String getPremio() { return premio; }
    public void setValorDoPremio(double valorDoPremio) { this.valorDoPremio = valorDoPremio; }
    public double getValorDoPremio() { return valorDoPremio; }
    public void setValorDoTicket(double valor) { this.valorDoTicket = valor; }
    public double getValorDoTicket() { return valorDoTicket; }
    public Map<Integer, Participante> getParticipantes() { return participantes; }
    public LocalDate getDataDoSorteio() { return dataDoSorteio; }
    public void setDataDoSorteio(LocalDate data) { this.dataDoSorteio = data; }
    public Participante getGanhador() { return ganhador; } // Somente leitura
    
    public String toString(){
        return String.format("Rifa: %s\nPrêmio: %s\nValor do Prêmio: R$%.2f\nValor do Ticket: %.2f\nQuantidade de Tickets para Evitar Prejuízo: %d\nQuantidade de Tickets Vendidos: %d\nQuantidade de Tickets que faltam Serem Vendidos: %d\nTotal de Participantes: %d\n", titulo, premio, valorDoPremio, valorDoTicket, quantidadeDeTicketsNecessariosParaEvitarPrejuizo(), totalDeTicketsVendidos(), quantidadeDeTicketsASeremVendidos(), participantes.size());
    }
    
    // Métodos responsáveis por gravar e recuperar o arquivo com os dados da rifa
    public static boolean salvarRifa(RifaMaster rifa, String nomeDoArquivo) {
        try {
            Participante umParticipante = rifa.getParticipantes().values().stream().findFirst().get();
            rifa.ultimoCodigoGerado = umParticipante.codigoAnterior;
        } catch (Exception erro) {
            // Provavelmente ainda não há participantes.
            // Iremos então definir o ultimoCodigo como 0
            rifa.ultimoCodigoGerado = 0;
        }
         
        try(ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(nomeDoArquivo))) {
            out.writeObject(rifa);
        } catch (Exception e) {
            return false;
        }
        // Chegou até aqui? Sucesso!
        return true;
    }
    
    public static RifaMaster carregarRifa(String nomeDoArquivo) {
        RifaMaster arquivoLido = null;
        try(ObjectInputStream in = new ObjectInputStream(new FileInputStream(nomeDoArquivo))) {
            arquivoLido = (RifaMaster)in.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Se conseguiu carregar o arquivo, retorna um objeto pronto.
        // Vamos configurar o último codigo gerado em Participantes...
        Participante umParticipante = null;
        if (arquivoLido != null) {
            if (arquivoLido.getParticipantes().size() > 0) {
                // Somente devemos obter este valor se houver participantes gravados na coleção
                umParticipante = arquivoLido.getParticipantes().values().stream().findFirst().get();
                if (umParticipante != null) {
                    umParticipante.codigoAnterior = arquivoLido.ultimoCodigoGerado;
                }
            }
        }
        // Se houve algum problema, retornará null. Quem chama este método deverá tratar o erro...
        return arquivoLido;
    }
    
    // Método utilitário para obter um resumo sobre um participante
    public String resumoDoParticipante(Participante p) {
        return String.format("Código: %d\r\nNome: %s\r\nTotal de Tickets: %d\r\nData da Compra: %s", p.getCodigo(), p.getNome(), p.getTicketsAdquiridos(), p.getDiaDaCompra());
    }
    
    // Método que irá resetar todas as informações atuais da rifa
    // Útil para criar novas rifas sem precisar destruir/reconstruir um objeto
    public void resetarRifa(){
        titulo = "";
        premio = "";
        valorDoPremio = 0;
        valorDoTicket = 0;
        dataDoSorteio = LocalDate.now();
        participantes.clear();
    }
    
    public boolean gravarRelatorio(String nomeDoArquivo){
        // Método que irá gravar um recibo em  formato texto após a conclusão de um sorteio
        String tituloDoRecibo = "RifaMaster v1.0b\r\n==========\r\n\r\nRelatorio:\r\n";
        String linha1 = String.format("Rifa: %s\r\nPrêmio, no valor de R$%.2f: %s\r\n", titulo, valorDoPremio, premio);
        String linha2 = String.format("Valor de cada Ticket: R$%.2f\r\nTickets vendidos: %d\r\nReceita total: R$%.2f\r\n", valorDoTicket, totalDeTicketsVendidos(), valorDoTicket * totalDeTicketsVendidos());
        String linha3;
        if (ganhador != null)   
            linha3 = String.format("\r\nGrande ganhador:\r\n%s\r\n", resumoDoParticipante(ganhador));
        else
            linha3 = "\r\nGrande ganhador:\r\nSorteio não realizado.\r\n";
        String linha4 = String.format("\r\nNúmero total de Participantes: %d\r\n", participantes.size());
        String linha5 = String.format("\r\nRelação de Todos os Participantes:\r\n");
        
        String dadosDosParticipantes = new String();
        Collection<Participante> osParticipantes = participantes.values();
        for (Participante p: osParticipantes) dadosDosParticipantes += resumoDoParticipante(p) + "\r\n\r\n";
        String divisor = String.format("\r\n==============================\r\n");
        String footer= "RifaMaster: Gerenciador de Contribuições com Possibilidade de Resgate de um Prêmio Mediante Sorteio.\r\n";
        String subfooter = "Software desenvolvido por Eduardo Stuart e Ygor Prata para a disciplina Programação Orientada a Objetos II.\r\n";
        String subfooter2 = "Professor Marco Aurélio N. Esteves - Universidade Veiga de Almeida - 2017.\r\n";
        String resultadoFinal = tituloDoRecibo + linha1 + linha2 + linha3 + linha4 + linha5 + dadosDosParticipantes + divisor + footer + subfooter + subfooter2;
      
        try(PrintWriter pw = new PrintWriter(nomeDoArquivo, "UTF-8")){
            pw.write(resultadoFinal);
        } catch (Exception e) {
            return false;
        }
        
        // Chegou até aqui? Sucesso!
        return true;
    }
    
    // Métodos responsáveis pelas funções CRUD
    public boolean adicionarParticipante(Participante novo) {
        
        // Talvez ocorra um problema ao tentar acrescentar um novo elemento no mapa...
        try {
            participantes.put(novo.getCodigo(), novo);
        } catch (Exception e) {
            // Deu algum problema -- talvez o objeto não seja da classe esperada
            // Vamos indicar a falha retornando falso.
            return false;
        }
        // Chegou até aqui? Sucesso! Retorna verdadeiro.
        return true;
    }
    
    
    public boolean removerParticipante(int codigo) {
        // Remove o participante cujo código foi passado no parâmetro
        // Devolve true se foi um sucesso ou false se houve erro
        // Dois erros podem ocorrer: o código passado não corresponde a nenhum objeto ou 
        // algum outro problema ao tentar remover um objeto. Vamos proteger esse pedaço de código
        try {
            // Vamos testar se há um objeto com esse código...
            if(!(participantes.containsKey(codigo)))
                return false; // Chave/código inexistente. Retorna false
            else
                participantes.remove(codigo); // Tenta removê-lo do map...
        } catch (Exception e) {
            // Ops
            return false;
        }
        // Chegou até aqui? Sucesso!
        return true;
    }
    
    public Participante obterParticipante(int codigo){
        // Retorna o objeto Participante cujo código está especificado no parâmetro
        // Se não houver um objeto correspondente, retorna null.
        // Quem chamou esse método que deve lidar com esse erro...
        return participantes.get(codigo);
    }
    
    // Métodos responsáveis pelas demais funções da classe
    // Vamos sortear o grande vencedor...
    public Participante sortearParticipante(){
        // Existem participantes? Se não, retorna null
        if (participantes.size() == 0) return null;
        
        // A lista será preenchida da seguinte forma: iremos obter o número de tickets adquiridos pelo participante
        // e colocar uma referência do seu objeto esse determinado número de vezes no ArrayList...
        // Por exemplo, o participante de código 2 comprou 3 tickets: esse participante estará presente 3 vezes neste vetor
        // O participante de código 4 comprou 15 tickets: esse participante estará presente 15 vezes neste vetor
        List<Participante>candidatos = new ArrayList<>();
        
        participantes.forEach((k, v) -> {
            Participante p = (Participante)v;
            // Por segurança, vamos testar se o número de tickets é maior do que zero.
            // Somente iremos incluir no vetor quem realmente adquiriu um ou mais tickets.
            int totalDeTickets = p.getTicketsAdquiridos();
            if (totalDeTickets > 0){
                // Vamos acrescentar referências desse candidato o número de vezes em que ele adquiriu os tickets
                for(int i = 0; i < totalDeTickets; i++)
                    candidatos.add(p);
            }  
        });
        
        // Com o ArrayList devidamente preenchido, vamos dar início ao sorteio...
        // Primeiro, vamos "embaralhar" o vetor...
        Collections.shuffle(candidatos);
        // Vamos escolher um número aleatório entre 0 e o número de elementos do ArrayList...
        Random random = new Random();
        // E retornar o grande vencedor!
        ganhador = candidatos.get(random.nextInt(candidatos.size()));
        return ganhador;
    }
    
    public int quantidadeDeTicketsNecessariosParaEvitarPrejuizo(){
        // Para evitar uma divisão por zero
        if (valorDoTicket == 0) return 0;
        // O prêmio não tem valor declarado? Não há como calcular um suposto prejuízo
        if (valorDoPremio == 0) return 0;
        int ticketsEstimados = (int)(valorDoPremio / valorDoTicket);
        if (valorDoPremio % valorDoTicket != 0) ticketsEstimados++;
        return ticketsEstimados;
    }
    
    public int totalDeTicketsVendidos() {
        // Usando Java 8! \o/
        // Percorra o mapa e some todos os tickets que foram vendidos até o momento
        return participantes.values().stream().mapToInt(Participante::getTicketsAdquiridos).sum();
    }
    
    public int quantidadeDeTicketsASeremVendidos(){
        int paraEvitarPrejuizo =  quantidadeDeTicketsNecessariosParaEvitarPrejuizo() - totalDeTicketsVendidos();
        if (paraEvitarPrejuizo < 0) paraEvitarPrejuizo = 0;
        return paraEvitarPrejuizo;
    }
    
    
}