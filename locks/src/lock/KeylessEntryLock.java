package lock;

import java.util.Arrays;

public class KeylessEntryLock extends KeyLock implements Lock {

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
        IDLE, ENTER_MASTER, SELECT_OP,
        ENTER_NEW_USER, CONFIRM_NEW_USER,
        ENTER_USER_TO_DELETE, CONFIRM_DELETE_USER,
        ENTER_NEW_MASTER, CONFIRM_NEW_MASTER,
        CONFIRM_DELETE_ALL
    }

    private State currentState = State.IDLE;
    private int[] buffer; 
    private int bufferIndex = 0;

    private int[] tempCodeStorage;
    private int selectedOperation = 0;
    private boolean keypadUnlocked = false;

    public KeylessEntryLock(int keyValue, int[] initialMasterCode) {
        super(keyValue);
        if (initialMasterCode == null || initialMasterCode.length != MASTER_CODE_LENGTH) {
            throw new IllegalArgumentException("Master code must be 6 digits");
        }
        this.masterCode = initialMasterCode.clone();
        this.userCodes = new int[MAX_NUM_USER_CODES][];
        this.attempt = new int[MASTER_CODE_LENGTH];
        this.buffer = new int[MASTER_CODE_LENGTH];
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
        if (input.length != USER_CODE_LENGTH) return false;
        for (int[] userCode : userCodes) {
            if (userCode != null && Arrays.equals(userCode, input)) {
                return true;
            }
        }
        return false;
    }

    private void resetState() {
        if (bufferIndex > 0) {
            attempt = Arrays.copyOf(buffer, bufferIndex);
        }
        Arrays.fill(buffer, 0);
        bufferIndex = 0;
        tempCodeStorage = null;
        selectedOperation = 0;
        currentState = State.IDLE;
        isReset = true;
    }

    private boolean addUserCode(int[] code) {
        for (int i = 0; i < MAX_NUM_USER_CODES; i++) {
            if (userCodes[i] == null) {
                userCodes[i] = code.clone();
                isNewUserCode = true;
                return true;
            }
        }
        return false;
    }

    private boolean deleteUserCode(int[] code) {
        for (int i = 0; i < MAX_NUM_USER_CODES; i++) {
            if (userCodes[i] != null && Arrays.equals(userCodes[i], code)) {
                userCodes[i] = null;
                isDeletedUserCode = true;
                return true;
            }
        }
        return false;
    }

    private void deleteAllUserCodes() {
        this.userCodes = new int[MAX_NUM_USER_CODES][];
        areAllUserCodesDeleted = true;
        resetState();
    }


    public boolean pushButton(char button) {
        if (button == '*') {
            if (currentState == State.ENTER_MASTER && bufferIndex == MASTER_CODE_LENGTH && checkMasterCode(buffer)) {
                keypadUnlocked = false;
                currentState = State.SELECT_OP;
                Arrays.fill(buffer, 0);
                bufferIndex = 0;
                return true;
            }
            resetState();
            keypadUnlocked = false;
            return false;
        }

        if (!Character.isDigit(button)) {
            return false; 
        }

        int digit = Character.getNumericValue(button);


        switch (currentState) {
            case IDLE:
            case ENTER_MASTER:
                if (bufferIndex == MASTER_CODE_LENGTH) {
                    resetState();
                    keypadUnlocked = false;
                }

                buffer[bufferIndex++] = digit;
                currentState = State.ENTER_MASTER;

                if (bufferIndex == USER_CODE_LENGTH) {
                    int[] currentAttempt = Arrays.copyOf(buffer, USER_CODE_LENGTH);
                    if (checkUserCode(currentAttempt)) {
                        keypadUnlocked = true;
                        resetState();
                        break; 
                    }
                }

                if (bufferIndex == MASTER_CODE_LENGTH) {
                    if (checkMasterCode(buffer)) {
                        keypadUnlocked = true;
                    } else {
                        resetState();
                        keypadUnlocked = false;
                    }
                }
                break;

            case SELECT_OP:
                selectedOperation = digit;
                Arrays.fill(buffer, 0);
                bufferIndex = 0;
                switch (selectedOperation) {
                    case 1: currentState = State.ENTER_NEW_USER; break;
                    case 2: currentState = State.ENTER_USER_TO_DELETE; break;
                    case 3: currentState = State.ENTER_NEW_MASTER; break;
                    case 6: currentState = State.CONFIRM_DELETE_ALL; break; 
                    default: resetState();
                }
                break;

            case ENTER_NEW_USER:
                handleCodeEntry(digit, USER_CODE_LENGTH, State.CONFIRM_NEW_USER);
                break;
            case CONFIRM_NEW_USER:
                handleConfirmation(digit, USER_CODE_LENGTH, () -> {
                    addUserCode(tempCodeStorage);
                });
                break;

            case ENTER_USER_TO_DELETE:
                handleCodeEntry(digit, USER_CODE_LENGTH, State.CONFIRM_DELETE_USER);
                break;
            case CONFIRM_DELETE_USER:
                handleConfirmation(digit, USER_CODE_LENGTH, () -> {
                    deleteUserCode(tempCodeStorage);
                });
                break;

            case ENTER_NEW_MASTER:
                handleCodeEntry(digit, MASTER_CODE_LENGTH, State.CONFIRM_NEW_MASTER);
                break;
            case CONFIRM_NEW_MASTER:
                handleConfirmation(digit, MASTER_CODE_LENGTH, () -> {
                    masterCode = tempCodeStorage.clone();
                    isChangedMasterCode = true;
                });
                break;
            
            case CONFIRM_DELETE_ALL:
                 if (bufferIndex < MASTER_CODE_LENGTH) {
                    buffer[bufferIndex++] = digit;
                }
                if (bufferIndex == MASTER_CODE_LENGTH) {
                    if (checkMasterCode(buffer)) {
                        deleteAllUserCodes();
                    } else {
                        resetState(); 
                    }
                }
                break;
        }
        return true;
    }
    

    private void handleCodeEntry(int digit, int codeLength, State nextState) {
        if (bufferIndex < codeLength) {
            buffer[bufferIndex++] = digit;
        }
        if (bufferIndex == codeLength) {
            tempCodeStorage = Arrays.copyOf(buffer, codeLength);
            currentState = nextState;
            Arrays.fill(buffer, 0);
            bufferIndex = 0;
        }
    }

    private void handleConfirmation(int digit, int codeLength, Runnable action) {
        if (bufferIndex < codeLength) {
            buffer[bufferIndex++] = digit;
        }
        if (bufferIndex == codeLength) {

            int[] confirmationCode = Arrays.copyOf(buffer, codeLength);
            if (Arrays.equals(tempCodeStorage, confirmationCode)) {
                action.run();
            }
            resetState(); 
        }
    }

    public boolean addedUserCode() {
        boolean result = isNewUserCode;
        isNewUserCode = false;
        return result;
    }

    public boolean deletedUserCode() {
        boolean result = isDeletedUserCode;
        isDeletedUserCode = false; 
        return result;
    }

    public boolean deletedAllUserCodes() {
        boolean result = areAllUserCodesDeleted;
        areAllUserCodesDeleted = false; 
        return result;
    }

    public boolean changedMasterCode() {
        boolean result = isChangedMasterCode;
        isChangedMasterCode = false; 
        return result;
    }

    public int[] getMasterCode() {
        return masterCode.clone();
    }

    public int[] getLastAttempt() {
        return attempt.clone();
    }

    public boolean isReset() {
        boolean result = this.isReset;
        this.isReset = false; 
        return result;
    }

    @Override
    public boolean isLocked() {
        return super.isLocked() && !keypadUnlocked;
    }
    
    @Override
    public boolean lock() {
        keypadUnlocked = false;
        return super.lock();
    }

    @Override
    public boolean unlock() {
        keypadUnlocked = false;
        return super.unlock();
    }
}



