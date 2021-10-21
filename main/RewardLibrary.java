package main;

public class RewardLibrary {

	private String names;
	private int quantity;

	public RewardLibrary(String name, int amount) {

		this.names = name;
		this.quantity = amount;
	}

	public String getName() {
		return names;
	}

	public int getAmount() {
		return quantity;
	}

	public void setAmount(int amount) {
		this.quantity = amount;
	}

}
