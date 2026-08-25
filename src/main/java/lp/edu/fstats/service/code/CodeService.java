package lp.edu.fstats.service.code;

import lp.edu.fstats.dto.code.CodeRequest;
import lp.edu.fstats.dto.code.CodeResponse;
import lp.edu.fstats.dto.code.CodesResponse;

public interface CodeService {
    CodesResponse getAllCodes();

    CodeResponse createCode(CodeRequest request);

    void deleteCode(Integer codeId);

    CodeResponse reactiveCode(Integer codeId);
}
