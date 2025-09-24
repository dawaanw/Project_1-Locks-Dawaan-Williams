package lock;

public class KeyLock implements Lock {
	private int key;
	private boolean isLocked;
	private boolean isInserted;
	
	public KeyLock(int key) {
		this.key = key; // specific key for the lock
		this.isLocked = true; // locked by default
		this.isInserted = false; // no key inserted initially
	}
	
	public boolean insertKey(int key) {
		if (this.isInserted) {
			return false; // a key is already inserted
		}
		if (key == this.key) {
			this.isInserted = true;
			return true; // the key is inserted
		}
		return false;
	}
	
	public boolean removeKey() {
		if (!this.isInserted) {
			return false; // if there's already no key what are you removing
		}
		this.isInserted = false;
		return true;
	}
	public boolean turn() {
		if (!this.isInserted) {
			return false; // can't turn without a key 
		}
		// if locked, unlock, and vice versa
		this.isLocked = !this.isLocked;
		return true;
	}

	@Override
	public boolean lock() {
		if (this.isInserted && !this.isLocked) { // if the key is inserted and its unlocked
			this.isLocked = true; // locks the lock
			return true;
		}
		return false;
	}

	@Override
	public boolean unlock() {
		if (this.isInserted && this.isLocked) { // if the key is inserted and the lock is locked
			this.isLocked = false; // unlocks the lock
			return true;
		}
		return false;
	}

	@Override
	public boolean isLocked() {
		return this.isLocked;
	}
}
