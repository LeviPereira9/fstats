package lp.edu.fstats.dto.code;

import lp.edu.fstats.model.code.Code;

public record CodeRequest(
        String code,
        String name
){

    public Code toModel(){
        Code code = new Code();
        code.setCode(this.code);
        code.setName(this.name);
        return code;
    }

}
