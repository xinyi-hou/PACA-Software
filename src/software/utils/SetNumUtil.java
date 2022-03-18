package software.utils;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

/**
 * 自动生成表格的序号列类
 *
 * @author 侯心怡
 * @class 软信1902
 * @StudentID 20195782
 * @date 2022-3-9
 */

public class SetNumUtil<S,T> implements Callback<TableColumn<S,T>, TableCell<S,T>> {

    @Override
    public TableCell<S, T> call(TableColumn<S, T> param) {

        TableCell cell=new TableCell() {
            @Override
            protected void updateItem(Object item, boolean empty) {
                super.updateItem(item, empty);
                this.setText(null);
                this.setGraphic(null);
                if (!empty) {
                    int rowIndex = this.getIndex() + 1;
                    this.setText(String.valueOf(rowIndex));
                }
            }

        };
        return cell;
    }

}

