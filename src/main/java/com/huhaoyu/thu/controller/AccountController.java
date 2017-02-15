package com.huhaoyu.thu.controller;

import com.huhaoyu.thu.common.Constants.Response;
import com.huhaoyu.thu.common.UnauthorizedException;
import com.huhaoyu.thu.entity.THUAccount;
import com.huhaoyu.thu.service.AccountService;
import com.huhaoyu.thu.service.AuthorizationService;
import com.huhaoyu.thu.widget.VisibleEntityWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by huhaoyu
 * Created On 2017/1/23 下午4:41.
 */

@RestController
@RequestMapping(value = "account")
public class AccountController {

    @Autowired
    private AccountService accountService;
    @Autowired
    private AuthorizationService authService;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity getAccounts(@RequestAttribute(value = AuthorizationService.OPEN_ID) String openId,
                                      @RequestParam(value = "account_id", required = false) Long accountId,
                                      @RequestParam(value = "student_id", required = false) String studentId,
                                      @RequestParam(value = "username", required = false) String username,
                                      @RequestParam(value = "alias", required = false) String alias,
                                      @RequestParam(value = "status", required = false) Integer status,
                                      @RequestParam(value = "user_type", required = false) Integer userType) throws UnauthorizedException {
        List<THUAccount> accounts = accountService.findAccountByProperties(openId, accountId, studentId, username,
                alias, status, userType);
        Object data = VisibleEntityWrapper.createVisibleFieldsMap(accounts);

        return ResponseEntity.ok(Response.Ok.createResponseMap(data));
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity createAccount(@RequestAttribute(value = AuthorizationService.OPEN_ID) String openId,
                                        @RequestParam(value = "student_id") String studentId,
                                        @RequestParam(value = "username") String username,
                                        @RequestParam(value = "password") String password,
                                        @RequestParam(value = "alias", required = false) String alias,
                                        @RequestParam(value = "user_type") Integer userType,
                                        @RequestParam(value = "phone_number") String phoneNumber,
                                        @RequestParam(value = "email") String email,
                                        @RequestParam(value = "description", required = false) String description) {
        THUAccount account = accountService.createAccount(openId, studentId, username, password, alias, userType,
                phoneNumber, email, description);
        Object data = VisibleEntityWrapper.createVisibleFieldsMap(account);
        return ResponseEntity.ok(Response.Ok.createResponseMap(data));
    }

    @RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity updateAccount(@RequestAttribute(value = AuthorizationService.OPEN_ID) String openId,
                                        @RequestParam(value = "account_id") Long accountId,
                                        @RequestParam(value = "password", required = false) String password,
                                        @RequestParam(value = "alias", required = false) String alias,
                                        @RequestParam(value = "user_type", required = false) Integer userType,
                                        @RequestParam(value = "phone_number", required = false) String phoneNumber,
                                        @RequestParam(value = "email", required = false) String email,
                                        @RequestParam(value = "description", required = false) String description) {
        THUAccount account = accountService.updateAccount(openId, accountId, password, alias, userType, phoneNumber,
                email, description);
        Object data = VisibleEntityWrapper.createVisibleFieldsMap(account);
        return ResponseEntity.ok(Response.Ok.createResponseMap(data));
    }

    @RequestMapping(method = RequestMethod.DELETE)
    public ResponseEntity deleteAccount(@RequestAttribute(value = AuthorizationService.OPEN_ID) String openId,
                                        @RequestParam(value = "account_id") Long accountId) {
        accountService.deleteAccount(openId, accountId);
        return ResponseEntity.ok(Response.Ok.createResponseMap());
    }

    @RequestMapping(value = "activate", method = RequestMethod.PUT)
    public ResponseEntity activateAccount(@RequestAttribute(value = AuthorizationService.OPEN_ID) String openId,
                                          @RequestParam(value = "account_id") Long accountId,
                                          @RequestParam(value = "status") Integer status) {
        accountService.activateAccount(openId, accountId, status);
        return ResponseEntity.ok(Response.Ok.createResponseMap());
    }

}
