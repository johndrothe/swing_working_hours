package org.rothe.john.working_hours.ui.toolbar;

import lombok.val;
import org.rothe.john.working_hours.ui.canvas.Canvas;
import org.rothe.john.working_hours.ui.table.MembersTable;
import org.rothe.john.working_hours.ui.toolbar.action.*;

import javax.swing.*;

public class Toolbar extends JToolBar {
    public Toolbar() {
        super();
        setFloatable(false);
    }

    public void displayChanged(final DisplayChangeEvent event) {
        removeAll();
        createButtons(event);
        revalidate();
        repaint();
    }

    private void createButtons(DisplayChangeEvent event) {
        val table = event.table().orElse(null);
        val canvas = event.canvas().orElse(null);

        addStandardActions(table);
        addSeparator();

        if (event.isTableDisplayed()) {
            addTableActions(table);
        }

        if (event.isCanvasDisplayed()) {
            addCanvasActions(canvas);
        }
    }

    private void addCanvasActions(Canvas canvas) {
        add(new ExportImageAction(canvas));
    }

    private void addTableActions(MembersTable table) {
        add(new MemberAddAction(table));
        add(new MemberRemoveAction(table));

        addSeparator();
        add(new MoveUpAction(table));
        add(new MoveDownAction(table));
    }

    private void addStandardActions(MembersTable table) {
        add(new ImportCsvAction(table));
        add(new ExportCsvAction(table));

        addSeparator();
        add(new CopyAction(table));
        add(new PasteAction(table));

        //TODO: undo manager
        addSeparator();
        add(new UndoAction(this));
        add(new RedoAction(this));
    }
}
