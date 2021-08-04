
public class Block {

	protected int e;

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		
		if (!(obj instanceof Block)) {
			return false;
		}
		
		Block b = (Block) obj;
		
		return Integer.compare(e, b.e) == 0 && Integer.compare(b.e, e) == 0;
	}

	@Override
	public String toString() {
		return "" + e;
	}
	
}
