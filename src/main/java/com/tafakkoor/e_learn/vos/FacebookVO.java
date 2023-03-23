package com.tafakkoor.e_learn.vos;

import lombok.Data;

@Data
public class FacebookVO extends AbstractVO {
    private String id;
    private String name;
    private String email;
    private String first_name;
    private String last_name;
    private Picture picture_large;
}
