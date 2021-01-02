import javax.swing.*;
import javax.swing.text.*;
import javax.swing.JFormattedTextField.AbstractFormatter;
import java.awt.*;
import java.awt.event.*;
import java.time.*;
import java.util.*;
import java.text.*;
import org.jdatepicker.*;
import org.jdatepicker.util.*;
import org.jdatepicker.impl.*;

public class DetalhesRifaGUI extends JDialog implements ActionListener, FocusListener {
    
    private LocalDate dataDoSorteio;
    
    private JTextField nomeTextField = new JTextField("Informe o título da rifa...");
    private JTextField nomeDoPremioTextField = new JTextField("Informe o prêmio...");
    private JTextField valorDoPremioTextField = new JTextField("R$ 0.00");
    private double valorDoPremio = 0.0;
    private JTextField valorDoTicketTextField = new JTextField("R$ 0.00");
    private double valorDoTicket = 0.0;
    
    // JDataPicker
    private UtilDateModel model = new UtilDateModel();  
    private JDatePanelImpl datePanel;
    private JDatePickerImpl datePicker;
    
    // Botões que fecham essa janela e devolvem o controle para o programa principal
    private JButton salvarBtn = new JButton("Salvar");
    private JButton cancelarBtn = new JButton("Cancelar");
    
    // Devemos salvar as alterações:
    private boolean salvarAlteracoes = false;
    
    public String getNomeDaRifa() { return nomeTextField.getText(); }
    public void setNomeDaRifa(String nome) { nomeTextField.setText(nome); }
    public String getPremio() { return nomeDoPremioTextField.getText(); }
    public void setPremio(String premio) { nomeDoPremioTextField.setText(premio); }
    public double getValorDoPremio() { return valorDoPremio; }
    public void setValorDoPremio(double valor) {
        valorDoPremioTextField.setText(String.format("R$ %.2f", valor)); 
        this.valorDoPremio = valor;
    }
    public double getValorDoTicket() { return valorDoTicket; }
    public void setValorDoTicket(double valor) {
        valorDoTicketTextField.setText(String.format("R$ %.2f", valor));
        this.valorDoTicket = valor;
    }
    public LocalDate getDataDoSorteio() { return dataDoSorteio; }
    public void setDataDoSorteio(LocalDate data) { this.dataDoSorteio = data; }
    public boolean getSalvarAlteracoes() { return salvarAlteracoes; }
    
    public void actionPerformed(ActionEvent event) {
        Object botao = event.getSource();
        if (botao == salvarBtn) {
            salvarAlteracoes = true;
            // Convete objeto do tipo Date para LocalDate.
            dataDoSorteio = ((Date)datePicker.getModel().getValue()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        }
        dispose();
    }
    
    public void focusGained(FocusEvent e){
        // Selecione todo o texto presente na caixa para facilitar a edição
        JTextField component = (JTextField)e.getSource();
        component.selectAll();
    }
    
    public void focusLost(FocusEvent e){
        Object obj = e.getSource();
        if (obj==valorDoPremioTextField || obj == valorDoTicketTextField){
            // Tente converter o seu conteúdo para double.
            // Se der erro, avise ao usuário que ele deve inserir um valor válido
            JTextField componente = (JTextField)obj;
            try {
                double valor = Double.parseDouble(componente.getText());
                if (obj==valorDoPremioTextField) valorDoPremio = valor; else valorDoTicket = valor;
                componente.setText(String.format("R$ %.2f", valor));
            } catch (Exception exception) {
                JOptionPane.showMessageDialog(this, "Erro ao fornecer um valor válido. Por favor, tente novamente...");
                componente.setText("0.0");
                componente.selectAll();
                componente.requestFocus();
            }
        }
    }
    
    public void gerarTela(){
        JPanel painel = new JPanel(new GridLayout(1, 2, 25, 0));
        JLabel imageLbl = new JLabel(new ImageIcon("rifa.png"));
        painel.add(imageLbl);
        
        JPanel detalhes = new JPanel(new GridLayout(11, 1));
        detalhes.setLayout(new BoxLayout(detalhes, BoxLayout.Y_AXIS));
        JLabel tituloLbl = new JLabel("Nome da Rifa:", SwingConstants.RIGHT);
        detalhes.add(tituloLbl);
        detalhes.add(nomeTextField);
        
        JLabel premioLbl = new JLabel("Prêmio:", SwingConstants.RIGHT);
        detalhes.add(premioLbl);
        detalhes.add(nomeDoPremioTextField);
        
        JLabel valorDoPremioLbl = new JLabel("Valor do Prêmio (R$):", SwingConstants.RIGHT);
        JLabel valorDoTicketLbl = new JLabel("Valor do Ticket (R$):", SwingConstants.RIGHT);
        
        detalhes.add(valorDoPremioLbl);
        detalhes.add(valorDoPremioTextField);
        detalhes.add(valorDoTicketLbl);
        detalhes.add(valorDoTicketTextField);
        
        JLabel dataDoSorteioLbl = new JLabel("Data do Sorteio:", SwingConstants.RIGHT);
        detalhes.add(dataDoSorteioLbl);
        
        // Configurando o componente de data
        model.setSelected(true);
        Properties p = new Properties();
        p.put("text.today", "Hoje");
        p.put("text.month", "Mês");
        p.put("text.year", "Ano");
        datePanel = new JDatePanelImpl(model, p);
        datePicker = new JDatePickerImpl(datePanel, new AbstractFormatter(){
            private String datePattern = "dd-MMMM-yyyy";
            private SimpleDateFormat dateFormatter = new SimpleDateFormat(datePattern);

            public Object stringToValue(String text) throws ParseException {
                return dateFormatter.parseObject(text);
            }

            public String valueToString(Object value) throws ParseException {
                if (value != null) {
                    Calendar cal = (Calendar) value;
                    return dateFormatter.format(cal.getTime());
                }
                return "";
            }
    
        });
        
        detalhes.add(datePicker);
        
        JPanel painelDosBotoes = new JPanel(new GridLayout(1, 2, 5, 5));
        salvarBtn.addActionListener(this);
        cancelarBtn.addActionListener(this);
        painelDosBotoes.add(salvarBtn);
        painelDosBotoes.add(cancelarBtn);
        
        detalhes.add(painelDosBotoes);
        
        painel.add(detalhes);
        getContentPane().add(painel);
    }
    
    public DetalhesRifaGUI(){
        nomeTextField.addFocusListener(this);
        nomeDoPremioTextField.addFocusListener(this);
        valorDoPremioTextField.addFocusListener(this);
        valorDoTicketTextField.addFocusListener(this);
        setModal(true);
        setTitle("Criar Nova Rifa");
        setSize(450, 320);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        gerarTela();
    }
    
   
    
    public static void main(String... args){
       DetalhesRifaGUI rifa = new DetalhesRifaGUI();
    }
}