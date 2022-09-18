package GraphicPresentor.Screens.thirdScreen.TrieDataSrtucture;

import java.util.*;

public class Trie {
    private TrieNode root;

    private class TrieNode{
        Map<String, TrieNode> children;
        String character;
        boolean isWord;

        TrieNode(String character, boolean isWord){
            children = new LinkedHashMap<>();
            this.isWord = isWord;
            this.character = character;
        }

    }

    public Trie(){
        root = new TrieNode("NA",false); //Set always with dummy root;
    }

    public void addWord(String word){
        TrieNode currentNode = root;
        int counter = 0;
        String currentCh;
        while(counter < word.length()){
            currentCh = String.valueOf(word.charAt(counter));
            if(!currentNode.children.containsKey(currentCh)){
                currentNode.children.put(currentCh, new TrieNode(currentCh, counter == word.length() -1));
            }
            currentNode = currentNode.children.get(currentCh);
            ++counter;
        }
        currentNode.isWord = true;
    }

    public List<String> getAllChildren(String word){
        List<String> children = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        String currentCh;
        TrieNode currentNode = root;
        for(int i = 0; i < word.length(); ++i){
            currentCh = String.valueOf(word.charAt(i));
            if(!currentNode.children.containsKey(currentCh)){
                return children;
            }
            builder.append(currentCh);
            currentNode = currentNode.children.get(currentCh);
        }
        if(word.length() != 0){
            builder.deleteCharAt(builder.toString().length() -1);
        }
        getAllChildrenHelper(children, builder, currentNode);
        return children;
    }

    private void getAllChildrenHelper(List<String> children, StringBuilder currenPrefix, TrieNode root){
        if(!root.character.equals(this.root.character))
            currenPrefix.append(root.character);
        if(root.isWord)
            children.add(currenPrefix.toString());

        if(root.children.isEmpty())
            return;

        root.children.forEach((ch, node) -> getAllChildrenHelper(children, new StringBuilder(currenPrefix), node));

    }
}
