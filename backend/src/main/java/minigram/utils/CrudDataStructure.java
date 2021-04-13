package minigram.utils;
/* Java program to design a data structure that support folloiwng operations
   in Theta(n) time
   a) Insert
   b) Delete
   c) Search
   d) getRandom */
import java.util.*;

public class CrudDataStructure {

    public ArrayList<String> arr;   // A resizable array

    // A hash where keys are array elements and values are
    // indexes in arr[]
    HashMap<String, Integer>  hash;

    // Constructor (creates arr[] and hash)
    public CrudDataStructure()
    {
        arr = new ArrayList<String>();
        hash = new HashMap<String, Integer>();
    }

    public CrudDataStructure(String[] array)
    {
        arr = new ArrayList<String>(Arrays.asList(array));
        hash = new HashMap<String, Integer>();
    }

    // A Theta(1) function to add an element to MyDS
    // data structure
    public void add(String x)
    {
        // If element is already present, then noting to do
        if (hash.get(x) != null)
            return;

        // Else put element at the end of arr[]
        int s = arr.size();
        arr.add(x);

        // And put in hash also
        hash.put(x, s);
    }

    // A Theta(1) function to remove an element from MyDS
    // data structure
    public void remove(String x)
    {
        // Check if element is present
        Integer index = hash.get(x);
        if (index == null)
            return;

        // If present, then remove element from hash
        hash.remove(x);

        // Swap element with last element so that remove from
        // arr[] can be done in O(1) time
        int size = arr.size();
        String last = arr.get(size-1);
        Collections.swap(arr, index,  size-1);

        // Remove last element (This is O(1))
        arr.remove(size-1);

        // Update hash table for new index of last element
        hash.put(last, index);
    }

    // Returns a random element from MyDS
    String getRandom()
    {
        // Find a random index from 0 to size - 1
        Random rand = new Random();  // Choose a different seed
        int index = rand.nextInt(arr.size());

        // Return element at randomly picked index
        return arr.get(index);
    }

    // Returns index of element if element is present, otherwise null
    public Integer search(String x)
    {
        return hash.get(x);
    }

}
