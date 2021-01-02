import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class AdicionarParticipanteGUI extends JDialog implements ActionListener, FocusListener {
    
    private JTextField nomeTF = new JTextField("Nome do Participante...");
    private JTextField ticketsTF = new JTextField("1");
    private JButton okBtn = new JButton("OK");
    private JButton cancelarBtn = new JButton("Cancelar");
    
    private int tickets = 1;
    private boolean gravarAlteracoes = false;
    
    public String getNome() { return nomeTF.getText(); }
    public void setNome(String nome) { nomeTF.setText(nome); }
    public int getTickets() { return Integer.parseInt(ticketsTF.getText()); }
    public void setTickets(int tickets) {
        this.tickets = tickets;
        ticketsTF.setText(String.valueOf(tickets));
    }
    public boolean gravarNovoParticipante() { return gravarAlteracoes; }
    
    public void actionPerformed(ActionEvent event) {
        Object botao = event.getSource();
        if (botao != cancelarBtn) {
            gravarAlteracoes = true;
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
        if (obj == ticketsTF) {
            // Tenta converter o texto inserido na caixa de texto para um inteiro
            // Se der erro, avise o usuário que ele deve inserir um valor válido
            JTextField componente = (JTextField)obj;
            try {
                int valor = Integer.parseInt(componente.getText());
                tickets = valor;
            } catch (Exception erro) {
                JOptionPane.showMessageDialog(this, "Erro ao fornecer a quantidade de tickets adquiridos. Por favor, tente novamente...", "Erro...", JOptionPane.ERROR_MESSAGE);
                componente.setText(String.valueOf(tickets));
                componente.selectAll();
                componente.requestFocus();
            }
            
        }
    }
    
    private void gerarTela(){
        JPanel superior = new JPanel(new GridLayout(0, 1, 10, 10));
        Color barrasColor = new Color(128, 21, 21);
        Color barraCentral = new Color(212, 106, 106);
        Color textoColor = new Color(255, 170, 170);
        superior.setBackground(barrasColor);
        JLabel head = new JLabel("Participante...", SwingConstants.RIGHT);
        head.setFont(new Font("Tahoma", Font.ITALIC, 20));
        head.setForeground(Color.WHITE);
        superior.add(head);
       
        JPanel meio = new JPanel(new GridLayout(0, 2, 10, 10));
        meio.setMaximumSize(meio.getPreferredSize());
        meio.setBackground(barraCentral);
        JLabel imagem = new JLabel(new ImageIcon("lucky.png"));
        meio.add(imagem);
        
        JPanel infoParticipante = new JPanel(new GridLayout(0, 1, 15, 15));
        infoParticipante.setBackground(barraCentral);
        
        JLabel nomeP = new JLabel("Nome do Participante:", SwingConstants.RIGHT);
        nomeP.setForeground(Color.WHITE);
        infoParticipante.add(nomeP);
        infoParticipante.add(nomeTF);
        
        JLabel qtd = new JLabel("Quantos tickets foram adquiridos?", SwingConstants.RIGHT);
        qtd.setForeground(Color.WHITE);
        infoParticipante.add(qtd);
        infoParticipante.add(ticketsTF);
        
        meio.add(infoParticipante);
        
        JPanel inferior = new JPanel();
        inferior.setBackground(barrasColor);
        inferior.add(okBtn);
        inferior.add(cancelarBtn);
        
        JPanel geral = new JPanel(new BorderLayout());
        geral.add(superior, BorderLayout.NORTH);
        geral.add(meio, BorderLayout.CENTER);
        geral.add(inferior, BorderLayout.SOUTH);
        
        getContentPane().add(geral);
    }
    
    public AdicionarParticipanteGUI() {
        okBtn.addActionListener(this);
        cancelarBtn.addActionListener(this);
        nomeTF.addFocusListener(this);
        ticketsTF.addFocusListener(this);
        
        getRootPane().setDefaultButton(okBtn);
        
        setModal(true);
        setTitle("Adicionar Novo Participante...");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        gerarTela();
        pack();
    }
    
    public static void main(String... args){
        AdicionarParticipanteGUI p = new AdicionarParticipanteGUI();
    }
    
}