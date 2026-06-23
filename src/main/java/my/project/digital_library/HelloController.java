package my.project.digital_library;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.stage.FileChooser;
import java.io.File;
import java.util.List;

public class HelloController {

    @FXML
    private ListView<String> listaLivros;

    @FXML
    public void initialize() {
        carregarLivrosDoBanco();
        configurarCliqueNaLista();
    }

    private void carregarLivrosDoBanco() {
        List<String> livros = DatabaseManager.listarLivros();
        listaLivros.getItems().addAll(livros);
    }

    // Configura o evento de duplo clique na lista de livros
    private void configurarCliqueNaLista() {
        listaLivros.setOnMouseClicked(event -> {
            // Verifica se foi um clique duplo (2 cliques)
            if (event.getClickCount() == 2) {
                String livroSelecionado = listaLivros.getSelectionModel().getSelectedItem();

                if (livroSelecionado != null) {
                    abrirLivroNoNavegador(livroSelecionado);
                }
            }
        });
    }

    private void abrirLivroNoNavegador(String nomeLivro) {
        // 1. Busca o caminho do ficheiro guardado no SQLite
        String caminho = DatabaseManager.buscarCaminhoLivro(nomeLivro);

        if (caminho != null) {
            File arquivo = new File(caminho);

            // Verifica se o arquivo ainda existe no computador do usuário
            if (arquivo.exists()) {
                // Converte o caminho do arquivo para o formato de URI (ex: file:///C:/...)
                String uri = arquivo.toURI().toString();

                // Usa o HostServices da aplicação para abrir no navegador padrão do sistema
                HelloApplication.getInstance().getHostServices().showDocument(uri);
                System.out.println("A abrir: " + uri);
            } else {
                System.out.println("Erro: O arquivo PDF não foi encontrado no caminho " + caminho);
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
            listaLivros.getItems().add(nomeLivro);
        }
    }
}