package io.yogurt.cli_mini_game.user;

import io.yogurt.cli_mini_game.common.api.ApiResponse;
import io.yogurt.cli_mini_game.common.user.dto.LoginRequest;
import io.yogurt.cli_mini_game.common.user.dto.StoreUserRequest;
import io.yogurt.cli_mini_game.common.user.dto.UserInfoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("")
    public ApiResponse<UserInfoResponse> store(@RequestBody StoreUserRequest request) {

        UserInfoResponse savedUser = userService.store(request);

        return ApiResponse.ok(savedUser);
    }


    @PostMapping("/login")
    public ApiResponse<UserInfoResponse> login(@RequestBody LoginRequest request) {

        UserInfoResponse savedUser = userService.login(request);

        return ApiResponse.ok(savedUser);
    }
}
