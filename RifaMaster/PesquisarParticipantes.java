import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import javax.swing.table.AbstractTableModel;

public class PesquisarParticipantes extends JDialog implements ActionListener, FocusListener {
    
    private RifaMaster rifaMaster;
    
    JTextField codigoTF = new JTextField();
    JTextField nomeTF = new JTextField();
    JTextField ticketsTF = new JTextField();
    
    JButton btnResetar = new JButton("Resetar");
    JButton btnOK = new JButton("OK");
    
    JTable tabela = new JTable();
    
    public void setRifaMaster(RifaMaster rifa) { this.rifaMaster = rifa; }
    public RifaMaster getRifaMaster() { return rifaMaster; }
    
    // Painel superior com os campos que podem ser usados para a pesquisa
    private JPanel criarPainelSuperior(){
        JPanel painel = new JPanel(new GridLayout(0, 3, 5, 5));
        painel.setBorder(BorderFactory.createTitledBorder("Pesquisar por:"));
        // Etiquedas da pesquisa
        JLabel codLbl = new JLabel("Código:");
        JLabel nomeLbl = new JLabel("Nome:");
        JLabel ticketsLbl = new JLabel("Mínimo de Tickets:");
        painel.add(codLbl);
        painel.add(nomeLbl);
        painel.add(ticketsLbl);

        // Adicione as caixas de texto que irão receber as informações
        painel.add(codigoTF);
        painel.add(nomeTF);
        painel.add(ticketsTF);
        return painel;
    }
    
    private JPanel criarPainelComTabela(){
        JPanel painel = new JPanel();
        painel.setBorder(BorderFactory.createTitledBorder("Resultados:"));
        JScrollPane scroll = new JScrollPane(tabela);
        painel.add(scroll);
        return painel;
    }
    
    private JPanel criarPainelInferior(){
        JPanel painel = new JPanel();
        painel.add(btnResetar);
        painel.add(btnOK);
        return painel;
    }
    
    public void gerarTelaPrincipal(){
        JPanel painel = new JPanel(new BorderLayout());
        painel.add(criarPainelSuperior(), BorderLayout.PAGE_START);
        painel.add(criarPainelComTabela(), BorderLayout.CENTER);
        painel.add(criarPainelInferior(), BorderLayout.PAGE_END);
        getContentPane().add(painel);
    }
    public void actionPerformed(ActionEvent event) {
        Object obj = event.getSource();
        if (obj == codigoTF) {
            ((PesquisarParticipantesTableModel)tabela.getModel()).filtrarPorCodigo(Integer.parseInt(codigoTF.getText()));
            nomeTF.setText("");
            ticketsTF.setText("");
        } else if (obj == nomeTF) {
            ((PesquisarParticipantesTableModel)tabela.getModel()).filtrarPorNome(nomeTF.getText());    
            codigoTF.setText("");
            ticketsTF.setText("");
        } else if (obj == ticketsTF) {
            ((PesquisarParticipantesTableModel)tabela.getModel()).filtrarPorTickets(Integer.parseInt(ticketsTF.getText()));
            nomeTF.setText("");
            codigoTF.setText("");
        } else if (obj == btnResetar) {
            ((PesquisarParticipantesTableModel)tabela.getModel()).resetarFiltros(true);
            
        } else if (obj == btnOK) {
            dispose();
        }
    }
    
     public void focusGained(FocusEvent e){
    }
    
    public void focusLost(FocusEvent e){
    }
    
    public PesquisarParticipantes(RifaMaster rifa){
        setModal(true);
        
        this.rifaMaster = rifa;
        tabela = new JTable(new PesquisarParticipantesTableModel(rifa));
        
        codigoTF.addActionListener(this);
        nomeTF.addActionListener(this);
        ticketsTF.addActionListener(this);
      
        btnOK.addActionListener(this);
        btnResetar.addActionListener(this);
        
        setTitle("Pesquisar Participantes...");
        gerarTelaPrincipal();
        pack();
    }
    
    public static void main(String... args){
        PesquisarParticipantes p = new PesquisarParticipantes(new RifaMaster());
    }
}