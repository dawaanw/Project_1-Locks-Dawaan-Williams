package lock;

public class Testing {

	public static void main(String[] args) {
		ComboLock lock = new ComboLock(new int[] {9,11,17});
		
		System.out.println(lock.isLocked());
		lock.turnRight(10);
		lock.turnLeft(20);
		lock.turnRight(30);
		
		if(lock.unlock()) {
			System.out.println("Unlocked");
		} else {
			System.out.println("Locked");
		}
		lock.lock();
		System.out.println(lock.isLocked());

	}

}
