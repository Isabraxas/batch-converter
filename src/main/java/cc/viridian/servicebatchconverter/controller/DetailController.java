package cc.viridian.servicebatchconverter.controller;

import cc.viridian.servicebatchconverter.payload.DetailPayload;
import cc.viridian.servicebatchconverter.repository.StatementDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DetailController {
    @Autowired
    private StatementDetailRepository statementDetailRepository;

    @PostMapping("detail")
    public String createDetail(@RequestBody DetailPayload body){

        statementDetailRepository.saveStatementDetail(body);
        return "SAVED";
    }
}
