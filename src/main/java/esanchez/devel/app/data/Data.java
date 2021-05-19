package esanchez.devel.app.data;

import java.math.BigDecimal;

import esanchez.devel.app.model.Account;
import esanchez.devel.app.model.Bank;

public class Data {
	
	public static Account createAccount001() {
		return new Account(1L, "Andres", new BigDecimal("1000"));
	}
	
	public static Account createAccount002() {
		return new Account(2L, "John", new BigDecimal("2000"));
	}
	
	public static Bank createBank() {
		return new Bank(1L, "Financial Bank", 0);
	}
}
