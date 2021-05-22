package esanchez.devel.app.controller;


import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import esanchez.devel.app.model.Account;
import esanchez.devel.app.model.dto.TransferDTO;
import esanchez.devel.app.service.AccountService;

@RestController
@RequestMapping("/v1/account")
public class AccountController {

	@Autowired
	private AccountService accountService;
	
	@GetMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	public Account getAccount(@PathVariable Long id) {
		
		return accountService.findById(id);
	}
	
	@PostMapping("/transfer")
	public ResponseEntity<?> transfer(@RequestBody TransferDTO request) {
		
		accountService.transfer(request.getOriginAccountId(), request.getDestinyAccountId(), request.getAmount(), request.getBankId());
		
		Map<String, Object> response = new HashMap<>();
		response.put("date", LocalDate.now().toString());
		response.put("status", "success");
		response.put("message", "transfer done successfully");
		response.put("transaction", request);
		
		return ResponseEntity.ok(response);
	}
}
