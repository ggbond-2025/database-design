package com.dengjx.affairs.controller;

import com.dengjx.affairs.service.UserAccountService;
import com.dengjx.affairs.entity.UserAccount;
import com.dengjx.affairs.dto.UserAccountRequest;
import com.dengjx.affairs.common.ApiResponse;
import com.dengjx.affairs.common.PageResult;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/users")
public class UserAccountController {

    private final UserAccountService userAccountService;

    public UserAccountController(UserAccountService userAccountService) {
        this.userAccountService = userAccountService;
    }

    @GetMapping
    public ApiResponse<PageResult<UserAccount>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size) {
        return ApiResponse.ok(userAccountService.list(keyword, page, size));
    }

    @GetMapping("/{id}")
    public ApiResponse<UserAccount> detail(@PathVariable Long id) {
        return ApiResponse.ok(userAccountService.getById(id));
    }

    @PostMapping
    public ApiResponse<UserAccount> create(@Valid @RequestBody UserAccountRequest request) {
        return ApiResponse.ok(userAccountService.create(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<UserAccount> update(@PathVariable Long id, @Valid @RequestBody UserAccountRequest request) {
        return ApiResponse.ok(userAccountService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        userAccountService.delete(id);
        return ApiResponse.ok(null);
    }
}
