package lock;

import java.util.Arrays;

public class Testing {
	private static void press(KeylessEntryLock lock, char... buttons) {
	    for (char b : buttons) {
	        lock.pushButton(b);
	    }
	}

	public static void main(String[] args) {
		
		KeyLock keyLock = new KeyLock(998);
		System.out.println("is it locked? " + keyLock.isLocked());
		System.out.println("\nis there a key inserted? " + keyLock.isInserted());
		System.out.println("\ninserting a correct key");
		
		keyLock.insertKey(998);
		System.out.println("\nis there a key inserted? " + keyLock.isInserted());
		System.out.println("\nunlocking the lock");
		keyLock.turn();
		System.out.println("\nis it locked? " + keyLock.isLocked());
		System.out.println("\nremoving the key");
		keyLock.removeKey();
		System.out.println("\nis there a key inserted? " + keyLock.isInserted());
		System.out.println("\ninserting an incorrect key");
		keyLock.insertKey(999);
		System.out.println("\nis there a key inserted? " + keyLock.isInserted() + "\n");
		
		
		System.out.println("------------------------");
		System.out.println("Combo lock test\n");
		ComboLock lock = new ComboLock(new int[] {9,11,17});
		
		System.out.println("is it locked? " + lock.isLocked());
		lock.turnRight(9);
		lock.turnLeft(12);
		lock.turnRight(17);
		
		System.out.println("\nis it locked? " + lock.isLocked());
		lock.lock();
		System.out.println("\nis it locked? " +lock.isLocked());
		lock.reset();
		
		lock.turnRight(9);
		lock.turnLeft(11);
		lock.turnRight(17);
		System.out.println("\nis it locked? " + lock.isLocked() + "\n");
		System.out.println("------------------------");
		
		System.out.println("KeylessEntryLock test");
		
		int[] masterCode = {1,2,3,4,5,6};
		KeylessEntryLock keylessLock = new KeylessEntryLock(999, masterCode);
		
		System.out.println("\n" + "Master Code: " + Arrays.toString(keylessLock.getMasterCode()));
		//unlocking with the master lock
		press(keylessLock, '1', '2', '3', '4', '5', '6');
		System.out.println("\n" + keylessLock.isLocked());
		
		System.out.println("checking if there's a user code added \n" + keylessLock.addedUserCode());
		
		System.out.println("\nadding a new user code");
		press(keylessLock, '1', '2', '3', '4', '5', '6', '*');
		press(keylessLock, '1');
		press(keylessLock, '4', '2', '0', '6');
		press(keylessLock, '4', '2', '0', '6');
		press(keylessLock, '*');
		System.out.println("\nrepeating twice for the next 2 tests");
		System.out.println("\nis there a user code? " + keylessLock.addedUserCode());
		press(keylessLock, '1', '2', '3', '4', '5', '6', '*');
		press(keylessLock, '1');
		press(keylessLock, '4', '2', '0', '6');
		press(keylessLock, '4', '2', '0', '6');
		press(keylessLock, '*');
		
		System.out.println("\nis there a user code? " + keylessLock.addedUserCode());
		press(keylessLock, '1', '2', '3', '4', '5', '6', '*');
		press(keylessLock, '1');
		press(keylessLock, '4', '2', '0', '7');
		press(keylessLock, '4', '2', '0', '7');
		press(keylessLock, '*');
		System.out.println("\nis there a user code? " + keylessLock.addedUserCode());
		
		System.out.println("\nunlocking with user code");
		keylessLock.lock();
		press(keylessLock, '4', '2', '0', '6');
		System.out.println("is it locked? " + keylessLock.isLocked());
		System.out.println("\ndeleting a user code");
		press(keylessLock, '1', '2', '3', '4', '5', '6', '*');
		press(keylessLock, '2');
		press(keylessLock, '4', '2', '0', '7');
		press(keylessLock, '4', '2', '0', '7');
		press(keylessLock, '*');
		System.out.println("is the user code deleted? " + keylessLock.deletedUserCode());
		System.out.println("\ndeleting all user codes");
		press(keylessLock, '1', '2', '3', '4', '5', '6', '*');
		press(keylessLock, '6');
		press(keylessLock, '1', '2', '3', '4', '5', '6');
		press(keylessLock, '*');
		System.out.println("are all user codes deleted? " + keylessLock.deletedAllUserCodes());
		//changing the master code
		press(keylessLock, '1', '2', '3', '4', '5', '6', '*');
		press(keylessLock, '3');
		press(keylessLock, '4','1','6','7','2','1');
		press(keylessLock, '4','1','6','7','2','1');
		press(keylessLock, '*');
		System.out.println("\n" + Arrays.toString(keylessLock.getMasterCode()));
		
		System.out.println("\ntesting if the key method works");
		keylessLock.lock();
		keylessLock.insertKey(999);
		keylessLock.turn();
		System.out.println("is it locked? " + keylessLock.isLocked());
		
		
		
		
		//unlocking with the user lock

	}

}
