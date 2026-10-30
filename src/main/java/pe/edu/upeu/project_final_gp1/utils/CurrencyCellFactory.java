package pe.edu.upeu.project_final_gp1.utils;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;
import java.util.Locale;

public final class CurrencyCellFactory {
    private CurrencyCellFactory() {}
    public static <S> Callback<TableColumn<S, Number>, TableCell<S, Number>> create() {
        return column -> new TableCell<>() {
            @Override protected void updateItem(Number value, boolean empty) {
                super.updateItem(value, empty);
                setText(empty || value == null ? null : String.format(Locale.US, "S/ %.2f", value.doubleValue()));
            }
        };
    }
}
