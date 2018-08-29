package cc.viridian.servicebatchconverter.controller;

import cc.viridian.servicebatchconverter.payload.StatementPayload;
import cc.viridian.servicebatchconverter.repository.StatementDetailRepository;
import cc.viridian.servicebatchconverter.repository.StatementHeaderRepository;
import cc.viridian.servicebatchconverter.service.ParseStatement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
public class ParseFileController {

    private ParseStatement parseStatement;
    private StatementHeaderRepository headerRepository;
    private StatementDetailRepository detailRepository;

    @Autowired
    public ParseFileController(ParseStatement parseStatement, StatementHeaderRepository headerRepository, StatementDetailRepository detailRepository) {
        this.parseStatement = parseStatement;
        this.headerRepository = headerRepository;
        this.detailRepository = detailRepository;
    }

    @GetMapping("file")
    public String createDetail(){
        List<StatementPayload> statementPayloadList = new ArrayList<StatementPayload>();
        String filePath = "/home/isvar/Documents/statement/service-batch-converter/src/main/resources/Files/Statement_1998-01-01_2017-12-31.prn";
        try {
            statementPayloadList = parseStatement.parseContent(filePath);

        } catch (IOException e) {
            e.printStackTrace();
            return "NO PARSED";
        }

        statementPayloadList.stream().forEach( statementPayload ->
                                                   this.headerRepository.saveStatementHeader(statementPayload.getHeader()) );
        statementPayloadList.stream().forEach( statementPayload ->
                                                   statementPayload.getDetails().stream().forEach( detail ->
                                                                                                       this.detailRepository.saveStatementDetail(detail) ) );

        //statementPayload.getDetails().stream().forEach(detail -> this.detailRepository.saveStatementDetail(detail));

        return "PARSED";
    }
}
