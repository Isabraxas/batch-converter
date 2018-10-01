package cc.viridian.servicebatchconverter.service;

import cc.viridian.servicebatchconverter.payload.DetailPayload;
import cc.viridian.servicebatchconverter.payload.HeaderPayload;
import cc.viridian.servicebatchconverter.persistence.StatementHeader;
import cc.viridian.servicebatchconverter.repository.StatementHeaderRepository;
import cc.viridian.servicebatchconverter.writer.Userlog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Slf4j
@Service
public class StatementHeaderService {

    private StatementHeaderRepository headerRepository;
    private String currentFilePath;
    private Integer count = 0;

    @Autowired
    public StatementHeaderService(StatementHeaderRepository headerRepository) {
        this.headerRepository = headerRepository;
    }


    public Integer insertOneInToDatabase(final HeaderPayload headerPayload) {
        this.headerRepository.saveStatementHeader(headerPayload);
        count++;
        return count;
    }

    public Integer insertOneInToDatabase(final HeaderPayload headerPayload,
        final List<DetailPayload> detailPayloadList) {
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
        return (statementHeader != null) ? true : false;
    }

    public void delete(final HeaderPayload headerPayload) {
        this.headerRepository.deleteStementHeader(headerPayload);
    }

    public HeaderPayload getStatementHeaderPayload(final HeaderPayload headerPayload) {
        return this.headerRepository.getOneStatementHeaderPayload(headerPayload);
    }

    public void deleteAll(final Userlog userlog) {
        this.headerRepository.deleteAllStatements();
        userlog.info("Database is it clean, without headers and details");
        userlog.closeLog();
    }


}
