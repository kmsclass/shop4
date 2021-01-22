package logic;

import java.util.ArrayList;
import java.util.List;

public class Cart {
	private List<ItemSet> itemSetList = new ArrayList<>();
	
	public List<ItemSet> getItemSetList() {
		return itemSetList;
	}
	public void push(ItemSet itemSet) {
		int count = itemSet.getQuantity();
		for(ItemSet old : itemSetList) {
			if(itemSet.getItem().getId().equals(old.getItem().getId())) {
				count = old.getQuantity() + itemSet.getQuantity();
				old.setQuantity(count);
				return;
			}
		}
	   itemSetList.add(itemSet);
	}
	public long getTotal() {
		long sum = 0;
		for(ItemSet is : itemSetList) {
			sum += is.getItem().getPrice() * is.getQuantity();
		}
		return sum;
	}

}
