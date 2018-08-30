package cc.viridian.servicebatchconverter.service;

import cc.viridian.servicebatchconverter.payload.DetailPayload;
import cc.viridian.servicebatchconverter.payload.StatementPayload;
import cc.viridian.servicebatchconverter.repository.StatementDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class StatementDetailService {

    private StatementDetailRepository detailRepository;

    @Autowired
    public StatementDetailService(StatementDetailRepository detailRepository) {
        this.detailRepository = detailRepository;
    }

    public String insertToDatabase(List<StatementPayload> statementPayloadList) {

        statementPayloadList.stream().forEach(statementPayload
                                                  -> statementPayload.getDetails().stream()
                                                                     .forEach(detail
                                                                                  -> this.detailRepository
                                                                         .saveStatementDetail(detail)));
        return "PARSED";
    }

    public int deleteByAccount(final String accountCode) {
        DetailPayload detailPayload = new DetailPayload();
        detailPayload.setAccountCode(accountCode);
        return detailRepository.deleteStatementDetail(detailPayload);
    }
}
