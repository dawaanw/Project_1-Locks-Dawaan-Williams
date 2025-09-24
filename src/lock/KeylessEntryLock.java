package lock;

public class KeylessEntryLock extends KeyLock {

	public static final int MAX_NUM_USER_CODES = 10;
	public static final int USER_CODE_LENGTH = 4;
	public static final int MASTER_CODE_LENGTH = 6;

	private boolean isReset;
	private boolean isNewUserCode;
	private boolean isDeletedUserCode;
	private boolean isChangedMasterCode;
	private boolean areAllUserCodesDeleted;
	private int[] masterCode;
	private int[][] userCodes;
	private int[] attempt;

	public KeylessEntryLock(int keyValue) {
		super(keyValue);
	}

	public boolean pushButton(char button) {
		return false;
	}
	
	public boolean addedUserCode() {
		return false;
	}

	public boolean deletedUserCode() {
		return false;
	}

	public boolean deletedAllUserCodes() {
		return false;
	}

	public boolean changedMasterCode() {
		return false;
	}

	public int[] getMasterCode() {
		return null;
	}

}
