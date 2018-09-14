package cc.viridian.servicebatchconverter.persistence;

import cc.viridian.servicebatchconverter.persistence.auto._StatementDetail;

public class StatementDetail extends _StatementDetail {

    private static final long serialVersionUID = 1L;

    // this method allow to access Foreign keys
    public Long getStatementHeaderId() {
        StatementHeader statementHeader = getStatementHeader();
        if (statementHeader != null) {
            return (Long) statementHeader.getObjectId().getIdSnapshot().get(StatementHeader.ID_PK_COLUMN);
        } else {
            return null;
        }
    }
}
