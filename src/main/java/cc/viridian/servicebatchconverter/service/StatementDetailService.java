package cc.viridian.servicebatchconverter.service;

import cc.viridian.servicebatchconverter.payload.DetailPayload;
import cc.viridian.servicebatchconverter.persistence.StatementDetail;
import cc.viridian.servicebatchconverter.repository.StatementDetailRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class StatementDetailService {

    private StatementDetailRepository detailRepository;

    @Autowired
    public StatementDetailService(StatementDetailRepository detailRepository) {
        this.detailRepository = detailRepository;
    }


    public Boolean exist(final DetailPayload detailPayload) {
        StatementDetail statementDetail = this.detailRepository.getOneStatementDetail(detailPayload);
        return statementDetail != null;
    }
}
