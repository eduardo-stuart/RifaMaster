import javax.swing.table.*;
import java.util.*;
import java.time.*;
import java.time.format.*;

public class ParticipantesTableModel extends AbstractTableModel {
    // Objeto que irá nos fornecer os dados dos participantes
    private RifaMaster rifaMaster;
    
    // Vetores com informações sobre como exibir a tabela
    String[] nomeDasColunas = {"Código", "Nome", "Tickets", "Investimento", "Dia da Compra" };
    Class[] classesDaTabela = {Integer.class, String.class, Integer.class, String.class, LocalDate.class };
    
    // Formatador de data para que possamos mostrar a data como 12 Nov 2017
    final private static DateTimeFormatter dataFormatada = DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy");
   
    public void setRifaMaster(RifaMaster rifa) { this.rifaMaster = rifa; }
    
    public ParticipantesTableModel(RifaMaster rifa){
        this.rifaMaster = rifa;
        
    }
    
  
    public int getRowCount() {
        return rifaMaster == null? 0: rifaMaster.getParticipantes().size();
        
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
        LinkedHashMap lsh = (LinkedHashMap)rifaMaster.getParticipantes();
        Object obj = lsh.get( (lsh.keySet().toArray())[row]);
        
        Participante p = (Participante)obj;
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
    
}