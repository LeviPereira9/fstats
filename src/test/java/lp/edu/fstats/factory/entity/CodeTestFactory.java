package lp.edu.fstats.factory.entity;

import lp.edu.fstats.model.code.Code;

public class CodeTestFactory {

    public static Code buildCode(Integer id, String code, String name){
        Code c = new Code();
        c.setId(id);
        c.setCode(code);
        c.setName(name);

        return c;
    }
}
