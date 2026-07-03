package com.dengjx.affairs.service;

import com.dengjx.affairs.dto.LoginRequest;
import com.dengjx.affairs.dto.LoginResponse;
import com.dengjx.affairs.dto.ChangePasswordRequest;
import com.dengjx.affairs.dto.CurrentUserProfileResponse;

public interface AuthService {

    LoginResponse login(LoginRequest request);

    void changePassword(Long userId, ChangePasswordRequest request);

    CurrentUserProfileResponse currentProfile(Long userId);
}
