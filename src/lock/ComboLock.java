package lock;

public class ComboLock implements Lock{
	public final static int COMBO_LENGTH = 3;
	public final static int MAX_TICKS = 39;
	
	private int[] combination;
	private int[] attempt;
	private boolean isLocked;
	private boolean isReset;
	private int step;
	
	public ComboLock(int[] combination) {
		if (combination.length != COMBO_LENGTH) {
			throw new IllegalArgumentException("Combination must have 3 numbers");
		}
		for (int num : combination) {
			if (num < 0 || num > MAX_TICKS) {
				throw new IllegalArgumentException("Combination numbers must be between 0 and " + MAX_TICKS);
			}
		}
		int x = combination[0] % 4;
		
		if (combination[2] % 4 != x) {
			throw new IllegalArgumentException("Last number must have remainder " + x + " mod 4");
		}
		if (combination[1] % 4 != (x+2) % 4) {
			throw new IllegalArgumentException("Middle number must have remainder " + ((x + 2) % 4) + " mod 4");
		}
		this.combination = combination.clone();
		this.attempt = new int[COMBO_LENGTH];
		this.isLocked = true;
		this.isReset = true;
		this.step = 0;
	}
	
	public boolean turnRight(int ticks) {
		if (step == 0 || step == 2) {
			attempt[step] = ticks;
			step++;
			isReset = false;
			return true;
		}
		return false;
	}
	
	public boolean turnLeft(int ticks) {
		if (step == 1) {
			attempt[step] = ticks;
			step++;
			isReset = false;
			return true;
		}
		return false;
	}
	
	public void reset() {
		this.attempt = new int[COMBO_LENGTH];
		this.isReset = true;
		this.step = 0;
	}
	
	public boolean isReset() {
		return this.isReset;
	}
	

	@Override
	public boolean lock() {
		this.isLocked = true;
		reset();
		return false;
	}

	@Override
	public boolean unlock() {
		if (step == COMBO_LENGTH) {
			for (int i = 0; i < COMBO_LENGTH; i++) {
				if (attempt[i] != combination[i]) {
					this.isLocked = true;
					reset();
					return false;
				}
			}
			this.isLocked = false;
			reset();
			return true;
		}
		return false;
	}

	@Override
	public boolean isLocked() {
		return this.isLocked;
	}
	
	public int[] getCombination() {
		return this.combination.clone();
	}

}
