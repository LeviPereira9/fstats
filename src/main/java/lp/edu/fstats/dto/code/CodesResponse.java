package lp.edu.fstats.dto.code;

import lp.edu.fstats.model.code.Code;

import java.util.List;

public record CodesResponse(
        List<CodeResponse> codes
) {

    public static CodesResponse toResponse(List<Code> source){
        List<CodeResponse> codes =  source.stream().map(CodeResponse::new).toList();

        return new CodesResponse(codes);
    }

}
