package work.rothe.tav.ui.table;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import work.rothe.tav.event.Documents;
import work.rothe.tav.model.Document;
import work.rothe.tav.model.Member;
import work.rothe.tav.model.Time;
import work.rothe.tav.model.Zone;
import work.rothe.tav.ui.table.editors.DecoratedZone;

import javax.swing.table.AbstractTableModel;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.nonNull;

@Slf4j
public class MembersTableModel extends AbstractTableModel {
    private final List<Member> members = new ArrayList<>();
    private Document document = null;

    public void setDocument(Document document) {
        this.document = document;
        this.members.clear();

        if (nonNull(document)) {
            this.members.addAll(document.members());
        }
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return members.size();
    }

    @Override
    public int getColumnCount() {
        return Columns.getCount();
    }

    @Override
    public String getColumnName(int columnIndex) {
        if (columnIndex < 0 || columnIndex >= getColumnCount()) {
            return null;
        }
        return Columns.getColumn(columnIndex).getDescription();
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if (columnIndex < 0 || columnIndex >= getColumnCount()) {
            return Object.class;
        }
        switch (Columns.getColumn(columnIndex)) {
            case NAME, ROLE, LOCATION -> {
                return String.class;
            }
            case START_TIME, END_TIME, LUNCH_START, LUNCH_END -> {
                return LocalTime.class;
            }
            case ZONE -> {
                return Zone.class;
            }
            default -> {
                return Object.class;
            }
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (columnIndex < 0 || columnIndex >= getColumnCount()) {
            return Object.class;
        }
        if (rowIndex < 0 || rowIndex >= members.size()) {
            return null;
        }
        val member = members.get(rowIndex);

        switch (Columns.getColumn(columnIndex)) {
            case NAME -> {
                return member.name();
            }
            case ROLE -> {
                return member.role();
            }
            case LOCATION -> {
                return member.location();
            }
            case START_TIME -> {
                return member.normal().left();
            }
            case END_TIME -> {
                return member.normal().right();
            }
            case LUNCH_START -> {
                return member.lunch().left();
            }
            case LUNCH_END -> {
                return member.lunch().right();
            }
            case ZONE -> {
                return member.zone();
            }
            default -> throw new IllegalArgumentException("Invalid column: " + columnIndex);
        }
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if (columnIndex < 0 || columnIndex >= getColumnCount()) {
            return;
        }
        if (rowIndex < 0 || rowIndex >= members.size()) {
            return;
        }

        try {
            fireDocumentChanged(columnIndex, setValue(aValue, rowIndex, columnIndex));
        } catch (DateTimeParseException e) {
            log.info("Ignoring invalid value '{}' entered as the '{}' for team member '{}'.%n",
                    aValue,
                    Columns.getColumn(columnIndex).getDescription(),
                    members.get(rowIndex).name());
        }
    }

    protected Document setValue(Object aValue, int rowIndex, int columnIndex) {
        return document.withMembers(updatedMembers(aValue, rowIndex, columnIndex));
    }

    private List<Member> updatedMembers(Object aValue, int rowIndex, int columnIndex) {
        val newMembers = new ArrayList<>(members);
        newMembers.set(rowIndex, updatedMember(aValue, members.get(rowIndex), columnIndex));
        return newMembers;
    }

    private Member updatedMember(Object aValue, Member member, int columnIndex) {
        val column = Columns.getColumn(columnIndex);
        switch (column) {
            case NAME -> {
                return member.withName(aValue.toString().trim());
            }
            case ROLE -> {
                return member.withRole(aValue.toString().trim());
            }
            case LOCATION -> {
                return member.withLocation(aValue.toString().trim());
            }
            case START_TIME -> {
                return member.withNormal(member.normal().withLeft(toTime(member.zone(), aValue)));
            }
            case END_TIME -> {
                return member.withNormal(member.normal().withRight(toTime(member.zone(), aValue)));
            }
            case LUNCH_START -> {
                return member.withLunch(member.lunch().withLeft(toTime(member.zone(), aValue)));
            }
            case LUNCH_END -> {
                return member.withLunch(member.lunch().withRight(toTime(member.zone(), aValue)));
            }
            case ZONE -> {
                return member.withZone(toZone(aValue));
            }
        }
        return member;
    }

    private static Zone toZone(Object aValue) {
        if(aValue instanceof DecoratedZone zone) {
            return zone.toZone();
        }
        if(aValue instanceof Zone zone) {
            return zone;
        }
        return Zone.fromCsv(aValue.toString().trim());
    }

    private static Time toTime(Zone zone, Object aValue) {
        return Time.parse(zone, adjustTime(aValue.toString().trim())).roundToQuarterHour();
    }

    private static String adjustTime(String text) {
        if (text.matches("[0-9]{1,4}")) {
            switch (text.length()) {
                case 1:
                    return text.replaceFirst("([0-9])", "0$1:00");
                case 2:
                    return text.replaceFirst("([0-9]{2})", "$1:00");
                case 3:
                    return text.replaceFirst("([0-9])([0-9]{2})", "0$1:$2");
                case 4:
                    return text.replaceFirst("([0-9]{2})([0-9]{2})", "$1:$2");
            }
        }
        return text;
    }

    private void fireDocumentChanged(int columnIndex, Document newDocument) {
        Documents.fireDocumentChanged(this,
                "Edit " + Columns.getColumn(columnIndex).getDescription(),
                newDocument);
    }
}
