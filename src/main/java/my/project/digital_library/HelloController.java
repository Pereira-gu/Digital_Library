package my.project.digital_library;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.stage.FileChooser;
import java.io.File;

public class HelloController {

    @FXML
    private ListView<String> listaLivros; // Ligação com o fx:id="listaLivros"

    @FXML
    protected void handleImportarPdf() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Selecionar Livro em PDF");

        // Filtra para mostrar apenas arquivos .pdf
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Arquivos PDF", "*.pdf")
        );

        // Abre a janela de seleção
        File arquivoSelecionado = fileChooser.showOpenDialog(null);

        if (arquivoSelecionado != null) {
            // Adiciona o nome do ficheiro à lista visual
            listaLivros.getItems().add(arquivoSelecionado.getName());
            System.out.println("PDF Carregado: " + arquivoSelecionado.getAbsolutePath());
        }
    }
}