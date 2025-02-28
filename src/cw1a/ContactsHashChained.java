package cw1a;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.ListIterator;

public class ContactsHashChained implements IContactDB {
    private final int initialTableCapacity = 991;
    private LinkedList<Contact>[] table;
    private int tableCapacity;
    private int numEntries;
    private int totalVisited = 0;
    private static final double maxLoadFactor = 50.0;

    public int getNumEntries(){return numEntries;}

    public void resetTotalVisited() {totalVisited = 0;}

    public int getTotalVisited() {return totalVisited;}

    public ContactsHashChained() {
        System.out.println("Hash Table with Chaining");
        this.tableCapacity = initialTableCapacity;
        table = new LinkedList[tableCapacity];
        clearDB();
    }

    public void clearDB() {
        // Initialize each chain as an empty linked list.
        for (int i = 0; i < tableCapacity; i++) {
            table[i] = new LinkedList<>();
        }
        numEntries = 0;
    }

    private int hash(String s) {

        int hashing_Value = 0;
        for (int i = 0; i < s.length(); i++) {
            hashing_Value = (hashing_Value << 17) ^ s.charAt(i);
        }
        return Math.abs(hashing_Value) % table.length;

    }

    public boolean containsName(String name) {
        return get(name) != null;
    }

    public Contact get(String name) {
        assert name != null && !name.trim().equals("");
        int index = hash(name);
        LinkedList<Contact> chain = table[index];
        int visitedCount = 0;
        for (Contact c : chain) {
            visitedCount++;
            System.out.println("Visited bucket in chain at index " + index + ": " + c);
            if (c.getName().equals(name)) {
                System.out.println("Total buckets visited in search: " + visitedCount);
                totalVisited += visitedCount;
                return c;
            }
        }
        System.out.println("Total buckets visited in search (not found): " + visitedCount);
        totalVisited += visitedCount;
        return null;
    }

    public int size() {return numEntries; }


    public boolean isEmpty() {return numEntries == 0; }

    public Contact put(Contact contact) {
        assert contact != null;
        String name = contact.getName();
        assert name != null && !name.trim().equals("");
        int index = hash(name);
        LinkedList<Contact> chain = table[index];
        int visitedCount = 0;


        ListIterator<Contact> it = chain.listIterator();
        while (it.hasNext()) {
            Contact current = it.next();
            visitedCount++;
            System.out.println("Visited bucket " + index + ": " + current);
            if (current.getName().equals(name)) {
                totalVisited += visitedCount;
                Contact old = current;
                it.set(contact);
                return old;
            }
        }

        chain.add(contact);
        numEntries++;
        totalVisited += visitedCount;
        return null;
    }

    @Override
    public Contact remove(String name) {
        return null;
    }

    public void displayDB() {
        // Display the current state of the hash table (chains per index).
        System.out.println("Hash Table With Chaining:");
        System.out.println("Capacity: " + tableCapacity + " Size: " + numEntries +
                " Load factor: " + String.format("%.2f%%", (numEntries * 100.0 / tableCapacity)));
        for (int i = 0; i < tableCapacity; i++) {
            System.out.print(i + ": ");
            LinkedList<Contact> chain = table[i];
            if (chain.isEmpty()) {
                System.out.println("_____");
            } else {
                for (Contact c : chain) {
                    System.out.print(c.toString() + " -> ");
                }
                System.out.println("null");
            }
        }

        // Optionally, display all contacts sorted in alphabetical order.
        Contact[] contactsArray = new Contact[numEntries];
        int j = 0;
        for (int i = 0; i < tableCapacity; i++) {
            for (Contact c : table[i]) {
                contactsArray[j++] = c;
            }
        }
        Arrays.sort(contactsArray, (a, b) -> a.getName().compareTo(b.getName()));
        System.out.println("Contacts in alphabetical order:");
        for (int i = 0; i < contactsArray.length; i++) {
            System.out.println(i + " " + contactsArray[i].toString());
        }
    }

    @Override
    public double loadFactor() {
        return (double) numEntries / table.length * 100.0;
    }


}
