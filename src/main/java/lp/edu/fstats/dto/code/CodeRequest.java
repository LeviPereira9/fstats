package lp.edu.fstats.dto.code;

import lp.edu.fstats.model.code.Code;

public record CodeRequest(
        String code
){

    public Code toModel(){
        Code code = new Code();
        code.setCode(this.code);
        return code;
    }

}
