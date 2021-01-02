import javax.swing.table.*;
import java.util.*;
import java.time.*;
import java.time.format.*;
import java.util.stream.Collectors;

public class PesquisarParticipantesTableModel extends AbstractTableModel {
    
    // Objeto que irá nos fornecer os dados dos participantes
    private RifaMaster rifaMaster;
    
    private List<Participante> lista;
    private List<Participante> resultados;
    
    
    // Vetores com informações sobre como exibir a tabela
    String[] nomeDasColunas = {"Código", "Nome", "Tickets", "Investimento", "Dia da Compra" };
    Class[] classesDaTabela = {Integer.class, String.class, Integer.class, String.class, LocalDate.class };
    
    // Formatador de data para que possamos mostrar a data como 12 Nov 2017
    final private static DateTimeFormatter dataFormatada = DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy");
   
    public void setRifaMaster(RifaMaster rifa) { this.rifaMaster = rifa; }
    
    public PesquisarParticipantesTableModel(RifaMaster rifa){
        this.rifaMaster = rifa;
        lista = new ArrayList<Participante>(rifaMaster.getParticipantes().values());
        resetarFiltros();
    }
    
    public int getRowCount() {
        return resultados == null? 0 : resultados.size();
        
    }
    public int getColumnCount() {
        return nomeDasColunas.length;
    }
    
    public Class<?> getColumnClass(int columnIndex) {
        return classesDaTabela[columnIndex];
    }
    
    public String getColumnName(int columnIndex) {
        return nomeDasColunas[columnIndex];
    }

    public Object getValueAt(int row, int column) {
        Participante p = resultados.get(row);
        
        switch(column){
            case 0: return p.getCodigo();
            case 1: return p.getNome();
            case 2: return p.getTicketsAdquiridos();
            case 3: double valorTotal = p.getTicketsAdquiridos() * rifaMaster.getValorDoTicket();
                    return String.format("R$ %.2f", valorTotal);
            case 4: return p.getDiaDaCompra().format(dataFormatada);
            default: return "";
        }
    }
        
    public void filtrarPorCodigo(int i){
        resetarFiltros();
        Participante p = rifaMaster.obterParticipante(i);
        if (p == null) {
            resultados = new ArrayList<Participante>();
        } else {
            resultados.clear();
            resultados.add(p);
        }
        fireTableDataChanged();
    }
    
    public void filtrarPorNome(String nome){
        resetarFiltros();
        resultados = lista.stream()
                        .filter(p -> p.getNome().toUpperCase().contains(nome.toUpperCase()))
                        .collect(Collectors.toCollection(ArrayList::new));
        fireTableDataChanged();
    }
    
    public void filtrarPorTickets(int tickets){
        resetarFiltros();
        resultados = lista.stream()
                        .filter(p -> p.getTicketsAdquiridos() >= tickets)
                        .collect(Collectors.toList()); 
        fireTableDataChanged();
    }
    
    public void resetarFiltros(){
         resultados = new ArrayList<Participante>(rifaMaster.getParticipantes().values());
    }
    
    public void resetarFiltros(boolean atualizarTabela){
        resetarFiltros();
        if (atualizarTabela) fireTableDataChanged();
    }
}