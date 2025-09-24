package lock;

public class ComboLock implements Lock{
	public final static int COMBO_LENGTH = 3;
	public final static int MAX_TICKS = 39;
	
	private int[] combination;
	private int[] attempt;
	private boolean isLocked;
	private boolean isReset;
	
	public ComboLock() {
		// TODO Auto-generated constructor stub
	}
	
	public boolean turnRight(int ticks) {
		return false;	
	}
	
	public boolean turnLeft(int ticks) {
		return false;
	}
	
	public void reset() {
	}
	
	public boolean isReset() {
		return false;
	}
	

	@Override
	public boolean lock() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean unlock() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isLocked() {
		// TODO Auto-generated method stub
		return false;
	}
	
	public int[] getCombination() {
		return null;
	}

}
