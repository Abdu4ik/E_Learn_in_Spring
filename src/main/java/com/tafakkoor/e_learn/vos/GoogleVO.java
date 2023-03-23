package com.tafakkoor.e_learn.vos;

import lombok.Data;

@Data
public class GoogleVO extends AbstractVO {
    private String sub;
    private String email;
    private String given_name;
    private String family_name;
    private String locale;
}
