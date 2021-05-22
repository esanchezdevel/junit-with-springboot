package esanchez.devel.app.model.dto;

import java.math.BigDecimal;

public class TransferDTO {

	private Long originAccountId;
	private Long destinyAccountId;
	private BigDecimal amount;
	private Long bankId;

	public Long getOriginAccountId() {
		return originAccountId;
	}

	public void setOriginAccountId(Long originAccountId) {
		this.originAccountId = originAccountId;
	}

	public Long getDestinyAccountId() {
		return destinyAccountId;
	}

	public void setDestinyAccountId(Long destinyAccountId) {
		this.destinyAccountId = destinyAccountId;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public Long getBankId() {
		return bankId;
	}

	public void setBankId(Long bankId) {
		this.bankId = bankId;
	}

	@Override
	public String toString() {
		return "TransferDTO [originAccountId=" + originAccountId + ", destinyAccountId=" + destinyAccountId
				+ ", amount=" + amount + ", bankId=" + bankId + "]";
	}
}
