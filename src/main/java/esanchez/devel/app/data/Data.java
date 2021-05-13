package esanchez.devel.app.data;

import java.math.BigDecimal;

import esanchez.devel.app.model.Account;
import esanchez.devel.app.model.Bank;

public class Data {

	public static final Account ACCOUNT_001 = new Account(1L, "Andres", new BigDecimal("1000"));
	public static final Account ACCOUNT_002 = new Account(2L, "John", new BigDecimal("2000"));
	public static final Bank BANK = new Bank(1L, "Financial Bank", 0);
}
