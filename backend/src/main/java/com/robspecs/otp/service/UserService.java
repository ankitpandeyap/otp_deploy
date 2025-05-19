package com.robspecs.otp.service;

import com.robspecs.otp.dto.RegistrationDTO;
import com.robspecs.otp.entity.User;
import com.robspecs.otp.enums.Role;

public interface UserService {


	User registerNewUser(RegistrationDTO regDto);



}
