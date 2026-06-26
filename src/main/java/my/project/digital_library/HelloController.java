package my.project.digital_library;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import java.io.File;
import java.util.List;
import javafx.animation.PauseTransition;
import javafx.util.Duration;

public class HelloController {

    @FXML
    private TilePane gridLivros; // Alterado de ListView para TilePane

    @FXML
    private TextField campoBusca;

    @FXML
    private Label labelTotalLivros;

    @FXML
    public void initialize() {
        carregarLivrosDoBanco();
        configurarSistemaDeBusca();
    }

    private void carregarLivrosDoBanco() {
        // Agora buscamos a lista completa de objetos (com id e nome) do banco
        List<Livro> livros = DatabaseManager.listarLivrosCompletos();
        renderizarGradeDeLivros(livros);
    }

    private void renderizarGradeDeLivros(List<Livro> livros) {
        gridLivros.getChildren().clear(); // Limpa a grade atual

        if (labelTotalLivros != null) {
            labelTotalLivros.setText(String.valueOf(livros.size()));
        }

        for (Livro livro : livros) {
            // Criando o Quadrado (VBox)
            VBox card = new VBox();
            card.getStyleClass().add("book-card");
            card.setPrefSize(140, 180); // Tamanho fixo do quadrado do livro
            card.setSpacing(15);

            // Se não há imagem, exibe o ID estilizado (ex: "ID: 1" ou "Livro 1")
            Label placeholder = new Label("Livro " + livro.getId());
            placeholder.getStyleClass().add("book-thumb-placeholder");

            // Texto com o nome real do arquivo PDF
            Label titulo = new Label(livro.getNome());
            titulo.getStyleClass().add("book-title-label");
            titulo.setWrapText(true); // Quebra a linha se o nome for grande
            titulo.setMaxWidth(120);

            // Adiciona os componentes dentro do quadrado
            card.getChildren().addAll(placeholder, titulo);

            // Configura o duplo clique para abrir o PDF
            card.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2) {
                    abrirLivroNoNavegador(livro.getNome());
                }
            });

            // Adiciona o quadrado finalizado na nossa grade principal
            gridLivros.getChildren().add(card);
        }
    }

    // Criamos o temporizador com um atraso de 300 milissegundos
    private final PauseTransition debounce = new PauseTransition(Duration.millis(300));

    private void configurarSistemaDeBusca() {
        campoBusca.textProperty().addListener((observable, oldValue, newValue) -> {
            // Toda vez que o usuário digita, o temporizador reinicia
            debounce.setOnFinished(event -> {
                // Esse bloco só roda quando o usuário parar de digitar por 300ms
                if (newValue == null || newValue.trim().isEmpty()) {
                    carregarLivrosDoBanco();
                } else {
                    List<Livro> filtrados = DatabaseManager.buscarLivrosObjetosPorNome(newValue.trim());
                    renderizarGradeDeLivros(filtrados);
                }
            });

            // Dispara ou reinicia a contagem do zero
            debounce.playFromStart();
        });
    }

    private void abrirLivroNoNavegador(String nomeLivro) {
        String caminho = DatabaseManager.buscarCaminhoLivro(nomeLivro);
        if (caminho != null) {
            File arquivo = new File(caminho);
            if (arquivo.exists()) {
                String uri = arquivo.toURI().toString();
                HelloApplication.getInstance().getHostServices().showDocument(uri);
            } else {
                System.out.println("Erro: Arquivo não encontrado.");
            }
        }
    }

    @FXML
    protected void handleImportarPdf() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Selecionar Livro em PDF");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Arquivos PDF", "*.pdf"));

        File arquivoSelecionado = fileChooser.showOpenDialog(null);

        if (arquivoSelecionado != null) {
            String nomeLivro = arquivoSelecionado.getName();
            String caminhoAbsoluto = arquivoSelecionado.getAbsolutePath();

            DatabaseManager.salvarLivro(nomeLivro, caminhoAbsoluto);

            campoBusca.clear();
            carregarLivrosDoBanco();
        }
    }
}