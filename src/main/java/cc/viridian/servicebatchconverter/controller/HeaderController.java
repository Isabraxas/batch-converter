package cc.viridian.servicebatchconverter.controller;

import cc.viridian.servicebatchconverter.payload.HeaderPayload;
import cc.viridian.servicebatchconverter.repository.StatementHeaderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HeaderController {
    @Autowired
    private StatementHeaderRepository statementHeaderRepository;

    @PostMapping("/header")
    public String createHeader(@RequestBody HeaderPayload body){

        statementHeaderRepository.registerStatementHeader(body);
        return "SAVED";
    }

    @PostMapping("/header2")
    public String createHeader2(@RequestBody HeaderPayload body){

        statementHeaderRepository.saveStatementHeader(body);
        return "SAVED";
    }
}
