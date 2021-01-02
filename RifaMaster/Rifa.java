import java.util.*;
import java.text.*;
import java.time.*;
import java.time.format.*;
import java.time.ZoneOffset;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.table.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class Rifa extends JFrame implements ActionListener {
    // Objeto que gerencia as rifas
    private RifaMaster rifaMaster;
    
    // Painel principal que irá mostrar uma mensagem inicial sobre como começar o usar o programa
    // Ou detalhes sobre uma rifa já criada
    JPanel painelPrincipal = new JPanel(new CardLayout());
    
    // Como iremos alterar o texto durante a criação de uma rifa, vamos declarar essa
    // etiqueta como global
    JLabel infoInicialLbl = new JLabel(infoInicial1, SwingConstants.CENTER);
    
    // Textos usados no painel de informação inicial
    final static String infoInicial1 = "<html>Clique no menu <b>Arquivo</b> e na opção <b>Nova Rifa</b> para começar...</html>";
    final static String infoInicial2 = "Criando nova rifa...";
    
    // Itens de menu Arquivo...
    private JMenuItem novoArquivo = new JMenuItem("Nova Rifa...");
    private JMenuItem salvarArquivo = new JMenuItem("Salvar Arquivo");
    private JMenuItem sair = new JMenuItem("Sair");
    
    // Itens de menu Editar
    private JMenuItem editarRifa = new JMenuItem("Editar Rifa");
    private JMenuItem pesquisar = new JMenuItem("Pesquisar");

    // Itens de menu Relatório
    private JMenuItem gerarRelatorio = new JMenuItem("Gerar Relatório");
    
    // Itens de menu Ajuda
    private JMenuItem sobre = new JMenuItem("Sobre...");
    
    // Botões do Toolbar
    private JButton novoTBB = new JButton("Novo");
    private JButton removerTBB = new JButton("Remover");
    private JButton editarTBB = new JButton("Editar");
    private JButton pesquisarTBB = new JButton("Pesquisar");
    private JButton sortearTBB = new JButton("Sortear Participante");
    private JButton salvarTBB = new JButton("Salvar Rifa");
    
    // Etiquetas com informações sobre a rifa
    // Estamos declarando-as como globais para podermos manipulá-las posteriormente via código
    JLabel lblNome = new JLabel("#nome", SwingConstants.CENTER);
    JLabel lblPremio = new JLabel("#prêmio", SwingConstants.CENTER);
    JLabel lblValorDoPremio = new JLabel("#valor do prêmio");
    JLabel lblValorDoTicket = new JLabel("#valor do ticket");
    JLabel lblTicketsVendidos = new JLabel("#tickets vendidos");
    JLabel lblTicketsASeremVendidos = new JLabel("#tickets a serem vendidos");
    JLabel lblDataDoSorteio = new JLabel("#data do sorteio", SwingConstants.CENTER);
    JLabel lblReceitaGerada = new JLabel("#receita gerada");
    JLabel lblGanhador = new JLabel("#ganhador");
    
    // Fontes
    Font fntComum = new Font("Tahoma", Font.PLAIN, 14);
    Font fntDestaque = new Font("Tahoma", Font.PLAIN, 18);
    
    // Para a tabela
    JTable tabela = new JTable();
    
    // Nome do arquivo...
    final static String nomeDoArquivo = "default.rifa";
    
    // Formatador de data para que possamos mostrar a data como 12 Nov 2017
    final private static DateTimeFormatter dataFormatada = DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy");
    
    // Se o usuário apenas abriu o aplicativo, mas não chegou a gerar uma rifa válida, não devemos gravar uma rifa vazia
    // Essa variável controla essa situação
    boolean rifaValida = false;
    
    // Método utilitário que irá gerar um nome de arquivo único para ser usado na hora de gravar um backup ou o relatório com detalhes da rifa
    private String gerarNomeDeArquivoUnico(){
        // O nome do arquivo terá o seguinte formato: título da rifa (sem espaços), seguido
        // do número de segundos desde a data padrão e a extensão rifa
        return ((rifaMaster.getTitulo()).trim()).replace(" ", "-") + (LocalDateTime.now()).toEpochSecond(ZoneOffset.of("Z")) + ".rifa";
    }
    
    private void configurarMenus(){
        JMenuBar menuBar = new JMenuBar();
        JMenu menuArquivo = new JMenu("Arquivo");
        JMenu menuEditar = new JMenu("Editar");
        JMenu menuRelatorio = new JMenu("Relatório");
        JMenu menuAjuda = new JMenu("Ajuda");
        JSeparator menuSeparador = new JSeparator();
        
        novoArquivo.addActionListener(this);
        menuArquivo.add(novoArquivo);
        menuArquivo.add(menuSeparador);
        salvarArquivo.addActionListener(this);
        menuArquivo.add(salvarArquivo);
        menuArquivo.add(menuSeparador);
        sair.addActionListener(this);
        menuArquivo.add(sair);
        pesquisar.addActionListener(this);
        editarRifa.addActionListener(this);
        menuEditar.add(editarRifa);
        menuEditar.add(pesquisar);
        gerarRelatorio.addActionListener(this);
        menuRelatorio.add(gerarRelatorio);
        sobre.addActionListener(this);
        menuAjuda.add(sobre);
        menuBar.add(menuArquivo);
        menuBar.add(menuEditar);
        menuBar.add(menuRelatorio);
        menuBar.add(Box.createHorizontalGlue());
        menuBar.add(menuAjuda);
        setJMenuBar(menuBar);
    }
    
    private JPanel painelTituloDaRifa() {
        // Cria e configura o painel com o header da rifa
        JPanel painel = new JPanel(new GridLayout(0, 1, 5, 5));
        
        lblNome.setFont(fntDestaque);
        painel.add(lblNome);
        
        lblPremio.setFont(fntDestaque);
        painel.add(lblPremio);
        
        lblDataDoSorteio.setFont(fntDestaque);
        painel.add(lblDataDoSorteio);
        
        painel.setBorder(BorderFactory.createTitledBorder("Rifa"));

        return painel;
    }
    
    private JPanel painelDetalhesRifa(){
        JPanel painel = new JPanel(new GridLayout(0, 2, 5, 5));
        
        JLabel lblVP = new JLabel("Valor do Prêmio:", SwingConstants.RIGHT);
        painel.add(lblVP);
        lblValorDoPremio.setFont(fntDestaque);
        painel.add(lblValorDoPremio);
        
        JLabel lblVT = new JLabel("Valor do Ticket:", SwingConstants.RIGHT);
        painel.add(lblVT);
        lblValorDoTicket.setFont(fntDestaque);
        painel.add(lblValorDoTicket);
        
        JLabel lblTV = new JLabel("Tickets Vendidos:", SwingConstants.RIGHT);
        painel.add(lblTV);
        lblTicketsVendidos.setFont(fntDestaque);
        painel.add(lblTicketsVendidos);
        
        JLabel lblTN = new JLabel("Tickets a serem Vendidos:", SwingConstants.RIGHT);
        painel.add(lblTN);
        lblTicketsASeremVendidos.setFont(fntDestaque);
        painel.add(lblTicketsASeremVendidos);
        
        JLabel lblRG = new JLabel("Receita gerada:", SwingConstants.RIGHT);
        painel.add(lblRG);
        lblReceitaGerada.setFont(fntDestaque);
        painel.add(lblReceitaGerada);
        
        JLabel lblG = new JLabel("Ganhador da Rifa:", SwingConstants.RIGHT);
        painel.add(lblG);
        lblGanhador.setFont(fntDestaque);
        painel.add(lblGanhador);
        
        painel.setBorder(BorderFactory.createTitledBorder("Detalhes"));
        
        return painel;
    }
    
    private JPanel painelParticipantes(){
        JPanel painel = new JPanel(new BorderLayout());
        painel.setBorder(BorderFactory.createTitledBorder("Participantes"));

        JToolBar toolBar = new JToolBar("Gerenciar Participantes");
        // Configure os listeners para os botões da Toolbar
        novoTBB.addActionListener(this);
        editarTBB.addActionListener(this);
        removerTBB.addActionListener(this);
        pesquisarTBB.addActionListener(this);
        sortearTBB.addActionListener(this);
        salvarTBB.addActionListener(this);
        
        // Adicione os botões no Toolbar
        toolBar.add(novoTBB);
        toolBar.add(editarTBB);
        toolBar.add(removerTBB);
        toolBar.add(pesquisarTBB);
        toolBar.addSeparator();
        toolBar.add(sortearTBB);
        toolBar.addSeparator();
        toolBar.add(Box.createHorizontalGlue());
        
        toolBar.add(salvarTBB);
        
        painel.add(toolBar, BorderLayout.PAGE_END);
        JScrollPane scroll = new JScrollPane(tabela);
        painel.add(scroll, BorderLayout.CENTER);
        
        return painel;
    }
    
    private JPanel mainPane(){
        JPanel painel = new JPanel(new BorderLayout());
        
        painel.add(painelTituloDaRifa(), BorderLayout.NORTH);
        painel.add(painelParticipantes(), BorderLayout.CENTER);
        painel.add(painelDetalhesRifa(), BorderLayout.SOUTH);
        
        return painel;
    }
    
    private void atualizarPainelDeDetalhesDaRifa(){
        lblNome.setText(rifaMaster.getTitulo());
        lblPremio.setText(rifaMaster.getPremio());
        lblValorDoPremio.setText(String.format("R$ %.2f", rifaMaster.getValorDoPremio()));
        lblValorDoTicket.setText(String.format("R$ %.2f", rifaMaster.getValorDoTicket()));
        lblTicketsVendidos.setText(String.valueOf(rifaMaster.totalDeTicketsVendidos()));
        lblTicketsASeremVendidos.setText(String.valueOf(rifaMaster.quantidadeDeTicketsASeremVendidos()));
        lblDataDoSorteio.setText("Data do Sorteio: " + rifaMaster.getDataDoSorteio().format(dataFormatada));
        lblReceitaGerada.setText(String.format("R$ %.2f", rifaMaster.getValorDoTicket() * rifaMaster.totalDeTicketsVendidos()));
        
        // Já temos um ganhador?
        String nomeDoGanhador;
        Participante p = rifaMaster.getGanhador();
        if (p != null)
            nomeDoGanhador = p.getNome();
        else
            nomeDoGanhador = "A ser determinado após o sorteio";
            
        lblGanhador.setText(nomeDoGanhador);
        ((AbstractTableModel)tabela.getModel()).fireTableDataChanged();
    }
    
    private void configurarDetalhesDaRifa(){
        JPanel painelInicial = new JPanel(new GridBagLayout());
        painelInicial.add(infoInicialLbl);
        
        painelPrincipal.add(painelInicial);
        painelPrincipal.add(mainPane());
        
        getContentPane().add(painelPrincipal);
        
    }
    
    private void salvarRifa(){
        // Somente devemos salvar uma rifa se ela for válida
        if (!rifaValida) return;
        if (!(RifaMaster.salvarRifa(rifaMaster, nomeDoArquivo))) {
            JOptionPane.showMessageDialog(this, "Erro ao tentar gravar o arquivo.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void novaRifa() {
        // Se já houver um arquivo default.rifa, cria um backup e zere todos os campos de rifaMaster.
        File arquivo = new File(nomeDoArquivo);
        if (arquivo.exists()){
            // Antes de reiniciar o objeto rifaMaster, vamos fazer um backup do seu estado atual
            RifaMaster.salvarRifa(rifaMaster, gerarNomeDeArquivoUnico());
        }
        // Agora podemos resetar o objeto
        rifaMaster.resetarRifa();
    }
    
    // Método utilitário para atribuir as novas informações sobre a rifa para o objeto rifaMaster
    private void atribuirInfoParaRifa(DetalhesRifaGUI d) {
        rifaMaster.setTitulo(d.getNomeDaRifa());
        rifaMaster.setPremio(d.getPremio());
        rifaMaster.setValorDoPremio(d.getValorDoPremio());
        rifaMaster.setValorDoTicket(d.getValorDoTicket());
        rifaMaster.setDataDoSorteio(d.getDataDoSorteio());
    }
    public void actionPerformed(ActionEvent event){
        Object src = event.getSource();
        if (src == novoArquivo){
            // Atualiza o texto informativo dizendo que a rifa está sendo criada...
            infoInicialLbl.setText(infoInicial2);
            // Cria a janela e a exiba para o usuário
            DetalhesRifaGUI novaRifa = new DetalhesRifaGUI();
            novaRifa.show(true);
            if (novaRifa.getSalvarAlteracoes()) {
                // O usuário forneceu os dados da rifa e pressionou salvar... crie uma nova rifa com essas informações]
                // Mas antes, se já existir uma rifa, grava um backup e resete o objeto
                novaRifa();
                // Ok, tudo pronto...
                atribuirInfoParaRifa(novaRifa);
                // Atualize o painel de detalhes da rifa...
                atualizarPainelDeDetalhesDaRifa();
                // Exiba o painel de detalhes da rifa...
                ((CardLayout)painelPrincipal.getLayout()).last(painelPrincipal);
                setSize(700, 600);
                rifaValida = true;
            } else {
                infoInicialLbl.setText(infoInicial1);
            }
        } else if (src == salvarArquivo || src == salvarTBB ) {
            salvarRifa();
        } else if (src == sair) {
            salvarRifa();
           dispose();
        } else if (src == editarRifa ) {
            DetalhesRifaGUI d = new DetalhesRifaGUI();
            d.setTitle("Editar Rifa...");
            // Vamos preencher a janela com os dados atuais
            d.setNomeDaRifa(rifaMaster.getTitulo());
            d.setPremio(rifaMaster.getPremio());
            d.setValorDoPremio(rifaMaster.getValorDoPremio());
            d.setValorDoTicket(rifaMaster.getValorDoTicket());
            d.setDataDoSorteio(rifaMaster.getDataDoSorteio());
            // Exiba a janela para o usuário e aguarde a confirmação se devemos atualizar os dados
            d.show(true);
            if(d.getSalvarAlteracoes()){
                atribuirInfoParaRifa(d);
            }
            atualizarPainelDeDetalhesDaRifa();   
        } else if (src == pesquisar || src == pesquisarTBB) {
            PesquisarParticipantes p = new PesquisarParticipantes(rifaMaster);
            p.show(true);
        } else if (src == gerarRelatorio) {
            String nomeTmp = gerarNomeDeArquivoUnico() + ".txt";
            rifaMaster.gravarRelatorio(nomeTmp);
            // Vamos mostrar o relatorio usando o editor de textos padrão do sistema operacional
            try {
                Desktop.getDesktop().open(new File(nomeTmp));
            } catch (Exception e) {
                // Por algum motivo não foi possível abrir o relatório.
                // Jogue as informações sobre o erro no terminal
                e.printStackTrace();
            }
   
        } else if (src == sobre) {
            String i1 = "<html><center><b>RifaMaster v1.0b</b><br>Trabalho para a disciplina Programação Orientada a Objetos II.";
            String i2 = "<br>Universidade <b>Veiga de Almeida</b><br><br>Alunos:<br><b>Eduardo Stuart<br>Ygor Prata</b><br>";
            String i3 = "<br>Professor: <br><b>Marco Aurélio</b><br><br><b>2017</b></center></html>";
            JOptionPane.showMessageDialog(this, i1 + i2 + i3, "Sobre...", JOptionPane.PLAIN_MESSAGE);
        } else if (src == novoTBB) {
            // O usuário escolheu criar um novo participante...
            AdicionarParticipanteGUI np = new AdicionarParticipanteGUI();
            np.show(true);
            if (np.gravarNovoParticipante()){
                // Vamos criar um novo participante e incluí-lo na coleção
                Participante p = new Participante(np.getNome(), np.getTickets(), LocalDate.now(), rifaMaster);
                rifaMaster.adicionarParticipante(p);
                atualizarPainelDeDetalhesDaRifa();
            }
        } else if (src == removerTBB) {
            int selectedRow = tabela.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Selecione um participante para ser removido...", "Nenhum Participante Selecionado", JOptionPane.ERROR_MESSAGE);
            } else {
                // Se o usuário ordenou a tabela, temos que converter o índice para o do modelo
                int participanteNoModelo = tabela.convertRowIndexToModel(selectedRow);
                // Vamos remover o participante
                int codParticipante = Integer.parseInt(tabela.getModel().getValueAt(participanteNoModelo, 0).toString());
                // Vamos confirmar se o usuário realmente deseja remover este participante
                Participante p = rifaMaster.obterParticipante(codParticipante);
                int n = JOptionPane.showConfirmDialog(this, "Tem certeza de que deseja apagar o seguinte participante?\n" + rifaMaster.resumoDoParticipante(p), "Confirma?", JOptionPane.YES_NO_OPTION);
                if (n == JOptionPane.YES_OPTION) {
                    rifaMaster.removerParticipante(codParticipante);
                    atualizarPainelDeDetalhesDaRifa();
                }
            }
        } else if (src == editarTBB) {
            // Vamos editar os dados do participante atualmente selecionado
            int selectedRow = tabela.getSelectedRow();
            if (selectedRow == -1){
                // Não há um registro selecionado para podermos editar. Avise ao usuário
                JOptionPane.showMessageDialog(this, "Selecione o participante que deseja editar...", "Nenhum Participante Selecionado", JOptionPane.ERROR_MESSAGE);
            } else {
                // Se o usuário ordenou a tabela, temos que converter o índice para o do modelo
                int participanteNoModelo = tabela.convertRowIndexToModel(selectedRow);
                int codParticipante = Integer.parseInt(tabela.getModel().getValueAt(participanteNoModelo, 0).toString());
                Participante p = rifaMaster.obterParticipante(codParticipante);
                AdicionarParticipanteGUI np = new AdicionarParticipanteGUI();
                np.setTitle("Editar Participante...");
                np.setNome(p.getNome());
                np.setTickets(p.getTicketsAdquiridos());
                np.show(true);
                // O usuário escolheu gravar as alterações?
                if (np.gravarNovoParticipante()){
                    p.setNome(np.getNome());
                    p.setTicketsAdquiridos(np.getTickets());
                    atualizarPainelDeDetalhesDaRifa();
                }
                
            }
        } else if (src == sortearTBB) {
            // Vamos sortear o participante ganhador da rifa
            // Antes, porém, devemos testar se há participantes...
            if (rifaMaster.getParticipantes().values().size() == 0) {
                JOptionPane.showMessageDialog(this, "Para realizarmos um sorteio, precisamos de pelo menos um participante.", "Nenhum Participante", JOptionPane.ERROR_MESSAGE);
            } else {
                Participante ganhador = rifaMaster.sortearParticipante();
                JOptionPane.showMessageDialog(this, "Eis o nosso grande vencedor:\n" + rifaMaster.resumoDoParticipante(ganhador), "Parabéns!", JOptionPane.INFORMATION_MESSAGE, new ImageIcon("winner.png"));
                atualizarPainelDeDetalhesDaRifa();
            }
        }
        
    }

    
    public Rifa(){
        super("RifaMaster v1.0b");
        setLayout(new BorderLayout(10, 10));
        setSize(540, 300);
        UIManager.put("Label.font", fntComum);
        
        // Para a janela iniciar centralizada na tela
        setLocationRelativeTo(null);
        
        // Evento que irá encerrar o programa ao fechar a janela
        addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent event){
                salvarRifa();
            }
        });
        
        // Vamos criar uma instância da classe RifaMaster.
        // Já temos um arquivo .rifa previamente gravado? Se sim, iremos usá-lo
        File arquivoDaRifa = new File(nomeDoArquivo);
        if (arquivoDaRifa.exists()) {
            rifaMaster = RifaMaster.carregarRifa(nomeDoArquivo);
        } else {
            // Arquivo não existe. Vamos criar um novo objeto
            rifaMaster = new RifaMaster();
        }

        // Vamos configurar os vários elementos que formam a nossa tela
        configurarMenus();
        // Nossa tabela
        tabela = new JTable(new ParticipantesTableModel(rifaMaster));
        // Para permitir que a tabela seja ordenada clicando em uma das suas colunas
        tabela.setAutoCreateRowSorter(true);
        
        // Painel com os detalhes da rifa...
        configurarDetalhesDaRifa();

        // Se um arquivo existente foi previamente carregado, vamos já mostrá-lo na janela
        // Temos que fazer isso somente agora porque ainda era preciso inicializar os
        // elementos da interface
        if (arquivoDaRifa.exists()){
            ((CardLayout)painelPrincipal.getLayout()).last(painelPrincipal);
            setSize(700, 600);
            atualizarPainelDeDetalhesDaRifa();
            rifaValida = true;
        }

        setVisible(true);
    }
    
    public static void main(String... args){
        Rifa rifa = new Rifa();
    }
    
}