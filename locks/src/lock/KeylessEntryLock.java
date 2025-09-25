package lock;

import java.util.Arrays;

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
	
	private enum State {
	    IDLE, ENTER_MASTER, SELECT_OP, ENTER_NEW_USER, CONFIRM_NEW_USER,
	    ENTER_USER_TO_DELETE, CONFIRM_DELETE_USER, ENTER_NEW_MASTER, CONFIRM_NEW_MASTER
	}

	private State currentState = State.IDLE;
	private int[] buffer; // store entered digits
	private int bufferIndex = 0;
	private int[] tempUserCode = new int[USER_CODE_LENGTH];
	private int tempIndex = 0;
	private int selectedOperation = 0;

	public KeylessEntryLock(int keyValue, int[] initialMasterCode) {
		super(keyValue);
		 if (initialMasterCode.length != MASTER_CODE_LENGTH) {
		        throw new IllegalArgumentException("Master code must be 6 digits");
		    }
		    this.masterCode = initialMasterCode.clone();
		    this.userCodes = new int[MAX_NUM_USER_CODES][USER_CODE_LENGTH];
		    this.attempt = new int[MASTER_CODE_LENGTH]; 
		    this.isReset = false;
		    this.isNewUserCode = false;
		    this.isDeletedUserCode = false;
		    this.isChangedMasterCode = false;
		    this.areAllUserCodesDeleted = false;
	}

	   private boolean checkMasterCode(int[] input) {
	        return Arrays.equals(input, masterCode);
	    }

	    private boolean checkUserCode(int[] input) {
	        for (int i = 0; i < MAX_NUM_USER_CODES; i++) {
	            if (Arrays.equals(userCodes[i], input)) {
	                return true;
	            }
	        }
	        return false;
	    }

	    private void resetBuffer() {
	        Arrays.fill(buffer, 0);
	        bufferIndex = 0;
	        tempIndex = 0;
	    }

	    private boolean addUserCode(int[] code) {
	        for (int i = 0; i < MAX_NUM_USER_CODES; i++) {
	            if (userCodes[i][0] == 0) { // empty slot
	                userCodes[i] = code.clone();
	                isNewUserCode = true;
	                return true;
	            }
	        }
	        return false; // no space
	    }

	    private boolean deleteUserCode(int[] code) {
	        for (int i = 0; i < MAX_NUM_USER_CODES; i++) {
	            if (Arrays.equals(userCodes[i], code)) {
	                userCodes[i] = new int[USER_CODE_LENGTH]; // clear slot
	                isDeletedUserCode = true;
	                return true;
	            }
	        }
	        return false;
	    }

	    private void deleteAllUserCodes() {
	        for (int i = 0; i < MAX_NUM_USER_CODES; i++) {
	            userCodes[i] = new int[USER_CODE_LENGTH];
	        }
	        areAllUserCodesDeleted = true;
	        resetBuffer();
	        currentState = State.IDLE;
	    }

	    public boolean pushButton(char button) {
	        if (!Character.isDigit(button) && button != '*') return false;

	        if (Character.isDigit(button)) {
	            int digit = button - '0';
	            if (currentState == State.IDLE || currentState == State.ENTER_MASTER) {
	                if (bufferIndex < MASTER_CODE_LENGTH) {
	                    buffer[bufferIndex++] = digit;
	                    currentState = State.ENTER_MASTER;
	                    return true;
	                }
	            } else if (currentState == State.ENTER_NEW_USER || currentState == State.CONFIRM_NEW_USER
	                    || currentState == State.ENTER_USER_TO_DELETE || currentState == State.CONFIRM_DELETE_USER
	                    || currentState == State.ENTER_NEW_MASTER || currentState == State.CONFIRM_NEW_MASTER) {
	                if (tempIndex < USER_CODE_LENGTH) {
	                    tempUserCode[tempIndex++] = digit;
	                    return true;
	                } else if (tempIndex < MASTER_CODE_LENGTH) { // for new master
	                    tempUserCode[tempIndex++] = digit;
	                    return true;
	                }
	            }
	            return false;
	        }

	        // '*' triggers next stage
	        if (button == '*') {
	            switch (currentState) {
	                case ENTER_MASTER:
	                    if (bufferIndex == MASTER_CODE_LENGTH && checkMasterCode(buffer)) {
	                        bufferIndex = 0;
	                        currentState = State.SELECT_OP;
	                        return true;
	                    }
	                    resetBuffer();
	                    return false;
	                case SELECT_OP:
	                    selectedOperation = buffer[0];
	                    bufferIndex = 0;
	                    resetBuffer();
	                    switch (selectedOperation) {
	                        case 1: currentState = State.ENTER_NEW_USER; break;
	                        case 2: currentState = State.ENTER_USER_TO_DELETE; break;
	                        case 3: currentState = State.ENTER_NEW_MASTER; break;
	                        case 6: deleteAllUserCodes(); break;
	                        default: currentState = State.IDLE; return false;
	                    }
	                    return true;
	                case ENTER_NEW_USER:
	                    if (tempIndex == USER_CODE_LENGTH) {
	                        tempIndex = 0;
	                        currentState = State.CONFIRM_NEW_USER;
	                        return true;
	                    }
	                    return false;
	                case CONFIRM_NEW_USER:
	                    if (tempIndex == USER_CODE_LENGTH) {
	                        if (addUserCode(tempUserCode)) {
	                            resetBuffer();
	                            currentState = State.IDLE;
	                            return true;
	                        }
	                        resetBuffer();
	                        currentState = State.IDLE;
	                        return false;
	                    }
	                    return false;
	                case ENTER_USER_TO_DELETE:
	                    if (tempIndex == USER_CODE_LENGTH) {
	                        tempIndex = 0;
	                        currentState = State.CONFIRM_DELETE_USER;
	                        return true;
	                    }
	                    return false;
	                case CONFIRM_DELETE_USER:
	                    if (tempIndex == USER_CODE_LENGTH) {
	                        if (deleteUserCode(tempUserCode)) {
	                            resetBuffer();
	                            currentState = State.IDLE;
	                            return true;
	                        }
	                        resetBuffer();
	                        currentState = State.IDLE;
	                        return false;
	                    }
	                    return false;
	                case ENTER_NEW_MASTER:
	                    if (tempIndex == MASTER_CODE_LENGTH) {
	                        tempIndex = 0;
	                        currentState = State.CONFIRM_NEW_MASTER;
	                        return true;
	                    }
	                    return false;
	                case CONFIRM_NEW_MASTER:
	                    if (tempIndex == MASTER_CODE_LENGTH) {
	                        masterCode = tempUserCode.clone();
	                        isChangedMasterCode = true;
	                        resetBuffer();
	                        currentState = State.IDLE;
	                        return true;
	                    }
	                    return false;
	                default:
	                    return false;
	            }
	        }

	        return false;
	    }
	
	public boolean addedUserCode() {
		return isNewUserCode;
	}

	public boolean deletedUserCode() {
		return isDeletedUserCode;
	}

	public boolean deletedAllUserCodes() {
		return areAllUserCodesDeleted;
	}

	public boolean changedMasterCode() {
		return isChangedMasterCode;
	}

	public int[] getMasterCode() {
		return masterCode.clone();
	}

}