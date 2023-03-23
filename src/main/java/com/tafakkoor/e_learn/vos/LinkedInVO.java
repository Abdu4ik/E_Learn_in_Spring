package com.tafakkoor.e_learn.vos;

import lombok.Data;

@Data
public class LinkedInVO extends AbstractVO {
    private String sub;
    private String given_name;
    private String family_name;
    private String name;
    private String email;
}
