package com.dengjx.affairs.service;

import com.dengjx.affairs.common.PageResult;
import com.dengjx.affairs.dto.UserAccountRequest;
import com.dengjx.affairs.entity.UserAccount;

public interface UserAccountService {

    PageResult<UserAccount> list(String keyword, long page, long size);

    UserAccount getById(Long id);

    UserAccount create(UserAccountRequest request);

    UserAccount update(Long id, UserAccountRequest request);

    void delete(Long id);
}
