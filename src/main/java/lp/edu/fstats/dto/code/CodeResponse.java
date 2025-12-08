package lp.edu.fstats.dto.code;

import lp.edu.fstats.model.code.Code;

public record CodeResponse(
        Integer id,
        String code
) {

    public CodeResponse(Code source){
        this(
                source.getId(),
                source.getCode()
        );
    }

}
