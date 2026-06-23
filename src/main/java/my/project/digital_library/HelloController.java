package my.project.digital_library;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField; // Importação necessária
import javafx.stage.FileChooser;
import java.io.File;
import java.util.List;

public class HelloController {

    @FXML
    private ListView<String> listaLivros;

    @FXML
    private TextField campoBusca; // Ligação com o fx:id="campoBusca" do FXML

    @FXML
    public void initialize() {
        carregarLivrosDoBanco();
        configurarCliqueNaLista();
        configurarSistemaDeBusca(); // Ativa a busca em tempo real
    }

    private void carregarLivrosDoBanco() {
        List<String> livros = DatabaseManager.listarLivros();
        listaLivros.getItems().clear(); // Limpa antes de adicionar para não duplicar
        listaLivros.getItems().addAll(livros);
    }

    // Configura a busca em tempo real conforme o usuário digita
    private void configurarSistemaDeBusca() {
        campoBusca.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.trim().isEmpty()) {
                // Se a barra de busca estiver vazia, mostra todos os livros
                carregarLivrosDoBanco();
            } else {
                // Busca no banco os livros filtrados pelo termo digitado (newValue)
                List<String> livrosFiltrados = DatabaseManager.buscarLivrosPorNome(newValue.trim());

                // Atualiza visualmente a lista
                listaLivros.getItems().clear();
                listaLivros.getItems().addAll(livrosFiltrados);
            }
        });
    }

    private void configurarCliqueNaLista() {
        listaLivros.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                String livroSelecionado = listaLivros.getSelectionModel().getSelectedItem();
                if (livroSelecionado != null) {
                    abrirLivroNoNavegador(livroSelecionado);
                }
            }
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

            // Após importar, se houver um filtro de busca ativo, limpa para mostrar o novo livro
            campoBusca.clear();
            carregarLivrosDoBanco();
        }
    }
}