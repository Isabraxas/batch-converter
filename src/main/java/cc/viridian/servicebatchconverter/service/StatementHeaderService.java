package cc.viridian.servicebatchconverter.service;

import cc.viridian.servicebatchconverter.payload.HeaderPayload;
import cc.viridian.servicebatchconverter.payload.StatementPayload;
import cc.viridian.servicebatchconverter.repository.StatementHeaderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class StatementHeaderService {

    private StatementHeaderRepository headerRepository;

    @Autowired
    public StatementHeaderService(StatementHeaderRepository headerRepository) {
        this.headerRepository = headerRepository;
    }

    public String insertToDatabase(List<StatementPayload> statementPayloadList){

          statementPayloadList.stream().forEach( statementPayload ->
                                                   this.headerRepository
                                                       .saveStatementHeader(statementPayload.getHeader()) );
          return "PARSED";
    }

    public int deleteByCustomer(final String customerCode){
        HeaderPayload headerPayload = new HeaderPayload();
        headerPayload.setCustomerCode(customerCode);
        return headerRepository.deleteByCustomer(headerPayload);
    }
}
