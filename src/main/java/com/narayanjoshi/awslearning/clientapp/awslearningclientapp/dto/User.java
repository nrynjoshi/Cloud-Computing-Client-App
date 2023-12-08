package com.narayanjoshi.awslearning.clientapp.awslearningclientapp.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class User {

    private String name;

    private String addressLine1;

    private String city;

    private String postCode;

    private String phoneNumber;

    private String dateOfBirth;

    private String email;

    private String password;

}
