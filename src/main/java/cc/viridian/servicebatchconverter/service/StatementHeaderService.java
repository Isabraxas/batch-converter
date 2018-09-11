package cc.viridian.servicebatchconverter.service;

import cc.viridian.servicebatchconverter.payload.DetailPayload;
import cc.viridian.servicebatchconverter.payload.HeaderPayload;
import cc.viridian.servicebatchconverter.payload.StatementPayload;
import cc.viridian.servicebatchconverter.persistence.StatementHeader;
import cc.viridian.servicebatchconverter.repository.StatementHeaderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class StatementHeaderService {

    private StatementHeaderRepository headerRepository;
    private String currentFilePath;
    private Integer count = 0;

    @Autowired
    public StatementHeaderService(StatementHeaderRepository headerRepository) {
        this.headerRepository = headerRepository;
    }

    public String insertToDatabase(final List<StatementPayload> statementPayloadList) {

 /*       statementPayloadList.stream().forEach(statementPayload ->
                                                  this.headerRepository
                                                      .saveStatementHeader(statementPayload.getHeader()));
*/
        return "PARSED";
    }

    public Integer insertOneInToDatabase(final HeaderPayload headerPayload) {
        this.headerRepository.saveStatementHeader(headerPayload);
        count++;
        return count;
    }

    public Integer insertOneInToDatabase(final HeaderPayload headerPayload
        , final List<DetailPayload> detailPayloadList) {
        this.headerRepository.saveStatementHeader(headerPayload, detailPayloadList);
        count++;
        return count;
    }

    public Boolean exist(final HeaderPayload headerPayload) {
        StatementHeader statementHeader = this.headerRepository.getOneStatementHeader(headerPayload);
        return statementHeader != null;
    }

    public Boolean existFileHash(final String hashCode) {
        StatementHeader statementHeader = this.headerRepository.getOneStatementHeaderByFileHash(hashCode);
        return statementHeader != null;
    }

    public String getCurrentFilePath() {
        return currentFilePath;
    }

    public void setCurrentFilePath(final String currentFilePath) {
        this.currentFilePath = currentFilePath;
    }

    public void delete(final HeaderPayload headerPayload) {
        this.headerRepository.deleteStementHeader(headerPayload);
    }

    public HeaderPayload getStatementHeaderPayload(final HeaderPayload headerPayload) {
        return this.headerRepository.getOneStatementHeaderPayload(headerPayload);
    }
}
