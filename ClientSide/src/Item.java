/*
* EE422C Final Project submission by
* Replace <...> with your actual data.
* Adrian Gallegos
* ag76424
* 17360
* Spring 2022
*/

import org.bson.types.ObjectId;

public class Item {
	private ObjectId id;
	private String name, description, winner, status;
	private double finalPrice;
	
	public Item() {}
	
	public Item(String name, String description, String winner, double finalPrice, String status) {
		this.name = name;
		this.description = description;
		this.winner = winner;
		this.finalPrice = finalPrice;
		this.status = status;
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getWinner() {
		return winner;
	}

	public void setWinner(String winner) {
		this.winner = winner;
	}

	public double getFinalPrice() {
		return finalPrice;
	}

	public void setFinalPrice(double finalPrice) {
		this.finalPrice = finalPrice;
	}
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "Item [name=" + name + ", description=" + description + ", winner=" + winner + ", finalPrice="
				+ finalPrice + ", status=" + status + "]";
	}
}
