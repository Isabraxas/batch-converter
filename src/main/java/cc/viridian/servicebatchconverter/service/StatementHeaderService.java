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

    public String insertToDatabase(List<StatementPayload> statementPayloadList) {

 /*       statementPayloadList.stream().forEach(statementPayload ->
                                                  this.headerRepository
                                                      .saveStatementHeader(statementPayload.getHeader()));
*/
        return "PARSED";
    }

    public int deleteByCustomer(final String customerCode) {
        HeaderPayload headerPayload = new HeaderPayload();
        headerPayload.setCustomerCode(customerCode);
        return headerRepository.deleteByCustomer(headerPayload);
    }

    public Integer insertOneInToDatabase(HeaderPayload headerPayload) {
        this.headerRepository.saveStatementHeader(headerPayload);
        count++;
        return count;
    }

    public Integer insertOneInToDatabase(HeaderPayload headerPayload, List<DetailPayload> detailPayloadList) {
        this.headerRepository.saveStatementHeader(headerPayload, detailPayloadList);
        count++;
        return count;
    }

    public Boolean exist(HeaderPayload headerPayload){
        StatementHeader statementHeader = this.headerRepository.getOneStatementHeader(headerPayload);
        if (statementHeader != null) {
            return true;
        }else {
            return false;
        }
    }

    public String getCurrentFilePath() {
        return currentFilePath;
    }

    public void setCurrentFilePath(String currentFilePath) {
        this.currentFilePath = currentFilePath;
    }
}
