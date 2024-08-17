package org.rothe.john.swc.ui.table.paste.operations;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.rothe.john.swc.event.Documents;
import org.rothe.john.swc.model.Document;
import org.rothe.john.swc.model.Member;
import org.rothe.john.swc.model.Zone;
import org.rothe.john.swc.ui.table.MembersTable;
import org.rothe.john.swc.ui.table.paste.CopiedContent;

import static org.rothe.john.swc.model.TimePair.businessLunch;
import static org.rothe.john.swc.model.TimePair.businessNormal;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
class AbstractPasteOperation {
    protected final CopiedContent content;
    protected final MembersTable table;

    protected void fireDocumentChanged(Document newDocument) {
        Documents.fireDocumentChanged(this, "Paste", newDocument);
    }

    protected Document applyValues(Document document,
                                   CopiedContent content,
                                   int startRow,
                                   int startColumn) {
        TableValueWriter cursor = new TableValueWriter(document, startRow, startColumn);

        for (int row = 0; row < content.getRowCount(); ++row) {
            for (int column = 0; column < content.getColumnCount(); ++column) {
                cursor.setNext(content.getValueAt(row, column));
            }
            cursor.nextRow();
        }
        return cursor.document();
    }

    protected static Member newMember() {
        return new Member("", "", "",
                Zone.here(),
                businessNormal(Zone.here()),
                businessLunch(Zone.here()));
    }
}
