package Engine.DM;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CalculationsUtils {
    static public long factorial(int k){
        long permutationNum = 1;
        for(; k > 0; permutationNum *= k, k--);
        return permutationNum;
    }

    static public long NumOfPermutations_k_of_n(int n, int k){
        return factorial(n)/ factorial(n-k);
    }

    static public List<List<Integer>> allPermutationOfNElements(List<Integer> elements){
        List<List<Integer>> permutations = new ArrayList<>();
        List<Integer> indexes = new ArrayList<>(Collections.nCopies(elements.size(), 0));
        permutations.add(new ArrayList<>(elements));

        int i = 0;
        while(i < elements.size()){
            if(indexes.get(i) < i){
                Collections.swap(elements, i %2 == 0 ? 0 : indexes.get(i), i);
                permutations.add(new ArrayList<>(elements));
                indexes.set(i, indexes.get(i)+ 1);
                i = 0;
            }else {
                indexes.set(i, 0);
                ++i;
            }
        }
        return permutations;
    }





}
